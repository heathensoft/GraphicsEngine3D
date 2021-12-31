package no.fredahl.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Frederik Dahl
 * 31/12/2021
 */


public class Frustum {
    
    private final FrustumFilter filter;
    private final Matrix4f projection;
    private final Matrix4f combined;
    private final Matrix4f view;
    boolean dirty = true;
    boolean allowTestSpheres;
    
    public Frustum(Matrix4f projection, Matrix4f view, boolean allowTestSpheres) {
        this.allowTestSpheres = allowTestSpheres;
        this.filter = new FrustumFilter();
        this.combined = new Matrix4f();
        this.projection = projection;
        this.view = view;
        updateFrustum();
    }
    
    public Frustum(Matrix4f projection, Matrix4f view) {
        this(projection,view,true);
    }
    
    public Frustum() {
        this(new Matrix4f(),new Matrix4f());
    }
    
    public void lookAt(Vector3f eye, Vector3f center, Vector3f up) {
        view.identity().lookAt(eye,center,up);
        dirty = true;
    }
    
    public void lookAt(float eyeX, float eyeY, float eyeZ,
                       float centerX, float centerY, float centerZ,
                       float upX, float upY, float upZ) {
        view.identity().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        dirty = true;
    }
    
    public void setPerspective(float fov, float aspect, float near, float far) {
        projection.setPerspective(fov, aspect, near, far);
        dirty = true;
    }
    
    public void setOrtho(float left, float right, float bottom, float top, float zNear, float zFar) {
        projection.setOrtho(left, right, bottom, top, zNear, zFar);
        dirty = true;
    }
    
    public void updateFrustum() {
        if (dirty) {
            combined.set(projection).mul(view);
            filter.setFrustum(combined,allowTestSpheres);
            dirty = false;
        }
    }
    
    public void filter(ArrayList<Cullable> source, ArrayList<Cullable> dest) {
        filter.filter(source,dest);
    }
    
    public boolean insideFrustum(Cullable cullable) {
        return filter.insideFrustum(cullable);
    }
    
    public Matrix4f view() {
        return view;
    }
    
    public Matrix4f projection() {
        return projection;
    }
    
    public Matrix4f combined() {
        return combined;
    }
    
    public void allowTestSpheres(boolean on) {
        this.allowTestSpheres = on;
    }
}
