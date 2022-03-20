package no.fredahl.engine.window;

import no.fredahl.engine.Application;
import no.fredahl.engine.window.events.*;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public interface GLFWindow {
    
    /**
     * Makes OpenGL-context current in the calling thread
     */
    void initialize();
    
    default void signalToClose() {
        glfwSetWindowShouldClose(windowHandle(),true);
    }
    default boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle());
    }
    default void swapBuffers() {
        glfwSwapBuffers(windowHandle());
    }
    default void setClearColor(Vector4f color) {
        glClearColor(color.x,color.y,color.z,color.w);
    }
    void waitEvents(float seconds);
    void show();
    void hide();
    void focus();
    void maximize();
    void minimize();
    void restore();
    void create(Options options) throws Exception;
    void terminate();
    void setWindowTitle(String title);
    void disableCursor(boolean disable);
    void centerCursor();
    void toggleVsync(boolean on);
    void lockAspectRatio(boolean lock);
    void updateViewport(Application app);
    void borrowViewport(int x, int y, int w, int h);
    void returnViewport();
    void centerWindow();
    void windowed(int width, int height);
    void fullscreen(int width, int height);
    boolean isWindowed();
    boolean cursorDisabled();
    boolean vsyncEnabled();
    boolean isMinimized();
    long windowHandle();
    long monitorHandle();
    int windowW();
    int windowH();
    int windowX();
    int windowY();
    int frameBufferW();
    int frameBufferH();
    int viewportW();
    int viewportH();
    int viewportX();
    int viewportY();
    float aspectRatio();
    float viewportInvW();
    float viewportInvH();
    
    // options is used for initialization only.
    // changing options will not change the window.
    // but you could set options then and save on exit.
    // the options would then take effect after restart.
    
    Options options(); // any thread
    
    // Display related callbacks are executed independently from glfwPollEvents.
    // i.e. an engine.window resize callback is not triggered on pollEvents
    
    KeyPressEvents keyPressEvents(); // any thread
    CharPressEvents charPressEvents(); // any thread
    MousePressEvents mousePressEvents(); // any thread
    MouseEnterEvents mouseEnterEvents(); // any thread
    MouseHoverEvents mouseHoverEvents(); // any thread
    MouseScrollEvents mouseScrollEvents(); // any thread
    void setKeyPressCallback(KeyPressEvents callback);
    void setCharPressCallback(CharPressEvents callback);
    void setMousePressCallback(MousePressEvents callback);
    void setMouseEnterCallback(MouseEnterEvents callback);
    void setMouseHoverCallback(MouseHoverEvents callback);
    void setMouseScrollCallback(MouseScrollEvents callback);
    
}
