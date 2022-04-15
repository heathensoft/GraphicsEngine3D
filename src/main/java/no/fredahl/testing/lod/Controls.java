package no.fredahl.testing.lod;

import no.fredahl.engine.Engine;
import no.fredahl.engine.math.MathLib;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import no.fredahl.engine.window.processors.MouseListener;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.primitives.Planef;
import org.joml.primitives.Rayf;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Utility class for testing purposes.
 *
 * @author Frederik Dahl
 * 19/12/2021
 */


public class Controls implements MouseListener {
    
    private static final int EXIT = GLFW_KEY_ESCAPE;
    private static final int CONTROL = GLFW_KEY_LEFT_CONTROL;
    private static final int STRAFE_LEFT = GLFW_KEY_A;
    private static final int STRAFE_RIGHT = GLFW_KEY_D;
    private static final int FORWARD = GLFW_KEY_W;
    private static final int BACKWARD = GLFW_KEY_S;
    
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final OrbitalCamera camera;
    private final FocalPoint focalPoint;
    
    float velocity = 160;
    
    public Controls(Window window) {
        this(new Mouse(window),new Keyboard(window),new OrbitalCamera());
    }
    
    public Controls(Mouse mouse, Keyboard keyboard, OrbitalCamera camera) {
        this.mouse = mouse;
        this.camera = camera;
        this.keyboard = keyboard;
        this.focalPoint = new FocalPoint(4096/2f,200,4096/2f);
        mouse.setListener(this);
    }
    
    public void processInput(float dt) {
        
        keyboard.collect();
    
        if (keyboard.justPressed(EXIT)) {
            Engine.get().exit();
            return;
        }
    
        mouse.collect(dt);
        
        float strafe = 0;
        float straight = 0;
        
        if (keyboard.pressed(FORWARD)) straight += 1;
        if (keyboard.pressed(BACKWARD)) straight -= 1;
        if (keyboard.pressed(STRAFE_LEFT)) strafe -= 1;
        if (keyboard.pressed(STRAFE_RIGHT)) strafe += 1;
    
        if (straight != 0) {
            Vector3f v3 = MathLib.vec3();
            v3.set(0,1,0).cross(camera.right()).normalize();
            focalPoint.translate(v3.mul(straight * velocity * dt));
        }
        if (strafe != 0) {
            Vector3f v3 = MathLib.vec3();
            v3.set(camera.right()).mul(strafe * velocity * dt);
            focalPoint.translate(v3);
        }
        Vector3f focalPointPos = focalPoint.getPosition(dt);
        camera.follow(focalPointPos);
    }
    
    public void resize(Window window) {
        camera.setAspectRatio(window.aspectRatio());
        camera.updateProjection();
    }
    
    public OrbitalCamera getCamera() {
        return camera;
    }
    
    @Override
    public void hover(double x, double y, double dX, double dY, double nX, double nY) {
    
    }
    
    @Override
    public void click(int button, double x, double y, double nX, double nY) {
        
        if (button == Mouse.RIGHT) {
            Vector3f normal = MathLib.vec3().set(0,1,0);
            Vector3f point = MathLib.vec3().set(0,0,0);
            Vector3f intersection = MathLib.vec3();
            Rayf ray = MathLib.ray();
            MathLib.rayCast.mouse(
                    camera.projectionINV(),
                    camera.viewINV(),
                    camera.position(),
                    (float) nX,(float) nY,ray
            );
            if (MathLib.rayCast.intersectPlane(ray,new Planef(point,normal),intersection)) {
                System.out.println(intersection.x + " " + intersection.z);
                focalPoint.lockOn(new Vector3f(intersection.x,200,intersection.z));
                //focalPoint.moveTo(new Vector3f(intersection.x,camera.position().y,intersection.z));
            }
        }
        
        
        
    }
    
    @Override
    public void scroll(int value, double x, double y) {
        camera.zoom(value);
    }
    
    @Override
    public void dragging(int button, double vX, double vY, double dX, double dY) {
        if (button == Mouse.LEFT) {
            camera.rotateHorizontally((float) dX);
            camera.rotateVertically((float) dY);
        }
    }
    
    @Override
    public void dragStart(int button, double pX, double pY) {
    
    }
    
    @Override
    public void dragRelease(int button, double pX, double pY) {
    
    }
    
    private static final class FocalPoint {
        
        private final Vector3f position = new Vector3f();
        private final Vector3f start = new Vector3f();
        private final Vector3f tmpV3f = new Vector3f();
        private Vector3f target = null;
        private boolean inTransition;
        private boolean lockedOn;
        private float t;
        private float d;
        
        public FocalPoint(float x, float y, float z) {
            position.set(x,y,z);
        }
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
}
