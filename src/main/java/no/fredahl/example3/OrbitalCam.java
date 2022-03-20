package no.fredahl.example3;

import no.fredahl.engine.math.Camera;
import no.fredahl.engine.math.MathLib;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 18/01/2022
 */


public class OrbitalCam extends Camera {
    
    private final static float PI2 = (float) (Math.PI * 2);
    private final static float MAX_PITCH = Math.toRadians(89);
    private final static float MIN_PITCH = Math.toRadians(15);
    private final static float MIN_PIVOT_DISTANCE = 5.0f;
    private final static float MAX_PIVOT_DISTANCE = 50.0f;
    
    public float horizontalSensitivity = 2;
    public float verticalSensitivity = 2;
    public float distanceFromPivot = 10;
    public float verticalRotation = Math.toRadians(60);
    public float horizontalRotation = 0;
    
    private final Matrix4f viewINV = new Matrix4f();
    private final Matrix4f projectionINV = new Matrix4f();
    private final Vector3f currentFocus = new Vector3f();
    private final Vector3f right = new Vector3f();
    
    public OrbitalCam() {
        fieldOfView = Math.toRadians(60);
        aspectRatio = 16/9f;
        farPlane = 1000;
        nearPlane = 0.01f;
    }
    
    public void follow(Vector3f targetFocus) {
        currentFocus.set(targetFocus);
        float horizontalDist = distanceFromPivot * Math.cos(verticalRotation);
        float verticalDist = distanceFromPivot * Math.sin(verticalRotation);
        float offsetX = horizontalDist * Math.sin(horizontalRotation);
        float offsetZ = horizontalDist * Math.cos(horizontalRotation);
        position.y = currentFocus.y + verticalDist;
        position.x = currentFocus.x - offsetX;
        position.z = currentFocus.z - offsetZ;
        direction.set(currentFocus).sub(position).normalize();
        right.set(direction).cross(MathLib.UP_VECTOR).normalize();
        up.set(right).cross(direction);
        view.identity().lookAt(position,currentFocus,up);
        viewINV.set(view).invert();
    }
    
    @Override
    public void updateProjection() {
        projection.identity().setPerspective(
                fieldOfView, aspectRatio,
                Math.abs(nearPlane),
                Math.abs(farPlane),
                false);
        projectionINV.set(projection);
        projectionINV.invert();
    }
    
    public void rotateHorizontally(float dx) {
        float r = -dx * horizontalSensitivity + horizontalRotation;
        if (r > PI2) r -= PI2;
        else if (r < 0) r += PI2;
        horizontalRotation = r;
    }
    
    public void rotateVertically(float dy) {
        float r = -dy * verticalSensitivity + verticalRotation;
        verticalRotation = Math.clamp(MIN_PITCH,MAX_PITCH,r);
    }
    
    public void zoom(float amount) {
        float z = distanceFromPivot - amount;
        distanceFromPivot = Math.clamp(MIN_PIVOT_DISTANCE,MAX_PIVOT_DISTANCE,z);
    }
    
    public Vector3f right() {
        return right;
    }
    
    @Override
    public Matrix4f projectionINV() {
        return projectionINV;
    }
    
    @Override
    public Matrix4f viewINV() {
        return viewINV;
    }
    
    @Override
    public Matrix4f combined() {
        Matrix4f combined = MathLib.mat4().identity();
        return combined.set(projection).mul(view);
    }
    
    @Override
    public Matrix4f combinedINV() {
        Matrix4f combinedINV = MathLib.mat4().identity();
        return combinedINV.set(projectionINV).mul(viewINV);
    }
}
