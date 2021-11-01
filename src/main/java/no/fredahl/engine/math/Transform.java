package no.fredahl.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 16/10/2021
 */


public class Transform {
    
    private final Matrix4f model;
    private final Vector3f pos;
    private final Vector3f rot;
    private final Vector3f scale;
    private boolean dirty;
    
    // Todo: single axis rotation / scale / translation
    
    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.pos = position;
        this.rot = rotation;
        this.scale = scale;
        this.model = new Matrix4f();
        this.dirty = true;
    }
    
    public Transform() {
        this(new Vector3f(), new Vector3f(),new Vector3f(1,1,1));
    }
    
    public Matrix4f model() {
        if (dirty) {
            model.identity().
                    translate(pos).
                    rotateX((float)Math.toRadians(-rot.x)).
                    rotateY((float)Math.toRadians(-rot.y)).
                    rotateZ((float)Math.toRadians(-rot.z)).
                    scale(scale);
            dirty = false;
        }
        return model;
    }
    
    public Matrix4f modelView(Matrix4f view, Matrix4f dest) {
        return dest.set(view).mul(model());
    }
    
    
    public void translate(float x, float y, float z) {
        pos.x += x;
        pos.y += y;
        pos.z += z;
        dirty = true;
    }
    
    public void translate(Vector3f translation) {
        pos.add(translation);
        dirty = true;
    }
    
    public void rotate(float x, float y, float z) {
        rot.x += x;
        rot.y += y;
        rot.z += z;
        dirty = true;
    }
    
    public void rotate(Vector3f rot) {
        rotate(rot.x,rot.y,rot.z);
    }
    
    public void setPosition(float x, float y, float z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
        dirty = true;
    }
    
    public void setPosition(Vector3f pos) {
        this.pos.set(pos);
        dirty = true;
    }
    
    public void setRotation(float x, float y, float z) {
        rot.x = x;
        rot.y = y;
        rot.z = z;
        dirty = true;
    }
    
    public void setRotation(Vector3f rot) {
        this.rot.set(rot);
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
        return pos;
    }
    
    public Vector3f getRotation() {
        return rot;
    }
    
    public Vector3f getScale() {
        return scale;
    }
}
