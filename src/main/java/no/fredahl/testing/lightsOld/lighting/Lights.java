package no.fredahl.testing.lightsOld.lighting;

import no.fredahl.engine.graphics.BufferObject;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

/**
 *
 * Lighting in the engine is uploaded as STD140 format data, to the shader in one uniform buffer object.
 * Scroll down in the file to see the structs and uniform-block strings
 * There is a limit to how many lights can be in a scene, so im using arraylists
 * Adding lights over set capacity simply won't be added to the lists
 *
 *
 * @author Frederik Dahl
 * 18/12/2021
 */


public class Lights {
    
    private final List<DirectionalLight> directionalLights;
    private final List<PointLight> pointLights;
    private final List<SpotLight> spotLights;
    
    private final int directionalLightCapacity;
    private final int pointLightCapacity;
    private final int spotLightCapacity;
    
    private final LightBlock uniformBlock;
    
    public Lights(int dirLightCap, int pointLightCap, int spotLightCap, int bindingPoint) {
        this.uniformBlock = new LightBlock(dirLightCap, pointLightCap, spotLightCap, bindingPoint);
        this.directionalLights = new ArrayList<>(dirLightCap);
        this.pointLights = new ArrayList<>(pointLightCap);
        this.spotLights = new ArrayList<>(spotLightCap);
        this.directionalLightCapacity = dirLightCap;
        this.pointLightCapacity = pointLightCap;
        this.spotLightCapacity = spotLightCap;
    }
    
    public void uploadUniforms(Matrix4f worldToViewMatrix) {
        uniformBlock.uniformBuffer.bind();
        uniformBlock.uploadDirectionalLights(worldToViewMatrix,directionalLights);
        uniformBlock.uploadPointLights(worldToViewMatrix,pointLights);
        uniformBlock.uploadSpotLights(worldToViewMatrix,spotLights);
        uniformBlock.refreshCount();
    }
    
    public void addDirectionalLight(DirectionalLight light) {
        if (directionalLights.size() < directionalLightCapacity)
            directionalLights.add(light);
    }
    
    public void addPointLight(PointLight light) {
        if (pointLights.size() < pointLightCapacity)
            pointLights.add(light);
    }
    
    public void addSpotLight(SpotLight light) {
        if (spotLights.size() < spotLightCapacity)
            spotLights.add(light);
    }
    
    public List<SpotLight> spotLights() {
        return spotLights;
    }
    
    public List<PointLight> pointLights() {
        return pointLights;
    }
    
    public List<DirectionalLight> directionalLights() {
        return directionalLights;
    }
    
    public void removeDirectionalLight(DirectionalLight light) {
        directionalLights.remove(light);
    }
    
    public void removePointLight(PointLight light) {
        pointLights.remove(light);
    }
    
    public void removeSpotLight(SpotLight light) {
        spotLights.remove(light);
    }
    
    public LightBlock uniformBlock() {
        return uniformBlock;
    }
    
    public String glslUniformBlock() {
    
        return "layout(std140, binding = " + uniformBlock.bindingPoint() + ") uniform Lights {\n" +
                               "    DirectionalLight dirLights["+ directionalLightCapacity +"];\n" +
                               "    PointLight pointLights["+ pointLightCapacity +"];\n" +
                               "    SpotLight spotLights["+ spotLightCapacity +"];\n" +
                               "    int num_dir_lights;\n" +
                               "    int num_point_lights;\n" +
                               "    int num_spot_lights;\n" +
                               "} ubo_lights;";
    }
    
