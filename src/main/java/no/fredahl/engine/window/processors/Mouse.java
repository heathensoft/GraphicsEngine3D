package no.fredahl.engine.window.processors;

import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.events.MouseEnterEvents;
import no.fredahl.engine.window.events.MouseHoverEvents;
import no.fredahl.engine.window.events.MousePressEvents;
import no.fredahl.engine.window.events.MouseScrollEvents;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Versatile cursor-input processor.
 *
 * Used with the MouseListener interface, but can also be queried
 * directly.
 *
 * Three coordinate-systems:
 *
 *  1. Screen.
 *      Position in pixels. 0,0 in the lower left corner of the window
 *  2. Viewport.
 *      Position relative to the viewport. 0,0 in the lower left corner of the viewport.
 *      Width: 0 -> 1, Height: 0 -> (1 / aspect-ratio)
 *  3. NDC.
 *      Normalized device coordinates. -1 to 1 with 0,0 as origin in the center of the window.
 *      Both Screen NDC and viewport relative NDC
 *
 * @author Frederik Dahl
 * 01/11/2021
 */


public class Mouse {
    
    public static final int BUTTONS = 3;
    
    public static final int LEFT  = GLFW_MOUSE_BUTTON_LEFT;
    public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int WHEEL = GLFW_MOUSE_BUTTON_MIDDLE;
    
    private final boolean[] current = new boolean[BUTTONS];
    private final boolean[] previous = new boolean[BUTTONS];
    private final boolean[] dragging = new boolean[BUTTONS];
    
    private final MouseEnterEvents enterEvents;
    private final MouseHoverEvents hoverEvents;
    private final MousePressEvents pressEvents;
    private final MouseScrollEvents scrollEvents;
    
    private final Vector2d currentScreen = new Vector2d();
    private final Vector2d prevScreen = new Vector2d();
    private final Vector2d currentVP = new Vector2d();
    private final Vector2d prevVP = new Vector2d();
    private final Vector2d tmp1 = new Vector2d();
    private final Vector2d ndc = new Vector2d();
    private final Vector2d dt = new Vector2d();
    
    private final Window window;
    
    private MouseListener listener = defaultListener;
    
    private final Vector2d[] dragOrigin = new Vector2d[BUTTONS];
    private final float[] timer = new float[BUTTONS];
    public float dragSensitivity = 5.0f;
    private boolean firstMove = true;
    
    public Mouse(Window window) {
        this.window = window;
        this.enterEvents = window.mouseEnterEvents();
        this.hoverEvents = window.mouseHoverEvents();
        this.pressEvents = window.mousePressEvents();
        this.scrollEvents = window.mouseScrollEvents();
        for (int b = 0; b < BUTTONS; b++) {
            dragOrigin[b] = new Vector2d();
        }
    }
    
    public void setListener(MouseListener listener) {
        if (listener == null) this.listener = defaultListener;
        else this.listener = listener;
        for (int b = 0; b < BUTTONS; b++) {
            dragOrigin[b].set(currentVP);
        }
    }
    
    public void collect(float delta) {
        
        boolean fpsMode = window.cursorDisabled();
        if (firstMove) {
            prevScreen.set(hoverEvents.x(),window.windowH() - hoverEvents.y());
            currentScreen.set(prevScreen);
            firstMove = false;
        }
        else {
            prevScreen.set(currentScreen);
            currentScreen.set(hoverEvents.x(),window.windowH() - hoverEvents.y());
        }
        if (!fpsMode) {
            currentScreen.x = Math.min(window.windowW(),Math.max(currentScreen.x,0));
            currentScreen.y = Math.min(window.windowH(),Math.max(currentScreen.y,0));
        }
        if (!prevScreen.equals(currentScreen,0.0001d)) {
            final double x = (currentScreen.x - window.viewportX()) * window.viewportInvW();
            final double y = (currentScreen.y - window.viewportY()) * window.viewportInvH();
            prevVP.set(currentVP);
            currentVP.x = fpsMode ? x : Math.min(1,Math.max(x,0));
            currentVP.y = fpsMode ? y : Math.min(1,Math.max(y,0));
            ndc.x = 2 * currentVP.x - 1;
            ndc.y = 2 * currentVP.y - 1;
            currentVP.y /= window.aspectRatio();
            tmp1.set(currentVP);
            dt.set(tmp1.sub(prevVP));
            listener.hover(currentVP.x,currentVP.y,dt.x,dt.y,ndc.x,ndc.y);
        
        }else dt.zero();
        
        for (int b = 0; b < BUTTONS; b++) {
            previous[b] = current[b];
            current[b] = pressEvents.isPressed(b);
            if (current[b]) {
                timer[b] += delta * dragSensitivity;
                if (!previous[b]) {
                    listener.click(b,currentVP.x,currentVP.y,ndc.x,ndc.y);
                    dragOrigin[b].set(currentVP);
                }
                else if (timer[b] > 1) {
                    if (!dragging[b]) {
                        dragging[b] = true;
                        listener.dragStart(b, currentVP.x, currentVP.y);
                    } else {
                        tmp1.set(currentVP).sub(dragOrigin[b]);
                        listener.dragging(b,tmp1.x,tmp1.y,dt.x,dt.y);
                    }
                }
            } else if (dragging[b]) {
                dragging[b] = false;
                listener.dragRelease(b,currentVP.x,currentVP.y);
            }
        }
        int scroll = scrollEvents.value(); // this sets value to 0
        if (scroll != 0)
            listener.scroll(scroll,currentVP.x,currentVP.y);
    }
    
    public boolean isDragging(int button) {
        if (button >= BUTTONS || button < 0) return false;
        return dragging[button];
    }
    
    public boolean isPressed(int button) {
        if (button >= BUTTONS || button < 0) return false;
        return current[button];
    }
    
    public boolean justPressed(int button) {
        if (button >= BUTTONS || button < 0) return false;
        return current[button] && !previous[button];
    }
    
    public boolean justReleased(int button) {
        if (button >= BUTTONS || button < 0) return false;
        return !current[button] && previous[button];
    }
    
    public double screenX() {
        return currentScreen.x;
    }
    
    public double screenY() {
        return currentScreen.y;
    }
    
    public double viewportX() {
        return currentVP.x;
    }
    
    public double viewportY() {
        return currentVP.y;
    }
    
    public double viewportNDCx() {
        return ndc.x;
    }
    
    public double viewportNDCy() {
        return ndc.y;
    }
    
    public double screenNDCx() {
        return 2 * currentScreen.x / window.windowW() - 1;
    }
    
    public double screenNDCy() {
        return 2 * currentScreen.y / window.windowH() - 1;
    }
    
    /**
     * to avoid null-checks.
     */
    private final static MouseListener defaultListener = new MouseListener() {
        
        @Override
        public void hover(double x, double y, double dX, double dY, double nX, double nY) {}
    
        @Override
        public void click(int button, double x, double y, double nX, double nY) {}
    
        @Override
        public void scroll(int value, double x, double y) {}
    
        @Override
        public void dragging(int button, double vX, double vY, double dX, double dY) {}
    
        @Override
        public void dragStart(int button, double pX, double pY) {}
    
        @Override
        public void dragRelease(int button, double pX, double pY) {}
        
    };
}
