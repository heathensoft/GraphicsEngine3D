package no.fredahl.engine.math;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class Plane {

    private final Vector3f pointOnPlane;
    private final Vector3f normal;
    private float d;
    
    private static final Vector3f tmpV3f_0 = new Vector3f();
    private static final Vector3f tmpV3f_1 = new Vector3f();
    
    public enum Side {
        OnPlane,
        Back,
        Front
    }
    
    public Plane(Vector3f normal, Vector3f point) {
        this.normal = normal.normalize();
        this.pointOnPlane = point;
        this.d = -this.normal.dot(point);
    }
    
    public Vector3f pointOfIntersection(Ray ray) {
        tmpV3f_0.set(ray.origin).sub(pointOnPlane);
        float dot = tmpV3f_0.dot(normal) / ray.direction.dot(normal);
        tmpV3f_0.set(ray.direction).mul(dot);
        return tmpV3f_1.set(ray.origin).sub(tmpV3f_0);
    }

    public float distance(Vector3f point) {
        return normal.dot(point) + d;
    }
    
    public Side testPoint (Vector3f point) {
        float dist = normal.dot(point) + d;
        if (dist == 0)
            return Side.OnPlane;
        else if (dist < 0)
            return Side.Back;
        else
            return Side.Front;
    }
    
    public boolean isFrontFacing (Vector3f direction) {
        float dot = normal.dot(direction);
        return dot <= 0;
    }
    
    public void set (Vector3f point, Vector3f normal) {
        this.normal.set(normal.normalize());
        this.pointOnPlane.set(point);
        this.d = -point.dot(normal);
    }
    
    public Vector3f normal() {
        return normal;
    }
    
    public float getD() {
        return d;
    }
}
