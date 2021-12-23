package no.fredahl.engine.math;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 21/12/2021
 */


public class Culling {
    
    private static final Matrix4f tmpV4f = new Matrix4f();
    
    private final FrustumIntersection intersection;
    
    
    public Culling() {
        this.intersection = new FrustumIntersection();
    }
    
    public void updateFrustum(ICamera camera) {
        updateFrustum(camera.combined());
    }
    
    public void updateFrustum(Matrix4f projection, Matrix4f view) {
        updateFrustum(tmpV4f.set(projection).mul(view));
    }
    
    public void updateFrustum(Matrix4f projectionView) {
        intersection.set(projectionView);
    }
    
    public boolean insideFrustum(Vector3f point, float boundingRadius) {
        return intersection.testSphere(point,boundingRadius);
    }
    
    public boolean insideFrustum(Vector3f point) {
        return intersection.testPoint(point);
    }
}
