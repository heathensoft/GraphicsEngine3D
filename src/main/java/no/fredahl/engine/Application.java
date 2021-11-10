package no.fredahl.engine;


import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 22/10/2021
 */


public interface Application {
    
    /**
     * The start of the application. Initialize scenes, renderers and load assets.
     * @param window the glfw window
     * @throws Exception any asset-loading or initializations fails
     */
    void start(Window window) throws Exception;
    
    /**
     * The input is for collecting input from callbacks. i.e. keyboard.collect()
     * Updating the values from last frames' input to the current.
     * It is required to implement this, but you could likewise update input from the update();
     * The difference is that this won't be called when the window is minimized,
     * but the update method will.
     * @param delta = 1 / UPS (updates per second). It is a fixed interval.
     * Set in the Engine. This is useful for stability.
     */
    void input(float delta);
    
    /**
     * The update-method is where the applications' logic is performed.
     * @param delta = 1 / UPS (updates per second). It is a fixed interval.
     * Set in the Engine. This is useful for stability.
     */
    void update(float delta);
    
    /**
     * For updating projection matrices
     * @param window the glfw window
     */
    void resize(Window window);
    
    /**
     * The rendering is happening separately from than that of
     * the input and update methods. (Look up "fixed time intervals")
     * @param alpha alpha = accumulator / delta, where the accumulator
     *              is a value from 0 to delta. You can think of it as
     *              the remaining time of an update. Can be used to
     *              render appropriately using interpolation.
     */
    void render(float alpha);
    
    /**
     * When the window is signalled to close, after finishing the
     * current run-cycle, this method is called. Happening
     * before the window is terminated by the main-thread.
     * Use this to free memory outside the JVM.
     */
    void exit();
}