    public String glslLighting() {
        
        return "struct DirectionalLight {\n" +
                       "    vec3 color;\n" +
                       "    float ambient;\n" +
                       "    vec3 direction;\n" +
                       "    float diffuse;\n" +
                       "};\n" +
                       "\n" +
                       "struct PointLight {\n" +
                       "    vec3 color;\n" +
                       "    float intensity;\n" +
                       "    vec3 position;\n" +
                       "    float ambient;\n" +
                       "    float diffuse;\n" +
                       "    float constant;\n" +
                       "    float linear;\n" +
                       "    float quadratic;\n" +
                       "};\n" +
                       "\n" +
                       "struct SpotLight {\n" +
                       "    vec3 color;\n" +
                       "    float ambient;\n" +
                       "    vec3 position;\n" +
                       "    float diffuse;\n" +
                       "    vec3 coneDir;\n" +
                       "    float constant;\n" +
                       "    float linear;\n" +
                       "    float quadratic;\n" +
                       "    float innerCutoff;\n" +
                       "    float outerCutoff;\n" +
                       "};\n" +
                       "\n" +
                       "vec3 a_source;\n" +
                       "vec3 d_source;\n" +
                       "vec3 s_source;\n" +
                       "vec3 e_source;\n" +
                       "\n" +
                       "float ec;\n" +
                       "float shine;\n" +
                       "float alpha;\n" +
                       "float shadow;\n" +
                       "\n" +
                       "vec3 a_sum = vec3(.0,.0,.0);\n" +
                       "vec3 d_sum = vec3(.0,.0,.0);\n" +
                       "vec3 s_sum = vec3(.0,.0,.0);\n" +
                       "vec3 e_sum = vec3(.0,.0,.0);\n" +
                       "\n" +
                       "\n" +
                       "float energyConservation(float shine) {\n" +
                       "    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );\n" +
                       "}\n" +
                       "\n" +
                       "float calc_shadow(vec4 pos, vec3 norm, vec3 dir, sampler2D shadowMap) {\n" +
                       "    vec3 coords = pos.xyz / pos.w;\n" +
                       "    float shadowFactor = 0.0;\n" +
                       "    float bias = max(0.05 * (1.0 - dot(norm, dir)), 0.005);\n" +
                       "    vec2 inc = 1.0 / textureSize(shadowMap,0);\n" +
                       "    coords = coords * 0.5 + 0.5;\n" +
                       "    for(int r = -2; r <= 2; ++r){\n" +
                       "        for(int c = -2; c <= 2; ++c){\n" +
                       "            float textDepth = texture(shadowMap, coords.xy + vec2(r,c) * inc).r;\n" +
                       "            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;\n" +
                       "        }\n" +
                       "    }\n" +
                       "    shadowFactor /= 25.0;\n" +
                       "    if(coords.z > 1.0) {\n" +
                       "        shadowFactor = 0.0;\n" +
                       "    }\n" +
                       "    return (1 - shadowFactor);\n" +
                       "}\n" +
                       "\n" +
                       "float calc_shadow(vec4 pos, sampler2D shadowMap) {\n" +
                       "    vec3 coords = pos.xyz / pos.w;\n" +
                       "    float shadowFactor = 0.0;\n" +
                       "    float bias = 0.0001;\n" +
                       "    vec2 inc = 1.0 / textureSize(shadowMap,0);\n" +
                       "    coords = coords * 0.5 + 0.5;\n" +
                       "    for(int r = -2; r <= 2; ++r){\n" +
                       "        for(int c = -2; c <= 2; ++c){\n" +
                       "            float textDepth = texture(shadowMap, coords.xy + vec2(r,c) * inc).r;\n" +
                       "            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;\n" +
                       "        }\n" +
                       "    }\n" +
                       "    shadowFactor /= 25.0;\n" +
                       "    if(coords.z > 1.0) {\n" +
                       "        shadowFactor = 0.0;\n" +
                       "    }\n" +
                       "    return (1 - shadowFactor);\n" +
                       "}\n" +
                       "\n" +
                       "void calc_dirlight(DirectionalLight l, vec3 eye, vec3 norm) {\n" +
                       "    vec3 toLightDir = -normalize(l.direction);\n" +
                       "    float diff = max(dot(norm, toLightDir), 0.0);\n" +
                       "    vec3 halfwayDir = normalize(toLightDir + eye);\n" +
                       "    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;\n" +
                       "    a_sum += (l.color * a_source * l.ambient);\n" +
                       "    d_sum += (l.color * d_source * l.diffuse * diff);\n" +
                       "    s_sum += (l.color * s_source * spec);\n" +
                       "}\n" +
                       "\n" +
                       "void calc_pointlight(PointLight l, vec3 pos, vec3 eye, vec3 norm) {\n" +
                       "    vec3 lightVec = l.position - pos;\n" +
                       "    vec3 toLightDir = normalize(lightVec);\n" +
                       "    float diff = max(dot(norm, toLightDir), 0.0);\n" +
                       "    vec3 halfwayDir = normalize(toLightDir + eye);\n" +
                       "    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;\n" +
                       "    float d = length(lightVec);\n" +
                       "    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);\n" +
                       "    a_sum += (l.color * a_source * l.ambient) * att;\n" +
                       "    d_sum += (l.color * d_source * l.diffuse * diff) * att;\n" +
                       "    s_sum += (l.color * s_source * spec) * att;\n" +
                       "}\n" +
                       "\n" +
                       "void calc_spotlight(SpotLight l, vec3 pos, vec3 eye, vec3 norm) {\n" +
                       "    vec3 lightVec = l.position - pos;\n" +
                       "    vec3 toLightDir = normalize(lightVec);\n" +
                       "    float diff = max(dot(norm, toLightDir), 0.0);\n" +
                       "    vec3 halfwayDir = normalize(toLightDir + eye);\n" +
                       "    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;\n" +
                       "    float theta = dot(toLightDir, normalize(-l.coneDir));\n" +
                       "    float epsilon = (l.innerCutoff - l.outerCutoff);\n" +
                       "    float intensity = clamp((theta - l.outerCutoff) / epsilon, 0.0, 1.0);\n" +
                       "    float d = length(lightVec);\n" +
                       "    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);\n" +
                       "    a_sum += (l.color * a_source * l.ambient) * att * intensity ;\n" +
                       "    d_sum += (l.color * d_source * l.diffuse * diff) * att * intensity * shadow;\n" +
                       "    s_sum += (l.color * s_source * spec) * att * intensity * shadow;\n" +
                       "}";
    }
    
