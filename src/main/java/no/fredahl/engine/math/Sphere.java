package no.fredahl.engine.math;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.primitives.AABBf;

/**
 * @author Frederik Dahl
 * 05/01/2022
 */


public class Sphere {
    
    public float x, y, z, r;
    
    public Sphere(float x, float y, float z, float r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }
    
    public Sphere(Vector3f center, float r) {
        this(center.x, center.y, center.z, r);
    }
    
    public Sphere() {
        this(0,0,0,1);
    }
    
    public boolean contains(Vector3f point) {
        return contains(point.x, point.y, point.z);
    }
    
    public boolean contains(float x, float y, float z) {
        final float dx = this.x - x;
        final float dy = this.y - y;
        final float dz = this.z - z;
        final float dSq = dx * dx + dy * dy + dz * dz;
        return dSq < r * r;
    }
    
    public boolean contains(float x, float y, float z, float r) {
        if (!contains(x,y,z)) return false;
        return contains(x,y,z+r);
    }
    
    public boolean contains(Vector3f center, float r) {
        return contains(center.x, center.y, center.z, r);
    }
    
    public boolean contains(Sphere sphere) {
        return contains(sphere.x,sphere.y,sphere.z, sphere.r);
    }
    
    public boolean inContact(Sphere sphere) {
        return inContact(sphere.x,sphere.y,sphere.z, sphere.r);
    }
    
    public boolean inContact(Vector3f center, float r) {
        return inContact(center.x, center.y, center.z, r);
    }
    
    public boolean inContact(float x, float y, float z, float r) {
        final float dx = this.x - x;
        final float dy = this.y - y;
        final float dz = this.z - z;
        final float dSq = dx * dx + dy * dy + dz * dz;
        final float rSum = this.r + r;
        return dSq < rSum * rSum;
    }
    
    public boolean inContact(AABBf box) {
        return box.intersectsSphere(x,y,z,r*r);
    }
    
    public boolean intersects(Sphere sphere) {
        return intersects(sphere.x,sphere.y,sphere.z, sphere.r);
    }
    
    public boolean intersects(Vector3f center, float r) {
        return intersects(center.x,center.y,center.z,r);
    }
    
    public boolean intersects(float x, float y, float z, float r) {
        return Intersectionf.testSphereSphere(this.x, this.y, this.z, this.r * this.r,x,y,z,r*r);
    }
    
    public void set(Vector3f position, float r) {
        setPosition(position);
        this.r = r;
    }
    
    public void set(float x, float y, float z, float r) {
        setPosition(x, y, z);
        this.r = r;
    }
    
    public void setPosition(Vector3f position) {
        setPosition(position.x,position.y, position.z);
    }
    
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void translate(Vector3f translation) {
        translate(translation.x,translation.y,translation.z);
    }
    
    public void translate(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }
}
