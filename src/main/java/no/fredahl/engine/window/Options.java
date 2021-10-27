package no.fredahl.engine.window;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public interface Options {
    
    default String windowTitle() { return "Application";}
    default int desiredResolutionWidth() { return 1280; }
    default int desiredResolutionHeight() { return 720; }
    default boolean compatibleProfile() {
        return false;
    }
    default boolean verticalSynchronization() { return false; }
    default boolean lockAspectRatio() { return true; }
    default boolean resizableWindow() { return true; }
    default boolean windowedMode() { return true; }
    default boolean showTriangles() { return false; }
    default boolean antialiasing() { return false; }
    default boolean cullFace() { return false; }
}
