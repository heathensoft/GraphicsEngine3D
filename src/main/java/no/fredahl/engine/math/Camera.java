package no.fredahl.engine.math;

import no.fredahl.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * This class is borrowed heavily from libgdx.
 * Converted into using the JOML library
 *
 * @author Frederik Dahl
 * 30/10/2021
 */


public abstract class Camera {
    
    protected final Window window;
    
    public final Matrix4f projection = new Matrix4f();
    public final Matrix4f view = new Matrix4f();
    public final Matrix4f combined = new Matrix4f();
    public final Matrix4f inverseCombined = new Matrix4f();
    public final Vector3f position = new Vector3f(0.000001f,0.000001f,0.000001f);
    public final Vector3f direction = new Vector3f(0,0,-1);
    public final Vector3f up = new Vector3f(0,1,0);
    
    protected final Vector3f tmpV1 = new Vector3f();
    protected final Vector3f tmpV2 = new Vector3f();
    protected final Matrix4f tmpM1 = new Matrix4f();
    
    public float near = 0.01f;
    public float far = 1000;
    
    public Camera(Window window) {
        this.window = window;
    }
    
    public abstract void update();
    
    
    public void translate(float x, float y, float z) {
        position.add(x,y,z);
    }
    
    public void translate(Vector3f vec) {
        position.add(vec);
    }
    
    public void rotate(float angle, float xAxis, float yAxis, float zAxis) {
        direction.rotateAxis(angle,xAxis,yAxis,zAxis);
        up.rotateAxis(angle,xAxis,yAxis,zAxis);
    }
    
    public void rotate(float x, float y) {
        tmpV1.set(direction).cross(up);
        tmpM1.identity().rotateY(x).rotate(y, tmpV1);
        direction.mulProject(tmpM1);
        up.mulProject(tmpM1);
    }
    
    public void rotate(float angle, Vector3f axis) {
        rotate(angle,axis.x,axis.y,axis.z);
    }
    
    public void rotate(Matrix4f transform) {
        direction.mulProject(transform);
        up.mulProject(transform);
    }
    
    public void rotateAround(Vector3f point, float angle, Vector3f axis) {
    
    }
    
    public void transform(Matrix4f transform) {
        position.mulProject(transform);
        rotate(transform);
    }
    
    public void lookAt(float x, float y, float z) {
        tmpV1.set(x,y,z).sub(position).normalize();
        if (tmpV1.x != 0 && tmpV1.y != 0 && tmpV1.z != 0) {
            float dot = tmpV1.dot(up);
            if (Math.abs(dot - 1f) < 0.000000001f) {
                up.set(direction).mul(-1);
            }
            else if (Math.abs(dot + 1f) < 0.000000001f) {
                up.set(direction);
            }
            direction.set(tmpV1);
            tmpV1.set(direction).cross(up);
            up.set(tmpV1).cross(direction).normalize();
        }
    }
    
    public void lookAt(Vector3f point) {
        lookAt(point.x,point.y,point.z);
    }
    
    public Vector3f project(Vector3f worldCoords) {
        worldCoords.x = window.viewportW() * (worldCoords.x + 1) / 2 * window.viewportX();
        worldCoords.y = window.viewportH() * (worldCoords.y + 1) / 2 * window.viewportY();
        worldCoords.z = (worldCoords.z + 1) / 2;
        return worldCoords;
    }
    
    public Vector3f unProject(Vector3f screenCoords) {
        float x = screenCoords.x - window.viewportX();
        float y = window.windowH() - screenCoords.y - window.viewportY();
        screenCoords.x = (2 * x) / window.viewportW() - 1;
        screenCoords.y = (2 * y) / window.viewportH() - 1;
        screenCoords.z = 2 * screenCoords.z - 1;
        return screenCoords.mulProject(inverseCombined);
    }
    
    
    
}
