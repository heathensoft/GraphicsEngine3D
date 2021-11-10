package no.fredahl.engine.math;

import org.joml.Matrix4f;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/11/2021
 */


public class Ray {
    
    private final static Vector3f tmpV3f = new Vector3f();
    public final Vector3f direction = new Vector3f();
    public final Vector3f origin = new Vector3f();
    
    
    public Vector3f getPoint(final Vector3f dest, final float distance) {
        return dest.set(direction).mul(distance).add(origin);
    }
    
    public Vector3f getPoint(final float distance) {
        return tmpV3f.set(direction).mul(distance).add(origin);
    }
    
    public void set(Ray ray) {
        this.direction.set(ray.direction);
        this.origin.set(ray.origin);
    }
    
    public Ray copy() {
        Ray ray = new Ray();
        ray.set(this);
        return ray;
    }
    
    public Ray mul(Matrix4f mat) {
        tmpV3f.set(origin).add(direction);
        tmpV3f.mulProject(mat);
        origin.mulProject(mat);
        direction.set(tmpV3f.sub(origin)).normalize();
        return this;
    }
    
    public RayAabIntersection aabIntersection(RayAabIntersection dest) {
        dest.set(origin.x,origin.y,origin.z,direction.x,direction.y,direction.z);
        return dest;
    }
}
