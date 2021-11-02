package no.fredahl.engine.window.processors;

import no.fredahl.engine.window.Window;
import no.fredahl.engine.window.events.MouseEnterEvents;
import no.fredahl.engine.window.events.MouseHoverEvents;
import no.fredahl.engine.window.events.MousePressEvents;
import no.fredahl.engine.window.events.MouseScrollEvents;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 27/10/2021
 */


public class MouseOld {
    
    public static final int NUM_BUTTONS = 3;
    
    public static final int LEFT  = GLFW_MOUSE_BUTTON_LEFT;
    public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int WHEEL = GLFW_MOUSE_BUTTON_MIDDLE;
    
    private final Window window;
    
    private final MouseEnterEvents mouseEnterEvents;
    private final MouseHoverEvents mouseHoverEvents;
    private final MousePressEvents mousePressEvents;
    private final MouseScrollEvents mouseScrollEvents;
    
    private final Vector2d deltaScreenVec = new Vector2d();
    private final Vector2d currentScreenPos = new Vector2d();
    private final Vector2d previousScreenPos = new Vector2d();
    private final Vector2d currentViewportPos = new Vector2d();
    private final Vector2d previousViewportPos = new Vector2d();
    private final Vector2d deltaViewportVec = new Vector2d();
    
    public final Vector2d dragOriginScreenLeft = new Vector2d();
    public final Vector2d dragOriginScreenRight = new Vector2d();
    public final Vector2d dragOriginScreenWheel = new Vector2d();
    public final Vector2d dragOriginViewportLeft = new Vector2d();
    public final Vector2d dragOriginViewportRight = new Vector2d();
    public final Vector2d dragOriginViewportWheel = new Vector2d();
    
    private final boolean[] buttonPressedCurrent = new boolean[NUM_BUTTONS];
    private final boolean[] buttonPressedPrevious = new boolean[NUM_BUTTONS];
    private final boolean[] mouseDragging = new boolean[NUM_BUTTONS];
    
    private final float[] dragTimers = new float[NUM_BUTTONS];
    public float dragSensitivity = 5.0f;
    
    private final Vector2d tmp1 = new Vector2d();
    
    private int scroll;
    private boolean cursorInWindow;
    
    
    public MouseOld(Window window) {
        this.mouseEnterEvents = window.mouseEnterEvents();
        this.mouseHoverEvents = window.mouseHoverEvents();
        this.mousePressEvents = window.mousePressEvents();
        this.mouseScrollEvents = window.mouseScrollEvents();
        this.window = window;
        
    }
    
    
    public void collect(float delta) {
        
        scroll = mouseScrollEvents.value();
        cursorInWindow = mouseEnterEvents.isInWindow();
        
        boolean buttonStateChange = false;
        boolean cursorStateChange = false;
        
        System.arraycopy(
                buttonPressedCurrent,
                0,
                buttonPressedPrevious,
                0,
                NUM_BUTTONS);
    
        buttonPressedCurrent[LEFT] = mousePressEvents.isPressed(LEFT);
        buttonPressedCurrent[RIGHT] = mousePressEvents.isPressed(RIGHT);
        buttonPressedCurrent[WHEEL] = mousePressEvents.isPressed(WHEEL);
    
        for (int button = 0; button < NUM_BUTTONS; button++) {
            if (buttonPressedCurrent[button] != buttonPressedPrevious[button]) {
                buttonStateChange = true;
                break;
            }
        }
        
        previousScreenPos.set(currentScreenPos);
        currentScreenPos.set(mouseHoverEvents.x(),window.windowH() - mouseHoverEvents.y());
        // When dragging, the mouse vector seems to register when off-screen.
        // So we bind the position to our window. We do the same for viewport coordinates below
        currentScreenPos.x = Math.min(window.windowW(),Math.max(currentScreenPos.x,0));
        currentScreenPos.y = Math.min(window.windowH(),Math.max(currentScreenPos.y,0));
        
        if (!previousScreenPos.equals(currentScreenPos,0.000d))
            cursorStateChange = true;
        
        if (cursorStateChange) {
            previousViewportPos.set(currentViewportPos);
            // if the viewport fits the window (integer values)
            // we set the viewport coordinates to equal screen coordinates
            if (window.windowW() == window.viewportW() && window.windowH() == window.viewportH()) {
                currentViewportPos.set(currentScreenPos);
        
            } else {
                final double viewportOffsetX = currentScreenPos.x - window.viewportX();
                final double viewportOffsetY = currentScreenPos.y - window.viewportY();
                final double screenToViewportRatioX = window.windowW() * (double)window.viewportInvW();
                final double screenToViewportRatioY = window.windowH() * (double)window.viewportInvH();
        
                currentViewportPos.set(
                        viewportOffsetX * screenToViewportRatioX,
                        viewportOffsetY * screenToViewportRatioY);
        
                final double maxX = window.viewportW() * screenToViewportRatioX;
                final double maxY = window.viewportH() * screenToViewportRatioY;
                currentViewportPos.x = Math.min(maxX,Math.max(currentViewportPos.x,0));
                currentViewportPos.y = Math.min(maxY,Math.max(currentViewportPos.y,0));
            }
            tmp1.set(currentScreenPos);
            deltaScreenVec.set(tmp1.sub(previousScreenPos));
            tmp1.set(currentViewportPos);
            deltaViewportVec.set(tmp1.sub(previousViewportPos));
        }
        else {
            deltaScreenVec.zero();
            deltaViewportVec.zero();
        }
        
        if (buttonStateChange) {
            for (int button = 0; button < NUM_BUTTONS; button++) {
                if (!buttonPressedCurrent[button]) {
                    switch (button) {
                        case LEFT:
                            dragOriginScreenLeft.set(currentScreenPos);
                            dragOriginViewportLeft.set(currentViewportPos);
                            break;
                        case RIGHT:
                            dragOriginScreenRight.set(currentScreenPos);
                            dragOriginViewportRight.set(currentViewportPos);
                            break;
                        case WHEEL:
                            dragOriginScreenWheel.set(currentScreenPos);
                            dragOriginViewportWheel.set(currentViewportPos);
                            break;
                    }
                    // Drag just released
                    if (buttonPressedPrevious[button]) {
                        mouseDragging[button] = false;
                        dragTimers[button] = 0.0f;
                    }
                }
                else {
                    if (!buttonPressedPrevious[button]) {
                        switch (button) {
                            case LEFT:
                                dragOriginScreenLeft.set(currentScreenPos);
                                dragOriginViewportLeft.set(currentViewportPos);
                                break;
                            case RIGHT:
                                dragOriginScreenRight.set(currentScreenPos);
                                dragOriginViewportRight.set(currentViewportPos);
                                break;
                            case WHEEL:
                                dragOriginScreenWheel.set(currentScreenPos);
                                dragOriginViewportWheel.set(currentViewportPos);
                                break;
                        }
                    }
                    dragTimers[button] += dragSensitivity * delta;
                    // Dragging
                    if (dragTimers[button] > 1.0f) {
                        mouseDragging[button] = true;
                    }
                }
            }
        }
        else {
            // if no button-state change, we still increase timers if pressed
            for (int button = 0; button < NUM_BUTTONS; button++) {
                if (buttonPressedCurrent[button]) {
                    dragTimers[button] += dragSensitivity * delta;
                    if (dragTimers[button] > 1.0f) {
                        mouseDragging[button] = true;
                    }
                }
            }
        }
    }
    
