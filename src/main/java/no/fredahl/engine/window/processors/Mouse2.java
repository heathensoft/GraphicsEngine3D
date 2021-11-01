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
    
    public static final int BUTTONS = 3;
    
    public static final int LEFT  = GLFW_MOUSE_BUTTON_LEFT;
    public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int WHEEL = GLFW_MOUSE_BUTTON_MIDDLE;
    
    private final Window window;
    
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
    
    private final boolean[] current = new boolean[BUTTONS];
    private final boolean[] previous = new boolean[BUTTONS];
    private final boolean[] dragging = new boolean[BUTTONS];
    
    private final Vector2d[] dragOrigin = new Vector2d[BUTTONS];
    
    private final float[] timer = new float[BUTTONS];
    public float dragSensitivity = 5.0f;
    
    
    
    private MouseListener listener;
    
    private boolean cursorInWindow;
    
    public Mouse2(Window window) {
        this.window = window;
        this.enterEvents = window.mouseEnterEvents();
        this.hoverEvents = window.mouseHoverEvents();
        this.pressEvents = window.mousePressEvents();
        this.scrollEvents = window.mouseScrollEvents();
    }
    
    public void collect(float delta) {
        
        prevScreen.set(currentScreen);
        currentScreen.set(hoverEvents.x(),window.windowH() - hoverEvents.y());
        currentScreen.x = Math.min(window.windowW(),Math.max(currentScreen.x,0));
        currentScreen.y = Math.min(window.windowH(),Math.max(currentScreen.y,0));
        
        if (!prevScreen.equals(currentScreen,0.0001d)) {
            prevVP.set(currentVP);
            if (window.windowW() == window.viewportW() && window.windowH() == window.viewportH()) {
                currentVP.set(currentScreen);
            } else {
                final double offsetX = currentScreen.x - window.viewportX();
                final double offsetY = currentScreen.y - window.viewportY();
                final double ratioX = window.windowW() * (double)window.viewportInvW();
                final double ratioY = window.windowH() * (double)window.viewportInvH();
                final double maxX = window.viewportW() * ratioX;
                final double maxY = window.viewportH() * ratioY;
                currentVP.set(offsetX * ratioX, offsetY * ratioY);
                currentVP.x = Math.min(maxX,Math.max(currentVP.x,0));
                currentVP.y = Math.min(maxY,Math.max(currentVP.y,0));
            }
            tmp1.set(currentVP);
            dt.set(tmp1.sub(prevVP));
            ndc.x = 2 * currentVP.x * window.viewportInvW() - 1;
            ndc.y = 2 * currentVP.y * window.viewportInvH() - 1;
            listener.hover(currentVP.x,currentVP.y,dt.x,dt.y,ndc.x,ndc.y);
        }
        else dt.zero();
        
        for (int b = 0; b < BUTTONS; b++) {
            previous[b] = current[b];
            current[b] = pressEvents.isPressed(b);
            if (current[b]) {
                timer[b] += delta * dragSensitivity;
                if (!previous[b]) {
                    listener.click(b,currentVP.x,currentVP.y,ndc.x,ndc.y);
                    dragOrigin[b] = currentVP;
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
        listener.scroll(scrollEvents.value(),currentVP.x,currentVP.y);
    }
    
    
}
