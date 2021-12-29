package no.fredahl.engine.graphics.lighting;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Frederik Dahl
 * 27/12/2021
 */


public class ShadowCast {
    
    private final static Matrix4f biasMatrix = new Matrix4f();
    private final static Matrix4f tmpM4f0 = new Matrix4f();
    private final static Matrix4f tmpM4f1 = new Matrix4f();
    private final static Vector4f tmpV4f = new Vector4f();
    private final static Vector3f tmpV3f = new Vector3f();
    private final static Vector3f frustumCenter = new Vector3f();
    private final static Vector3f up = new Vector3f(0,1,0);
    private final static Vector4f[] frustumCorners = new Vector4f[8];
    
    static {
        for (int i = 0; i < 8; i++) frustumCorners[i] = new Vector4f();
        biasMatrix.translate(0.5f,0.5f,0.5f).scale(0.5f);
    }
    
    public static void calcLightViewPerspective(Vector3f lightPos, Vector3f lightDir, Matrix4f dest) {
        tmpV3f.set(lightPos).add(lightDir);
        dest.identity().lookAt(lightPos,tmpV3f,up);
    }
    
    public static void calcLightProjViewPerspective(Matrix4f lightProj, Matrix4f lightView, Matrix4f dest) {
        calcLightProjView(lightProj, lightView, dest, false);
    }
    
    public static void calcLightProjViewPerspective(Matrix4f lightProj, Matrix4f lightView, Matrix4f dest, boolean useBiasMatrix) {
        calcLightProjView(lightProj, lightView, dest, useBiasMatrix);
    }
    
    public static void calcLightProjViewPerspective(Matrix4f lightProj, Vector3f lightPos, Vector3f lightDir, Matrix4f dest) {
        calcLightViewPerspective(lightPos,lightDir,tmpM4f0);
        calcLightProjViewPerspective(lightProj,tmpM4f0,dest,false);
    }
    
    public static void calcLightProjViewPerspective(Matrix4f lightProj, Vector3f lightPos, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        calcLightViewPerspective(lightPos,lightDir,tmpM4f0);
        calcLightProjViewPerspective(lightProj,tmpM4f0,dest,useBiasMatrix);
    }
    
    public static void calcLightViewOrtho(Matrix4f projView, Vector3f lightDir, Matrix4f dest) {
        frustumCenter.zero();
        int i = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    Vector4f c = frustumCorners[i++];
                    c.set(2*x-1,2*y-1,2*z-1,1).mulProject(projView);
                    frustumCenter.add(c.x,c.y,c.z);
                }
            }
        }
        frustumCenter.div(8);
        tmpV3f.set(frustumCenter).sub(lightDir);
        dest.identity().lookAt(tmpV3f,frustumCenter,up);
    }
    
    public static void calcLightViewOrtho(Matrix4f proj, Matrix4f view, Vector3f lightDir, Matrix4f dest) {
        calcLightViewOrtho(tmpM4f0.set(proj).mul(view).invert(),lightDir,dest);
    }

    public static void calcLightProjOrtho(Matrix4f lightView, Matrix4f dest) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;
    
        for (int i = 0; i < 8; i++) {
            tmpV4f.set(frustumCorners[i]);
            tmpV4f.mulProject(lightView);
            minX = Math.min(tmpV4f.x, minX);
            minY = Math.min(tmpV4f.y, minY);
            minZ = Math.min(tmpV4f.z, minZ);
            maxX = Math.max(tmpV4f.x, maxX);
            maxY = Math.max(tmpV4f.y, maxY);
            maxZ = Math.max(tmpV4f.z, maxZ);
        }
        dest.setOrtho(minX,maxX,minY,maxY,minZ,maxZ);
    }
    
    public static void calcLightProjView(Matrix4f lightProj, Matrix4f lightView, Matrix4f dest, boolean useBiasMatrix) {
        if (useBiasMatrix) dest.set(biasMatrix).mul(lightProj).mul(lightView);
        else dest.set(lightProj).mul(lightView);
    }
    
    public static void calcLightProjViewOrtho(Matrix4f proj, Matrix4f view, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        calcLightProjViewOrtho(tmpM4f0.set(proj).mul(view).invert(),lightDir,dest, useBiasMatrix);
    }
    
    public static void calcLightProjViewOrtho(Matrix4f projView, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        calcLightViewOrtho(projView,lightDir,tmpM4f1);
        calcLightProjOrtho(tmpM4f1,tmpM4f0);
        calcLightProjView(tmpM4f0,tmpM4f1,dest,useBiasMatrix);
    }
}
