package no.fredahl.testing.lod.terrain;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.graphics.surface.DepthMap16;
import no.fredahl.engine.graphics.surface.HeightMap;
import no.fredahl.engine.graphics.surface.NormalMap;
import no.fredahl.engine.math.Camera;
import no.fredahl.engine.math.MathLib;
import no.fredahl.engine.utility.Disposable;
import no.fredahl.engine.utility.FileUtility;
import no.fredahl.engine.utility.FastNoiseLite;
import org.joml.FrustumIntersection;
import org.joml.Math;
import org.joml.Random;
import org.joml.Vector3f;
import org.joml.primitives.Planef;
import org.joml.primitives.Rayf;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 07/04/2022
 */


public class Terrain implements Disposable {
    
    private final static float PI = (float) Math.PI;
    private final static float ROOT_2 = Math.sqrt(2.0f);
    private final static int MAP_SIZE = MathLib.log2(4096);
    private final static int CHUNK_SIZE = MathLib.log2(128);
    
    private final static Planef PLANE = new Planef(
            new Vector3f(0,0,0),
            new Vector3f(0,1,0));
    
    private final FrustumIntersection frustum = new FrustumIntersection();
    private final ShaderProgram shaderProgram;
    private final Texture detailTexture;
    private final Texture terrainSpecular;
    private final Texture terrainPalette;
    private final Texture normalTexture;
    private final Texture heightTexture;
    private final TerrainBatch batch;
    private final TerrainQT quadTree;
    private final TerrainQT.Iterator itr = new TerrainQT.Iterator() {
        
        // This iterator is used within the QuadTree structure.
        // This is the final (and most expensive) stage of the terrain culling, where we cull
        // "chunks" outside the camera frustum, then draw the ones within.
        
        @Override
        public void pass(int cX, int cY, int size) {
            int sh = size / 2;
            int minX = cX - sh;
            int maxX = cX + sh;
            int minY = cY - sh;
            int maxY = cY + sh;
            if (frustum.testPlaneXZ(minX,minY,maxX,maxY)) {
                batch.draw(cX,cY,size,0);
            }
        }
    };
    
    // time accumulation, used for water effects in the shader stages
    private float time;
    
    public Terrain() throws Exception{
        
        batch = new TerrainBatch();
        quadTree = new TerrainQT(MAP_SIZE,CHUNK_SIZE,1200);
        
        // Noise setup
        FastNoiseLite fastNoiseLite = new FastNoiseLite(new Random().nextInt(9999999));
        fastNoiseLite.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        fastNoiseLite.SetFrequency(0.001f);
        fastNoiseLite.SetFractalType(FastNoiseLite.FractalType.FBm);
        fastNoiseLite.SetFractalOctaves(5);
        fastNoiseLite.SetFractalLacunarity(2f);
        fastNoiseLite.SetFractalGain(0.5f);
        fastNoiseLite.SetFractalPingPongStrength(2f);
        fastNoiseLite.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
        fastNoiseLite.SetDomainWarpAmp(740.0f);
        
        // Height map / Normal map
        float amplitude = 10f;
        HeightMap heightMap = new HeightMap(fastNoiseLite,4096,4096,amplitude,2f,false);
        DepthMap16 depthMap16 = new DepthMap16(heightMap);
        NormalMap normalMap = new NormalMap(depthMap16,amplitude);
        heightTexture = depthMap16.toTexture(GL_CLAMP_TO_EDGE,GL_NEAREST);
        normalTexture = normalMap.toTexture(GL_CLAMP_TO_EDGE,GL_NEAREST);
    
        // Other textures
        String palettePath = "testing/lod/terrain_height_palette64.png";
        terrainPalette = new Texture(GL_TEXTURE_1D);
        terrainPalette.bind();
        terrainPalette.filter(GL_LINEAR);
        terrainPalette.wrapS(GL_CLAMP_TO_EDGE);
        Image paletteIMG = FileUtility.resource.image(palettePath,170,true);
        terrainPalette.tex1D(paletteIMG);
        paletteIMG.free();
        terrainPalette.unbind();
        
        String specularPath = "testing/lod/terrain_shine_palette.png";
        terrainSpecular = new Texture(GL_TEXTURE_1D);
        terrainSpecular.bind();
        terrainSpecular.filter(GL_LINEAR);
        terrainSpecular.wrapS(GL_CLAMP_TO_EDGE);
        Image specularIMG = FileUtility.resource.image(specularPath,170,true);
        terrainSpecular.tex1D(specularIMG);
        specularIMG.free();
        terrainSpecular.unbind();
    
        String detailPath = "testing/lod/terrain_detail.png";
        detailTexture = new Texture(GL_TEXTURE_2D);
        detailTexture.bind();
        detailTexture.filter(GL_LINEAR);
        detailTexture.wrapST(GL_REPEAT);
        Image detailIMG = FileUtility.resource.image(detailPath,132000,true);
        detailTexture.tex2D(detailIMG);
        detailTexture.generateMipMap();
        detailIMG.free();
        
        // Shaders
        String vsPath = "testing/lod/terrain_vs.glsl";
        String fsPath = "testing/lod/terrain_fs.glsl";
        String waterPath = "testing/lod/water1.glsl";
        String vs_source = FileUtility.resource.toString(vsPath);
        String fs_source = FileUtility.resource.toString(fsPath);
        String water_source = FileUtility.resource.toString(waterPath);
        fs_source = ShaderProgram.insert(water_source,"#WATER",fs_source);
        shaderProgram = new ShaderProgram();
        shaderProgram.attach(fs_source,GL_FRAGMENT_SHADER);
        shaderProgram.attach(vs_source,GL_VERTEX_SHADER);
        shaderProgram.compile();
        shaderProgram.link();
        shaderProgram.bind();
        shaderProgram.createUniform("uTime");
        //shaderProgram.createUniform("uAmplitude");
        shaderProgram.createUniform("uCombined");
        shaderProgram.createUniform("uLightDir");
        shaderProgram.createUniform("uCameraPos");
        shaderProgram.createUniform("uNormalTexture");
        shaderProgram.createUniform("uHeightTexture");
        shaderProgram.createUniform("uTerrainPalette");
        shaderProgram.createUniform("uTerrainSpecular");
        shaderProgram.createUniform("uTerrainDetail");
        shaderProgram.setUniform("uLightDir",new Vector3f(-3f,-108f,-1f).normalize());
        //shaderProgram.setUniform1f("uAmplitude",amplitude);
        //glEnable(GL_DEPTH_TEST);
    }
    
