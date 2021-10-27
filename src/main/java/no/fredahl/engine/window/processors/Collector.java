package no.fredahl.engine.window.processors;

/**
 * @author Frederik Dahl
 * 27/10/2021
 */

@FunctionalInterface
public interface Collector {
    void next(int key);
}
