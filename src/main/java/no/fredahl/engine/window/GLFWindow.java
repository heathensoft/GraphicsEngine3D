package no.fredahl.engine.window;

import no.fredahl.engine.graphics.Color;
import no.fredahl.engine.window.events.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

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
    default void setClearColor(Color color) {
        glClearColor(color.r(),color.g(),color.b(),color.a());
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
    void toggleVsync(boolean on);
    void lockAspectRatio(boolean lock);
    void updateViewport();
    void centerWindow();
    void windowed(int width, int height);
    void fullscreen(int width, int height);
    boolean isWindowed();
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
    
    KeyInput keyInput(); // any thread
    CharInput charInput(); // any thread
    MouseButtons mouseButtons(); // any thread
    MousePosition mousePosition(); // any thread
    MouseScroll mouseScroll(); // any thread
    void setKeyInput(KeyInput callback);
    void setCharInput(CharInput callback);
    void setMouseButtons(MouseButtons callback);
    void setMousePosition(MousePosition callback);
    void setMouseScroll(MouseScroll callback);
    
}
