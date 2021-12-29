package no.fredahl.engine.utility.storage;

/**
 * @author Frederik Dahl
 * 29/12/2021
 */

@FunctionalInterface
public interface Iterator<E> {
    void next(E item);
}
