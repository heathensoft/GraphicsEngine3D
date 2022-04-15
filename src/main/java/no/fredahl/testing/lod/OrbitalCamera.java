package no.fredahl.testing.lod;

import no.fredahl.engine.math.Camera;
import org.joml.Math;
import org.joml.Vector3f;

/**
 *
 * Example use camera
 *
 * @author Frederik Dahl
 * 18/01/2022
 */


public class OrbitalCamera extends Camera {
    
    private final static float PI2 = (float) (Math.PI * 2);
    private final static float MAX_PITCH = Math.toRadians(89);
    private final static float MIN_PITCH = Math.toRadians(15);
    private final static float MIN_PIVOT_DISTANCE = 5.0f;
    private final static float MAX_PIVOT_DISTANCE = 1500.0f;
    
    public float zoomSpeed = 16;
    public float horizontalSensitivity = 2;
    public float verticalSensitivity = 2;
    public float distanceFromPivot = 10;
    public float verticalRotation = Math.toRadians(60);
    public float horizontalRotation = 0;
    public float baselineY = 0;
    public float strafeVelocity = 3;
    
    private final Vector3f focus = new Vector3f();
    private final Vector3f destination = new Vector3f();
    private boolean followMode = false;
    
    public void update(float dt) {
    
    }
    
    public void follow(Vector3f targetFocus) {
        float horizontalDist = distanceFromPivot * Math.cos(verticalRotation);
        float verticalDist = distanceFromPivot * Math.sin(verticalRotation);
        float offsetX = horizontalDist * Math.sin(horizontalRotation);
        float offsetZ = horizontalDist * Math.cos(horizontalRotation);
        focus.set(targetFocus);
        position.y = focus.y + verticalDist;
        position.x = focus.x - offsetX;
        position.z = focus.z - offsetZ;
        lookAt(targetFocus);
    }
    
    public void strafeRight() {
    
    }
    
    public void strafeLeft() {
    
    }
    
    public void strafeDown() {
    
    }
    
    public void strafeUp() {
    
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
        float z = distanceFromPivot - (amount * zoomSpeed);
        distanceFromPivot = Math.clamp(MIN_PIVOT_DISTANCE,MAX_PIVOT_DISTANCE,z);
    }
    
}
