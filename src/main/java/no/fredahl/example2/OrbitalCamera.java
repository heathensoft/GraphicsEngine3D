package no.fredahl.example2;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.primitives.Rayf;

/**
 *
 * No roll, orbital camera
 *
 * @author Frederik Dahl
 * 04/11/2021
 */


public class OrbitalCamera implements ICamera {
    
    private final static Vector3f UP = new Vector3f(0,1,0);
    private final static float PI2 = (float) (Math.PI * 2);
    private final static float MAX_PITCH = Math.toRadians(89);
    private final static float MIN_PITCH = Math.toRadians(15);
    private final static float MIN_PIVOT_DISTANCE = 5.0f;
    private final static float MAX_PIVOT_DISTANCE = 50.0f;
    
    private final Matrix4f tmpM4f = new Matrix4f();
    private final Vector3f tmpV3f = new Vector3f();
    private final Vector4f tmpV4f = new Vector4f();
    private final Rayf tmpRf = new Rayf();
    
    public final Matrix4f inverseProjection = new Matrix4f();
    public final Matrix4f worldToView = new Matrix4f();
    public final Vector3f currentFocus = new Vector3f();
    public final Matrix4f projection = new Matrix4f();
    public final Vector3f position = new Vector3f();
    public final Vector3f direction = new Vector3f();
    public final Vector3f right = new Vector3f();
    public final Vector3f up = new Vector3f();
    
    public float horizontalSensitivity = 2;
    public float verticalSensitivity = 2;
    public float distanceFromPivot = 10;
    public float verticalRotation = Math.toRadians(60);
    public float horizontalRotation = 0;
    
    public float aspectRatio = 16/9f;
    public float fieldOfView = Math.toRadians(60);
    public float far = 100;
    public float near = 0.01f;
    
    public void updateProjection() {
        projection.setPerspective(
                fieldOfView, aspectRatio,
                Math.abs(near),
                Math.abs(far),
                false);
        inverseProjection.set(projection);
        inverseProjection.invert();
    }
    
    public void switchToOrtho() {
        
        /*
        float focus_plane = 10;
        float top = Math.tan(fieldOfView/2) * focus_plane;
        float right = top * aspectRatio;
        projection.identity().ortho(
                -right,
                right,
                -top,
                top,
                near,
                far
        );
        inverseProjection.set(projection);
        inverseProjection.invert();
        
         */
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
        right.set(direction).cross(UP).normalize();
        up.set(right).cross(direction);
        worldToView.identity().lookAt(position,currentFocus,up);
    }
    
    public Rayf getPickingRay(float ndcX, float ndcY) {
        tmpV4f.set(ndcX,ndcY,-1.0f,1.0f);
        tmpV4f.mul(inverseProjection);
        tmpV4f.z = -1.0f;
        tmpV4f.w = 0.0f;
        tmpM4f.set(worldToView).invert();
        tmpV4f.mul(tmpM4f);
        tmpV3f.set(tmpV4f.x,tmpV4f.y,tmpV4f.z).normalize();
        tmpRf.oX = position.x;
        tmpRf.oY = position.y;
        tmpRf.oZ = position.z;
        tmpRf.dX = tmpV3f.x;
        tmpRf.dY = tmpV3f.y;
        tmpRf.dZ = tmpV3f.z;
        return tmpRf;
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
    
    @Override
    public final Matrix4f projection() {
        return projection;
    }
    
    @Override
    public final Matrix4f view() {
        return worldToView;
    }
    
    @Override
    public Matrix4f combined() {
        return tmpM4f.set(projection).mul(worldToView);
    }
    
    @Override
    public Matrix4f inverseProjection() {
        return null;
    }
    
    @Override
    public Matrix4f inverseView() {
        return null;
    }
    
    @Override
    public Matrix4f inverseCombined() {
        return null;
    }
}
