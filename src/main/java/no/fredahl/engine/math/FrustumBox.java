package no.fredahl.engine.math;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.primitives.AABBf;

import java.util.ArrayList;

/**
 * Omni-directional (6 pyramids) frustum
 * Treated as a bounding-box on filtering
 *
 * @author Frederik Dahl
 * 31/12/2021
 */


public class FrustumBox {
    
    public static final int FRUSTUM_RIGHT   = 0;
    public static final int FRUSTUM_LEFT    = 1;
    public static final int FRUSTUM_TOP     = 2;
    public static final int FRUSTUM_BOTTOM  = 3;
    public static final int FRUSTUM_REAR    = 4;
    public static final int FRUSTUM_FRONT   = 5;
    
    public static final float FOV = Math.toRadians(90.0f);
    public static final float NEAR = 0.0f;
    public static final float ASPECT = 1.0f;
    
    private static final Matrix4f view = new Matrix4f();
    
    private final Matrix4f[] combined;
    private final Matrix4f projection;
    private final AABBf boundingBox;
    private final Vector3f center;
    
    private boolean dirty;
    private float far;
    
    
    public FrustumBox(Vector3f center, float far) {
        this.center = center;
        this.projection = new Matrix4f();
        this.boundingBox = new AABBf();
        this.combined = new Matrix4f[6];
        for (int i = 0; i < 6; i++) {
            combined[i] = new Matrix4f();
        }
        setFar(far);
    }
    
    public FrustumBox(Vector3f center) {
        this(center,1.0f);
    }
    
    public FrustumBox() {
        this(new Vector3f());
    }
    
    public void updateFrustum() {
        if (dirty) {
            final float x = center.x;
            final float y = center.y;
            final float z = center.z;
            view.identity().lookAt(x,y,z,x+1,y,z,0,-1,0);
            combined[FRUSTUM_RIGHT].set(projection).mul(view);
            view.identity().lookAt(x,y,z,x-1,y,z,0,-1,0);
            combined[FRUSTUM_LEFT].set(projection).mul(view);
            view.identity().lookAt(x,y,z,x,y+1,z,0,0,1);
            combined[FRUSTUM_TOP].set(projection).mul(view);
            view.identity().lookAt(x,y,z,x,y-1,z,0,0,-1);
            combined[FRUSTUM_BOTTOM].set(projection).mul(view);
            view.identity().lookAt(x,y,z,x,y,z+1,0,-1,0);
            combined[FRUSTUM_REAR].set(projection).mul(view);
            view.identity().lookAt(x,y,z,x,y,z-1,0,-1,0);
            combined[FRUSTUM_FRONT].set(projection).mul(view);
            boundingBox.setMax(x+far,y+far,z+far);
            boundingBox.setMin(x-far,y-far,z-far);
            dirty = false;
        }
    }
    
    public void setCenter(Vector3f center) {
        this.center.set(center);
        dirty = true;
    }
    
    public void setFar(float far) {
        projection.setPerspective(FOV,ASPECT,NEAR,far);
        this.far = far;
        dirty = true;
    }
    
    public boolean insideFrustum(Cullable cullable) {
        return cullable.insideAABB(boundingBox);
    }
    
    public Matrix4f projection() {
        updateFrustum();
        return projection;
    }
    
    public Matrix4f[] combined() {
        updateFrustum();
        return combined;
    }
    
}