    public void free() {
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
        uniformBlock.free();
    }
    
    private static final class LightBlock {
        
        private final static DirectionalLight tmpDL = new DirectionalLight();
        private final static PointLight tmpPL = new PointLight();
        private final static SpotLight tmpSL = new SpotLight();
        private final static Vector4f tmpV4f = new Vector4f();
        
        private final BufferObject uniformBuffer;
        
        private final int bindingPoint;
        
        private final FloatBuffer dirLightBuffer;
        private final FloatBuffer pointLightBuffer;
        private final FloatBuffer spotLightBuffer;
        private final IntBuffer lightCountBuffer;
        
        private final int dirLightOffset;
        private final int pointLightOffset;
        private final int spotLightOffset;
        private final int countOffset;
        
        private int dirLightCount;
        private int pointLightCount;
        private int spotLightCount;
        
        private boolean refreshCount;
        
        
        private LightBlock(int dirLightCap, int pointLightCap, int spotLightCap, int bindingPoint) {
            
            this.bindingPoint = bindingPoint;
            this.dirLightBuffer = MemoryUtil.memAllocFloat(DirectionalLight.sizeInFloats(1));
            this.pointLightBuffer = MemoryUtil.memAllocFloat(PointLight.sizeInFloats(1));
            this.spotLightBuffer = MemoryUtil.memAllocFloat(SpotLight.sizeInFloats(1));
            this.lightCountBuffer = MemoryUtil.memAllocInt(3);
            
            final int dirLightsStride = DirectionalLight.sizeSTD140(dirLightCap);
            final int pointLightsStride = PointLight.sizeSTD140(pointLightCap);
            final int spotLightsStride = SpotLight.sizeSTD140(spotLightCap);
            final int countBufferSizeBytes = 3 * Integer.BYTES;
            
            this.dirLightOffset = 0;
            this.pointLightOffset = dirLightsStride;
            this.spotLightOffset = pointLightOffset + pointLightsStride;
            this.countOffset = spotLightOffset + spotLightsStride;
            int uboSize = countOffset + countBufferSizeBytes;
            
            this.uniformBuffer = new BufferObject(GL_UNIFORM_BUFFER,GL_DYNAMIC_DRAW);
            this.uniformBuffer.bind();
            this.uniformBuffer.bufferData(uboSize);
            this.uniformBuffer.bindBufferBase(bindingPoint);
            
            this.dirLightCount = 0;
            this.pointLightCount = 0;
            this.spotLightCount = 0;
            
            this.refreshCount = true;
        }
        