    public boolean isDragging(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return mouseDragging[button];
    }
    
    public boolean isPressed(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return buttonPressedCurrent[button];
    }
    
    public boolean justPressed(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return buttonPressedCurrent[button] && !buttonPressedPrevious[button];
    }
    
    public boolean justReleased(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return buttonPressedPrevious[button] && !buttonPressedCurrent[button];
    }
    
    public Vector2d screenNDC() {
        final double nX = 2 * currentScreenPos.x / window.windowW() - 1;
        final double nY = 2 * currentScreenPos.y / window.windowH() - 1;
        return tmp1.set(nX,nY);
    }
    
    public Vector2d viewportNDC() {
        final double nx = 2 * currentViewportPos.x * window.viewportInvW() - 1;
        final double ny = 2 * currentViewportPos.y * window.viewportInvH() - 1;
        return tmp1.set(nx,ny);
    }
    
    public Vector2d deltaScreenVec() {
        return deltaScreenVec;
    }
    
    public Vector2d deltaViewportVec() {
        return deltaViewportVec;
    }
    
    public double deltaScreenX() {
        return deltaScreenVec.x;
    }
    
    public double deltaScreenY() {
        return deltaScreenVec.y;
    }
    
    public double deltaViewportX() {
        return deltaViewportVec.x;
    }
    
    public double deltaViewportY() {
        return deltaViewportVec.y;
    }
    
    public double screenX() {
        return currentScreenPos.x;
    }
    
    public double screenY() {
        return currentScreenPos.y;
    }
    
    public double viewportX() {
        return currentViewportPos.x;
    }
    
    public double viewportY() {
        return currentViewportPos.y;
    }
    
    public double viewportNDCx() {
        return 2 * currentViewportPos.x * window.viewportInvW() - 1;
    }
    
    public double viewportNDCy() {
        return 2 * currentViewportPos.y * window.viewportInvH() - 1;
    }
    
    public double screenNDCx() {
        return 2 * currentScreenPos.x / window.windowW() - 1;
    }
    
    public double screenNDCy() {
        return 2 * currentScreenPos.y / window.windowH() - 1;
    }
    
    public int getScroll() {
        return scroll;
    }
    
    public boolean cursorInWindow() {
        return cursorInWindow;
    }
}
