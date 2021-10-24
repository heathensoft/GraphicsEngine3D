package no.fredahl.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 16/10/2021
 */


public class Camera {
    
    private final Window window;
    private final Vector3f pos;
    private final Vector3f rot;
    private final Matrix4f m_0;
    private final Vector3f v_0;
    private final Vector3f v_1;
    private final float zNear;
    private final float zFar;
    private final float fov;
    
    
    public Camera(Window window, Vector3f position, Vector3f rotation, float zNear, float zFar, float fov) {
        
        this.window = window;
        this.pos = position;
        this.rot = rotation;
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
        
        // Helper objects
        this.m_0 = new Matrix4f();
        this.v_0 = new Vector3f();
        this.v_1 = new Vector3f();
    }
    
    public Camera(Window window) {
        this(window, new Vector3f(), new Vector3f(),0.01f,1000.0f,(float) Math.toRadians(60.0f));
    }
    
    
    public Matrix4f view(Matrix4f mat) {
        final float x = (float)Math.toRadians(rot.x);
        final float y = (float)Math.toRadians(rot.y);
        v_0.set(1,0,0);
        v_1.set(0,1,0);
        mat.identity().rotate(x,v_0).rotate(y,v_1);
        return mat.translate(-pos.x,-pos.y,-pos.z);
    }
    
    public Matrix4f modelView(Matrix4f model, Matrix4f dest) {
        return dest.set(view(m_0)).mul(model);
    }
    
    public Matrix4f inverseView(Matrix4f mat) {
        return view(mat).invert();
    }
    
    public Matrix4f perspective(Matrix4f mat) {
        return mat.setPerspective(fov, window.aspectRatio(),zNear,zFar);
    }
    
    public Matrix4f inversePerspective(Matrix4f mat) {
        return perspective(mat).invert();
    }
    
    public Matrix4f orthographic(Matrix4f mat) {
        final float v = window.viewportW()/2.0f;
        final float h = window.viewportH()/2.0f;
        return mat.identity().ortho(-v, v, -h, h, zNear, zFar);
    }
    
    public Matrix4f inverseOrthographic(Matrix4f mat) {
        return orthographic(mat).invert();
    }
    
    public void translate(float x, float y, float z) {
        if ( z != 0 ) {
            pos.x += (float)Math.sin(Math.toRadians(rot.y)) * -1.0f * z;
            pos.z += (float)Math.cos(Math.toRadians(rot.y)) * z;
        }
        if ( x != 0) {
            pos.x += (float)Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * x;
            pos.z += (float)Math.cos(Math.toRadians(rot.y - 90)) * x;
        }
        pos.y += y;
    }
    
    public void translate(Vector3f translation) {
        translate(translation.x, translation.y, translation.z);
    }
    
    public void rotate(float x, float y, float z) {
        rot.x += x;
        rot.y += y;
        rot.z += z;
    }
    
    public void setPosition(float x, float y, float z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }
    
    public void setRotation(float x, float y, float z) {
        rot.x = x;
        rot.y = y;
        rot.z = z;
    }
    
    public void setPosition(Vector3f pos) {
        this.pos.set(pos);
    }
    
    public void setRotation(Vector3f rot) {
        this.rot.set(rot);
    }
    
    public Vector3f getPosition() {
        return pos;
    }
    
    public Vector3f getRotation() {
        return rot;
    }
    
    public float getNear() {
        return zNear;
    }
    
    public float getFar() {
        return zFar;
    }
    
    public float getFov() {
        return fov;
    }
}
