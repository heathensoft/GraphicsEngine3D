package no.fredahl.engine.math;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.primitives.Rayf;

/**
 * @author Frederik Dahl
 * 09/01/2022
 */


public abstract class Camera {
    
    protected final FrustumIntersection frustumIntersection = new FrustumIntersection();
    
    protected Matrix4f projection = new Matrix4f();
    protected Matrix4f view = new Matrix4f();
    
    protected Vector3f position = new Vector3f();
    protected Vector3f direction = new Vector3f();
    protected Vector3f up = new Vector3f(0,1,0);
    
    protected float aspectRatio;
    protected float fieldOfView;
    protected float nearPlane;
    protected float farPlane;
    
    
    
    public abstract void updateProjection();
    
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
    
    public abstract Matrix4f projectionINV();
    
    public Matrix4f view() {
        return view;
    }
    
    public abstract Matrix4f viewINV();
    
    public abstract Matrix4f combined();
    
    public abstract Matrix4f combinedINV();
    
    public Vector3f position() {
        return position;
    }
    
    public Vector3f direction() {
        return direction;
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
