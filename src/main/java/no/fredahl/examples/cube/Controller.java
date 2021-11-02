package no.fredahl.examples.cube;

import no.fredahl.engine.math.Camera;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import no.fredahl.engine.window.processors.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 02/11/2021
 */


public class Controller implements MouseListener {
    
    private static final int JUMP = GLFW_KEY_SPACE;
    private static final int STRAFE_LEFT = GLFW_KEY_A;
    private static final int STRAFE_RIGHT = GLFW_KEY_D;
    private static final int FORWARD = GLFW_KEY_W;
    private static final int BACKWARD = GLFW_KEY_S;
    
    public float speed = 5;
    private Camera camera;
    private final Keyboard keyboard;
    private final SineJump jump = new SineJump();
    private final Vector3f tmp = new Vector3f();
    
    public Controller(Camera camera, Keyboard keyboard) {
        this.keyboard = keyboard;
        this.camera = camera;
    }
    
    private static final class SineJump {
        
        float time;
        float duration;
        float strength;
        boolean inProgress;
        
        public SineJump() {
            this.duration = 1;
            this.strength = 0.2f;
            this.time = 0;
        }
        
        float process(float dt) {
            if (inProgress) {
                float y = (float) Math.sin(2*Math.PI*time) * strength;
                System.out.println(y);
                time += dt * 1 / duration;
                if (time >= 1) {
                    time = 0;
                    inProgress = false;
                }return y;
            }return 0;
        }
        
        boolean inProgress() {
            return inProgress;
        }
        
        void setStrength(float strength) {
            this.strength = strength;
        }
        
        void setDuration(float duration) {
            this.duration = duration;
        }
        
        void start() {
            inProgress = true;
        }
    }
    
    
    public void update(float dt) {
        
        keyboard.collect();
        
        float strafe = 0;
        float straight = 0;
        
        if (keyboard.pressed(JUMP)&&!jump.inProgress) jump.start();
        if (keyboard.pressed(STRAFE_LEFT)) strafe -= 1;
        if (keyboard.pressed(STRAFE_RIGHT)) strafe += 1;
        if (keyboard.pressed(FORWARD)) straight -= 1;
        if (keyboard.pressed(BACKWARD)) straight += 1;
    
        if (jump.inProgress) {
            tmp.set(camera.up).mul(jump.process(dt));
            camera.position.add(tmp);
        }
        
        if (strafe != 0) {
            tmp.set(camera.direction).cross(camera.up);
            tmp.normalize().mul(strafe*dt* speed).y = 0;
            camera.position.add(tmp);
        }
        
        if (straight != 0) {
            tmp.set(camera.direction).mul(-straight*dt* speed).y = 0;
            camera.position.add(tmp);
        }
        camera.update();
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    
    @Override
    public void hover(double x, double y, double dX, double dY, double nX, double nY) {
    
    }
    
    @Override
    public void click(int button, double x, double y, double nX, double nY) {
    
    }
    
    @Override
    public void scroll(int value, double x, double y) {
    }
    
    @Override
    public void dragging(int button, double vX, double vY, double dX, double dY) {
        if (button == Mouse.LEFT) {
            
            camera.rotate((float) -dX * 2, (float) dY * 2);
            camera.update();
        }
    }
    
    @Override
    public void dragStart(int button, double pX, double pY) {
    
    }
    
    @Override
    public void dragRelease(int button, double pX, double pY) {
    
    }
}
