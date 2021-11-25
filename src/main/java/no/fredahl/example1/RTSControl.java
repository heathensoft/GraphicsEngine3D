package no.fredahl.example1;

import no.fredahl.engine.Engine;
import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.processors.Keyboard;
import no.fredahl.engine.window.processors.Mouse;
import no.fredahl.engine.window.processors.MouseListener;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;
import org.joml.primitives.Rayf;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * You are not moving the camera but an invisible focal point.
 * The camera follows the focal point.
 *
 * @author Frederik Dahl
 * 04/11/2021
 */


public class RTSControl implements MouseListener {
    
    private static final int EXIT = GLFW_KEY_ESCAPE;
    private static final int CONTROL = GLFW_KEY_LEFT_CONTROL;
    private static final int STRAFE_LEFT = GLFW_KEY_A;
    private static final int STRAFE_RIGHT = GLFW_KEY_D;
    private static final int FORWARD = GLFW_KEY_W;
    private static final int BACKWARD = GLFW_KEY_S;
    private static final Vector3f tmpV3f = new Vector3f();

    World world;
    Mouse mouse;
    Keyboard keyboard;
    OrbitalCamera camera;
    FocalPoint focalPoint;
    RayAabIntersection rayIntersection;
    
    float velocity = 3;
    
    
    public RTSControl(World world) {
        Window window = Engine.get().window;
        mouse = new Mouse(window);
        mouse.setListener(this);
        keyboard = new Keyboard(window);
        camera = new OrbitalCamera();
        focalPoint = new FocalPoint();
        rayIntersection = new RayAabIntersection();
        this.world = world;
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
    
    public void onWindowResize(float aspectRatio) {
        camera.aspectRatio = aspectRatio;
        camera.updateProjection();
    }
    
    public OrbitalCamera getCamera() {
        return camera;
    }
    
    @Override
    public void hover(double x, double y, double dX, double dY, double nX, double nY) {
    
    }
    private static Vector3f intersection(Vector3f origin, Vector3f direction, Vector3f dest) {
        Vector3f pointOnPlane = new Vector3f(1,0,1);
        Vector3f normal = new Vector3f(0,1,0);
        Vector3f tmp = new Vector3f();
        tmp.set(origin).sub(pointOnPlane);
        float prod1 = tmp.dot(normal);
        float prod2 = direction.dot(normal);
        System.out.println(prod2);
        float prod3 = prod1 / prod2;
        
        tmp.set(direction).mul(prod3);
        return dest.set(origin).sub(tmp);
    }
    @Override
    public void click(int button, double x, double y, double nX, double nY) {
        if (button == Mouse.LEFT) {
            if (keyboard.pressed(CONTROL)) {
                // create unit
            }
            else {
                Rayf r;
                boolean unitGotSelected = false;
                final float ndcX = (float) nX;
                final float ndcY = (float) nY;
                r = camera.getPickingRay(ndcX,ndcY);
                rayIntersection.set(r.oX,r.oY,r.oZ,r.dX,r.dY,r.dZ);
                Vector3f origin = new Vector3f(r.oX,r.oY,r.oZ);
                Vector3f dir = new Vector3f(r.dX,r.dY,r.dZ);
                Vector3f dest = new Vector3f();
                intersection(origin,dir,dest);
                //System.out.println((int) dest.x + " " + (int) dest.y + " " + (int) dest.z);
                List<Unit> units = world.getUnits();
                for (Unit unit : units) {
                    if (!unitGotSelected) {
                        if (unit.intersects(rayIntersection)) {
                            unitGotSelected = true;
                            if (unit.isSelected()) {
                                focalPoint.lockOn(unit.position());
                            }
                            else unit.select();
                            continue;
                        }
                    }
                    if (unit.isSelected())
                        unit.unselect();
                }
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
}
