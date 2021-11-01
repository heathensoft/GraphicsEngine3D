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
 * 01/11/2021
 */


public class Mouse2 {
    
    public static final int NUM_BUTTONS = 3;
    
    public static final int LEFT  = GLFW_MOUSE_BUTTON_LEFT;
    public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int WHEEL = GLFW_MOUSE_BUTTON_MIDDLE;
    
    private final Window window;
    
    private final MouseEnterEvents mouseEnterEvents; // enterEvents
    private final MouseHoverEvents mouseHoverEvents;
    private final MousePressEvents mousePressEvents;
    private final MouseScrollEvents mouseScrollEvents;
    
    private final Vector2d currentScreenPos = new Vector2d();
    private final Vector2d previousScreenPos = new Vector2d();
    private final Vector2d currentViewportPos = new Vector2d();
    private final Vector2d previousViewportPos = new Vector2d();
    private final Vector2d deltaViewportVec = new Vector2d();
    private final Vector2d deltaScreenVec = new Vector2d();
    
    private final boolean[] buttonPressedCurrent = new boolean[NUM_BUTTONS];
    private final boolean[] buttonPressedPrevious = new boolean[NUM_BUTTONS];
    private final boolean[] mouseDragging = new boolean[NUM_BUTTONS];
    
    private final Vector2d[] dragOriginsViewport = new Vector2d[NUM_BUTTONS];
    private final Vector2d[] dragOriginsScreen = new Vector2d[NUM_BUTTONS];
    
    private final float[] dragTimers = new float[NUM_BUTTONS];
    public float dragSensitivity = 5.0f;
    
    private final Vector2d tmp1 = new Vector2d();
    
    private MouseListener listener;
    
    private boolean cursorInWindow;
    private int scroll;
    
    public Mouse2(Window window) {
        
        this.window = window;
        
        this.mouseEnterEvents = window.mouseEnterEvents();
        this.mouseHoverEvents = window.mouseHoverEvents();
        this.mousePressEvents = window.mousePressEvents();
        this.mouseScrollEvents = window.mouseScrollEvents();
        
        for (int button = 0; button < NUM_BUTTONS; button++) {
            dragOriginsScreen[button] = new Vector2d();
        }
    }
    
    public void collect(float delta) {
    
        scroll = mouseScrollEvents.value();
        cursorInWindow = mouseEnterEvents.isInWindow();
    
        boolean buttonStateChange = false;
        boolean cursorStateChange = false;
    
        System.arraycopy(
                buttonPressedCurrent, 0,
                buttonPressedPrevious, 0,
                NUM_BUTTONS);
    
        for (int button = 0; button < NUM_BUTTONS; button++) {
            buttonPressedCurrent[button] = mousePressEvents.isPressed(button);
            if (buttonPressedCurrent[button] != buttonPressedPrevious[button]){
                buttonStateChange = true;
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
    
        
    
    }
    
    
}
