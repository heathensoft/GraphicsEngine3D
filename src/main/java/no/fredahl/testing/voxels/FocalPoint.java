package no.fredahl.testing.voxels;

import org.joml.Math;
import org.joml.Vector3f;


/**
 * @author Frederik Dahl
 * 04/11/2021
 */


public class FocalPoint {
 
    private final Vector3f position = new Vector3f();
    private final Vector3f start = new Vector3f();
    private final Vector3f tmpV3f = new Vector3f();
    private Vector3f target = null;
    private boolean inTransition;
    private boolean lockedOn;
    private float t;
    private float d;
    
    
    public void lockOn(Vector3f target) {
        moveTo(target);
        lockedOn = true;
    }
    
    public void moveTo(Vector3f target) {
        if (this.target == target) return;
        this.target = target;
        start.set(position);
        d = Math.abs(start.distance(target));
        inTransition = true;
        t = 0.0f;
    }
    
    public void moveTo(Vector3f... targets) {
        moveTo(getCenter(targets));
    }
    
    public Vector3f getPosition(float dt) {
        if (inTransition) {
            t += calculateStep(dt);
            interpolate(smoothStep(t));
            if (t >= 1.0f)
                inTransition = false;
            return position;
        }
        if (lockedOn) position.set(target);
        return position;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void translate(Vector3f translation) {
        if (lockedOn) {
            target = null;
            lockedOn = false;
        }
        inTransition = false;
        position.add(translation);
    }
    
    private float calculateStep(float dt) {
        return (3 * dt) / (2 * d + 1) + (2 * dt / 3);
    }
    
    private float smoothStep(float t) {
        return Math.min(t * t * (3 - 2 * t),1.0f);
    }
    
    private void interpolate(float t) {
        position.x = start.x + (target.x - start.x) * t;
        position.y = start.y + (target.y - start.y) * t;
        position.z = start.z + (target.z - start.z) * t;
    }
    
    private Vector3f getCenter(Vector3f... targets) {
        float x = 0; float y = 0; float z = 0;
        float l = targets.length;
        for (int i = 0; i < l; i++) {
            x += target.x;
            y += target.y;
            z += target.z;
        }
        return tmpV3f.set(x/l,y/l,z/l);
    }
    
}
