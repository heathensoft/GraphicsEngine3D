package no.fredahl.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 16/10/2021
 */


public class Transform {
    
    private final Matrix4f modelToWorld;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;
    private boolean dirty;
    
    
    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.modelToWorld = new Matrix4f();
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.dirty = true;
    }
    
    public Transform() {
        this(new Vector3f(), new Vector3f(),new Vector3f(1,1,1));
    }
    
    public Matrix4f get() {
        if (dirty) {
            modelToWorld.identity().
                    translate(position).
                    rotateX((float)Math.toRadians(-rotation.x)).
                    rotateY((float)Math.toRadians(-rotation.y)).
                    rotateZ((float)Math.toRadians(-rotation.z)).
                    scale(scale);
            dirty = false;
        }
        return modelToWorld;
    }
    
    public Matrix4f get(Matrix4f dest) {
        return dest.set(get());
    }
    
    public Matrix4f modelView(Matrix4f view, Matrix4f dest) {
        return dest.set(view).mul(get());
    }
    
    
    public void translate(Vector3f translation) {
        position.add(translation);
        dirty = true;
    }
    
    public void translate(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
        dirty = true;
    }
    
    public void translateX(float x) {
        position.x += x;
        dirty = true;
    }
    
    public void translateY(float y) {
        position.y += y;
        dirty = true;
    }
    
    public void translateZ(float z) {
        position.z += z;
        dirty = true;
    }
    
    public void rotate(Vector3f rot) {
        rotate(rot.x,rot.y,rot.z);
    }
    
    public void rotate(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
        dirty = true;
    }
    
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
        dirty = true;
    }
    
    public void setPosition(Vector3f pos) {
        this.position.set(pos);
        dirty = true;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
        dirty = true;
    }
    
    public void setRotation(Vector3f rot) {
        this.rotation.set(rot);
        dirty = true;
    }
    
    public void setScale(float x, float y, float z) {
        scale.set(x,y,z);
        dirty = true;
    }
    
    public void setScale(float scale) {
        this.scale.set(scale);
        dirty = true;
    }
    
    public void scaleBy(float factor) {
        scale.mul(factor);
        dirty = true;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getRotation() {
        return rotation;
    }
    
    public Vector3f getScale() {
        return scale;
    }
}
