package no.fredahl.engine.window;

import no.fredahl.engine.window.events.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public interface GLFWindow {
    
    
    void initialize(); // Makes context current in calling thread
    
    // Any thread
    default void makeContextCurrent() {
        glfwMakeContextCurrent(windowHandle());
    }
    // Any thread
    default void signalToClose() {
        glfwSetWindowShouldClose(windowHandle(),true);
    }
    // Any thread
    default boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle());
    }
    // Any thread
    default void swapBuffers() {
        glfwSwapBuffers(windowHandle());
    }
    // Context thread
    default void setClearColor(float r, float g, float b, float a) {
        glClearColor(r,g,b,a);
    }
    // Main thread only
    default void pollEvents() {
        glfwPollEvents();
    }
    // Main thread only
    default void waitEvents() {
        glfwWaitEvents();
    }
    // Main thread only
    default void setTitle(CharSequence title) {
        glfwSetWindowTitle(windowHandle(), title);
    }
    // Main thread only
    default void show() {
        glfwShowWindow(windowHandle());
    }
    // Main thread only
    default void hide() {
        glfwHideWindow(windowHandle());
    }
    // Main thread only
    default void focus() {
        glfwFocusWindow(windowHandle());
    }
    // Main thread only
    default void maximize() {
        glfwMaximizeWindow(windowHandle());
    }
    // Main thread only
    default void minimize() {
        glfwIconifyWindow(windowHandle());
    }
    // Main thread only
    default void restore() {
        glfwRestoreWindow(windowHandle());
    }
    
    // Main thread only.
    void terminate();
    
    void toggleVsync(boolean on); // context thread
    
    void lockAspectRatio(boolean lock); // any thread
    
    void updateViewport(); // any thread
    
    void centerWindow(); // Main thread
    
    void windowed(int width, int height); // Main thread
    
    void fullscreen(int width, int height); // Main thread
    
    boolean isWindowed(); // any thread
    
    boolean vsyncEnabled(); // any thread
    
    boolean isMinimized(); // any thread
    
    long windowHandle(); // any thread
    
    long monitorHandle(); // any thread
    
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
    
    Viewport viewport(); // any thread
    
    // options is used for engine.window initialization only.
    // changing options will not change the engine.window.
    // but you could set options, and save on exit.
    // the options will take effect after restart.
    
    Options options(); // any thread
    
    // Display related callbacks are executed independently from glfwPollEvents.
    // i.e. a engine.window resize callback is not triggered on pollEvents
    
    KeyInput keyInput(); // any thread
    CharInput charInput(); // any thread
    MouseButtons mouseButtons(); // any thread
    MousePosition mousePosition(); // any thread
    MouseScroll mouseScroll(); // any thread
    
}