        public void uploadDirectionalLights(Matrix4f worldToViewMatrix, List<DirectionalLight> lights) {
            int count = 0;
            for (DirectionalLight light : lights) {
                tmpDL.set(light);
                tmpV4f.set(tmpDL.direction(),0.0f);
                tmpV4f.mul(worldToViewMatrix);
                tmpDL.setDirection(tmpV4f.x,tmpV4f.y,tmpV4f.z);
                dirLightBuffer.clear();
                tmpDL.getSTD140(dirLightBuffer);
                dirLightBuffer.flip();
                uniformBuffer.bufferSubData(dirLightBuffer,dirLightOffset + DirectionalLight.sizeSTD140(count));
                count++;
            } if (count != dirLightCount) {
                refreshCount = true;
                dirLightCount = count;
            }
        }
        
        public void uploadPointLights(Matrix4f worldToViewMatrix, List<PointLight> lights) {
            int count = 0;
            for (PointLight light : lights) {
                tmpPL.set(light);
                tmpV4f.set(tmpPL.position,1.0f);
                tmpV4f.mul(worldToViewMatrix);
                tmpPL.setPosition(tmpV4f.x,tmpV4f.y,tmpV4f.z);
                pointLightBuffer.clear();
                tmpPL.getSTD140(pointLightBuffer);
                pointLightBuffer.flip();
                uniformBuffer.bufferSubData(pointLightBuffer,pointLightOffset + PointLight.sizeSTD140(count));
                count++;
            } if (count != pointLightCount) {
                refreshCount = true;
                pointLightCount = count;
            }
        }
        
        public void uploadSpotLights(Matrix4f worldToViewMatrix, List<SpotLight> lights) {
            int count = 0;
            for (SpotLight light : lights) {
                tmpSL.set(light);
                tmpV4f.set(tmpSL.direction(),0.0f);
                tmpV4f.mul(worldToViewMatrix);
                tmpSL.setDirection(tmpV4f.x,tmpV4f.y,tmpV4f.z);
                tmpV4f.set(tmpSL.light().position,1.0f);
                tmpV4f.mul(worldToViewMatrix);
                tmpSL.light().setPosition(tmpV4f.x,tmpV4f.y,tmpV4f.z);
                spotLightBuffer.clear();
                tmpSL.getSTD140(spotLightBuffer);
                spotLightBuffer.flip();
                uniformBuffer.bufferSubData(spotLightBuffer,spotLightOffset + SpotLight.sizeSTD140(count));
                count++;
            } if (count != spotLightCount) {
                refreshCount = true;
                spotLightCount = count;
            }
        }
        
        public void refreshCount() {
            if (refreshCount) {
                lightCountBuffer.clear();
                lightCountBuffer.put(dirLightCount);
                lightCountBuffer.put(pointLightCount);
                lightCountBuffer.put(spotLightCount);
                lightCountBuffer.flip();
                uniformBuffer.bufferSubData(lightCountBuffer,countOffset);
                refreshCount = false;
            }
        }
        
        public int bindingPoint() {
            return bindingPoint;
        }
        
        public void free() {
            
            MemoryUtil.memFree(dirLightBuffer);
            MemoryUtil.memFree(pointLightBuffer);
            MemoryUtil.memFree(spotLightBuffer);
            MemoryUtil.memFree(lightCountBuffer);
            
            uniformBuffer.unbind_();
            uniformBuffer.free();
        }
    }
}
