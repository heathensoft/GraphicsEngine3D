package no.fredahl.engine.graphics.lighting;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Static utility class for calculating lighting matrices
 *
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
    
    public static void lightViewPerspective(Vector3f lightPos, Vector3f lightDir, Matrix4f dest) {
        tmpV3f.set(lightPos).add(lightDir);
        dest.identity().lookAt(lightPos,tmpV3f,up);
    }
    
    public static void lightCombinedPerspective(Matrix4f lightProjection, Matrix4f lightView, Matrix4f dest) {
        lightCombined(lightProjection, lightView, dest, false);
    }
    
    public static void lightCombinedPerspective(Matrix4f lightProjection, Matrix4f lightView, Matrix4f dest, boolean useBiasMatrix) {
        lightCombined(lightProjection, lightView, dest, useBiasMatrix);
    }
    
    public static void lightCombinedPerspective(Matrix4f lightProjection, Vector3f lightPos, Vector3f lightDir, Matrix4f dest) {
        lightViewPerspective(lightPos,lightDir,tmpM4f0);
        lightCombinedPerspective(lightProjection,tmpM4f0,dest,false);
    }
    
    public static void lightCombinedPerspective(Matrix4f lightProjection, Vector3f lightPos, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        lightViewPerspective(lightPos,lightDir,tmpM4f0);
        lightCombinedPerspective(lightProjection,tmpM4f0,dest,useBiasMatrix);
    }
    
    public static void lightViewOrtho(Matrix4f camCombined, Vector3f lightDir, Matrix4f dest) {
        frustumCenter.zero();
        int i = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    Vector4f c = frustumCorners[i++];
                    c.set(2*x-1,2*y-1,2*z-1,1).mulProject(camCombined);
                    frustumCenter.add(c.x,c.y,c.z);
                }
            }
        }
        frustumCenter.div(8);
        tmpV3f.set(frustumCenter).sub(lightDir);
        dest.identity().lookAt(tmpV3f,frustumCenter,up);
    }
    
    public static void lightViewOrtho(Matrix4f camProjection, Matrix4f camView, Vector3f lightDir, Matrix4f dest) {
        lightViewOrtho(tmpM4f0.set(camProjection).mul(camView).invert(),lightDir,dest);
    }

    public static void lightProjectionOrtho(Matrix4f lightView, Matrix4f dest) {
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
        } dest.setOrtho(minX,maxX,minY,maxY,minZ,maxZ);
    }
    
    public static void lightCombined(Matrix4f lightProjection, Matrix4f lightView, Matrix4f dest, boolean useBiasMatrix) {
        if (useBiasMatrix) dest.set(biasMatrix).mul(lightProjection).mul(lightView);
        else dest.set(lightProjection).mul(lightView);
    }
    
    public static void lightCombined(Matrix4f lightProjection, Matrix4f lightView, Matrix4f dest) {
        lightCombined(lightProjection,lightView,dest,false);
    }
    
    public static void lightCombinedOrtho(Matrix4f camProjection, Matrix4f camView, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        lightCombinedOrtho(tmpM4f0.set(camProjection).mul(camView).invert(),lightDir,dest, useBiasMatrix);
    }
    
    public static void lightCombinedOrtho(Matrix4f camProjection, Matrix4f camView, Vector3f lightDir, Matrix4f dest) {
        lightCombinedOrtho(camProjection, camView, lightDir, dest,false);
    }
    
    public static void lightCombinedOrtho(Matrix4f camCombined, Vector3f lightDir, Matrix4f dest, boolean useBiasMatrix) {
        lightViewOrtho(camCombined,lightDir,tmpM4f1);
        lightProjectionOrtho(tmpM4f1,tmpM4f0);
        lightCombined(tmpM4f0,tmpM4f1,dest,useBiasMatrix);
    }
    
    public static void lightCombinedOrtho(Matrix4f camCombined, Vector3f lightDir, Matrix4f dest) {
        lightCombinedOrtho(camCombined, lightDir, dest,false);
    }
}
