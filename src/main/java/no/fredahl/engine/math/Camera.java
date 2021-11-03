package no.fredahl.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Frederik Dahl
 * 03/11/2021
 */


public class Camera {
    
    private final static Vector3f tmpV3f = new Vector3f();
    private final static Vector4f tmpV4f = new Vector4f();
    private final static Matrix4f tmpM4f = new Matrix4f();
    private final static Ray tmpRay = new Ray();
    
    public final Matrix4f worldToViewInverse;
    public final Matrix4f projectionInverse;
    public final Matrix4f worldToView;
    public final Matrix4f projection;
    
    public final Vector3f position;
    public final Vector3f direction;
    public final Vector3f up;
    
    public float fov;
    public float far;
    public float near;
    public float aspectRatio;
    
    
    public Camera(float aspectRatio) {
        this.near = 0.01f;
        this.far = 1000.0f;
        this.fov = (float) Math.toRadians(60);
        this.position = new Vector3f(0,0,0);
        this.direction = new Vector3f(0,0,-1);
        this.up = new Vector3f(0,1,0);
        this.worldToViewInverse = new Matrix4f();
        this.projectionInverse = new Matrix4f();
        this.worldToView = new Matrix4f();
        this.projection = new Matrix4f();
        this.aspectRatio = aspectRatio;
    }
    
    
    public void updateProjection() {
        projection.identity().setPerspective(
                fov, aspectRatio,
                Math.abs(near),
                Math.abs(far),
                false);
        projectionInverse.set(projection);
        projectionInverse.invert();
    }
    
    public void updateWorldToView() {
        tmpV3f.set(position).add(direction);
        worldToView.identity().lookAt(position,tmpV3f,up);
        worldToViewInverse.set(worldToView).invert();
    }
    
    public void rotate(float deltaX, float deltaY) {
        tmpV3f.set(direction).cross(up);
        tmpM4f.identity().rotateY(deltaX).rotate(deltaY,tmpV3f);
        direction.mulProject(tmpM4f);
        up.mulProject(tmpM4f);
    }
    
    public void rotate(float angle, Vector3f axis) {
        direction.rotateAxis(angle,axis.x,axis.y,axis.z);
        up.rotateAxis(angle,axis.x,axis.y,axis.z);
    }
    
    public void rotate(Matrix4f rotationMatrix) {
        direction.mulProject(rotationMatrix);
        up.mulProject(rotationMatrix);
    }
    
    public void lookAt(Vector3f point) {
        tmpV3f.set(point).sub(position).normalize();
        if (tmpV3f.x != 0 && tmpV3f.y != 0 && tmpV3f.z != 0) {
            float dot = tmpV3f.dot(up);
            if (Math.abs(dot - 1f) < 0.000000001f) {
                up.set(direction).mul(-1);
            }
            else if (Math.abs(dot + 1f) < 0.000000001f) {
                up.set(direction);
            }
            direction.set(tmpV3f);
            tmpV3f.set(direction).cross(up);
            up.set(tmpV3f).cross(direction).normalize();
        }
    }
    
    public Ray getCameraRay() {
        tmpRay.direction.set(direction);
        tmpRay.origin.set(position);
        return tmpRay;
    }
    
    /**
     *
     * @param ndcX normalized device coordinates x
     * @param ndcY normalized device coordinates y
     * @return a Ray with camera position as origin
     */
    public Ray getPickingRay(float ndcX, float ndcY) {
        tmpV4f.set(ndcX,ndcY,-1.0f,1.0f);
        tmpV4f.mul(projectionInverse);
        tmpV4f.z = -1.0f;
        tmpV4f.w = 0.0f;
        tmpV4f.mul(worldToViewInverse);
        tmpRay.origin.set(position);
        tmpRay.direction.set(tmpV4f.x,tmpV4f.y,tmpV4f.z).normalize();
        return tmpRay;
        
    }
    
}
