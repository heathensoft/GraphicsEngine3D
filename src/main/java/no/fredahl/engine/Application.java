package no.fredahl.engine;


import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 22/10/2021
 */


public interface Application {
    
    void start(Window window);
    void update(float delta);
    void render(float alpha);
    void pause();
    void resume();
    void exit();
}
