package no.fredahl.testing.lightsOld;

import no.fredahl.engine.Engine;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import no.fredahl.engine.window.processors.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

/**
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
    private static final Vector3f tmpV3f = new Vector3f();
    
    private final World world;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final OrbitalCamera camera;
    private final FocalPoint focalPoint;
    
    float velocity = 3;
    
    public Controls(Mouse mouse, Keyboard keyboard, OrbitalCamera camera, World world) {
        this.world = world;
        this.mouse = mouse;
        this.camera = camera;
        this.keyboard = keyboard;
        this.focalPoint = new FocalPoint();
        mouse.setListener(this);
    }
    
    public void processInput(float dt) {
        mouse.collect(dt);
        keyboard.collect();
    
        if (keyboard.justPressed(EXIT)) {
            Engine.get().exit();
            return;
        }
        float strafe = 0;
        float straight = 0;
    
        if (keyboard.justPressed(CONTROL)) camera.switchToOrtho();
        if (keyboard.pressed(FORWARD)) straight += 1;
        if (keyboard.pressed(BACKWARD)) straight -= 1;
        if (keyboard.pressed(STRAFE_LEFT)) strafe -= 1;
        if (keyboard.pressed(STRAFE_RIGHT)) strafe += 1;
    
        if (straight != 0) {
            tmpV3f.set(0,1,0).cross(camera.right).normalize();
            focalPoint.translate(tmpV3f.mul(straight * velocity * dt));
        }
        if (strafe != 0) {
            tmpV3f.set(camera.right).mul(strafe * velocity * dt);
            focalPoint.translate(tmpV3f);
        }
        Vector3f focalPointPos = focalPoint.getPosition(dt);
        camera.follow(focalPointPos);
    }
    
    @Override
    public void hover(double x, double y, double dX, double dY, double nX, double nY) {
    
    }
    
    @Override
    public void click(int button, double x, double y, double nX, double nY) {
    
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
}