    public void render(Camera camera) {
        
        // Upload shader uniforms
        
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        shaderProgram.bind();
        shaderProgram.setUniform("uCombined", camera.combined());
        shaderProgram.setUniform("uCameraPos", camera.position());
        shaderProgram.setUniform1f("uTime", time / 2f);
        shaderProgram.setUniform1i("uHeightTexture",0);
        heightTexture.bindAndSetActive(GL_TEXTURE0);
        shaderProgram.setUniform1i("uNormalTexture",1);
        normalTexture.bindAndSetActive(GL_TEXTURE1);
        shaderProgram.setUniform1i("uTerrainPalette",2);
        terrainPalette.bindAndSetActive(GL_TEXTURE2);
        shaderProgram.setUniform1i("uTerrainSpecular",3);
        terrainSpecular.bindAndSetActive(GL_TEXTURE3);
        shaderProgram.setUniform1i("uTerrainDetail",4);
        detailTexture.bindAndSetActive(GL_TEXTURE4);
        
        // The following code calculates the point where the camera focus intersects the plane,
        // then figures out an appropriate radius around that point where we cull the outside
        // terrain, before we to the more expensive, camera frustum culling.
        // Also: Level of detail is at maximum around that point of focus.
        // This should ideally be done separately from rendering code
        
        Vector3f camPos = camera.position();
        Vector3f inSect = MathLib.vec3();
        Rayf ray = MathLib.ray();
        camera.centerRay(ray);
        float r = 0; float x, z;
        if (MathLib.rayCast.intersectPlane(ray, PLANE,inSect)) {
            float C = camera.fieldOfView() / 2f;
            float cX = camPos.x;
            float cY = camPos.y;
            float cZ = camPos.z;
            float iX = inSect.x;
            float iY = inSect.y;
            float iZ = inSect.z;
            float a = Vector3f.distance(cX,cY,cZ,iX,iY,iZ);
            float B = PI - Math.asin(cY / a);
            float A = PI - B - C;
            r = a * Math.sin(C) / Math.sin(A);
            x = inSect.x;
            z = inSect.z;
        }  else {
            x = camPos.x;
            z = camPos.z;
        }
        r = Math.min(camera.farPlane(),r) + (camPos.y * ROOT_2);
        quadTree.setFar((int)r);
        frustum.set(camera.combined(),false);
        batch.begin();
        quadTree.query(itr,(int) x,(int) z);
        batch.end();
        shaderProgram.unBind();
    }
    
    
    @Override
    public void dispose() {
        heightTexture.dispose();
        normalTexture.dispose();
        terrainPalette.dispose();
        batch.dispose();
        if (shaderProgram != null)
            shaderProgram.dispose();
    }
    
    public void update(float delta) {
        time += delta;
    }
}
