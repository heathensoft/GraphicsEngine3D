package no.fredahl.engine.math;

import org.joml.FrustumIntersection;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.primitives.Rayf;

/**
 *
 * Utility class. Multipurpose Camera without "roll".
 * Direction, "pitch" and "yaw" is set by a function of position and a point of focus.
 * No quaternions. Avoiding "gimbal-lock" is up to the implementer.
 *
 * @author Frederik Dahl
 * 28/03/2022
 */


public abstract class Camera {
    
    protected final FrustumIntersection frustumIntersection = new FrustumIntersection();
    
    protected final Matrix4f view = new Matrix4f();
    protected final Matrix4f projection = new Matrix4f();
    protected final Matrix4f combined = new Matrix4f();
    protected final Matrix4f viewINV = new Matrix4f();
    protected final Matrix4f projectionINV = new Matrix4f();
    protected final Matrix4f combinedINV = new Matrix4f();
    
    protected final Vector3f position = new Vector3f();
    protected final Vector3f direction = new Vector3f();
    protected final Vector3f right = new Vector3f();
    protected final Vector3f up = new Vector3f(0,1,0);
    
    protected float fieldOfView = Math.toRadians(60);
    protected float aspectRatio = 16/9f;
    protected float nearPlane = 1.0f;
    protected float farPlane = 1000.0f;
    
    
    public void updateProjection() {
        projection.identity().setPerspective(
                fieldOfView, aspectRatio,
                Math.abs(nearPlane),
                Math.abs(farPlane),
                false);
        projectionINV.set(projection);
        projectionINV.invert();
        combined.set(projection).mul(view);
        combinedINV.set(combined).invert();
    }
    
    /**
     * Camera will turn around to face the point
     * @param point The point of focus
     */
    public void lookAt(Vector3f point) {
        direction.set(point).sub(position).normalize();
        right.set(direction).cross(MathLib.UP_VECTOR).normalize();
        up.set(right).cross(direction);
        view.identity().lookAt(position,point,up);
        viewINV.set(view).invert();
        combined.set(projection).mul(view);
        combinedINV.set(combined).invert();
    }
    
    public void frustum(FrustumIntersection dest) {
        dest.set(combined());
    }
    
    public FrustumIntersection frustum() {
        return frustumIntersection.set(combined());
    }
    
    public void potentialShadowCasters(FrustumIntersection dest, float border) {
        MathLib.lightSpace.psc(direction,position,fieldOfView,aspectRatio,nearPlane,farPlane,border,dest);
    }
    
    public void pickingRay(float ndcX, float ndcY, Rayf dest) {
        MathLib.rayCast.mouse(projectionINV(),viewINV(),position,ndcX,ndcY,dest);
    }
    
    public Rayf pickingRay(float ndcX, float ndcY) {
        return MathLib.rayCast.mouse(projectionINV(),viewINV(),position,ndcX,ndcY);
    }
    
    public void centerRay(Rayf dest) {
        MathLib.rayCast.mouse(projectionINV(),viewINV(),position,0,0,dest);
    }
    
    public Rayf centerRay() {
        return MathLib.rayCast.mouse(projectionINV(),viewINV(),position,0,0);
    }
    
    public Matrix4f projection() {
        return projection;
    }
    
    public Matrix4f projectionINV() {
        return projectionINV;
    }
    
    public Matrix4f view() {
        return view;
    }
    
    public Matrix4f viewINV() {
        return viewINV;
    }
    
    public Matrix4f combined() {
        return combined;
    }
    
    public Matrix4f combinedINV() {
        return combinedINV;
    }
    
    public Vector3f position() {
        return position;
    }
    
    public Vector3f direction() {
        return direction;
    }
    
    public Vector3f right() {
        return right;
    }
    
    public float aspectRatio() {
        return aspectRatio;
    }
    
    public float fieldOfView() {
        return fieldOfView;
    }
    
    public float nearPlane() {
        return nearPlane;
    }
    
    public float farPlane() {
        return farPlane;
    }
    
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }
    
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }
    
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
    
}
