package no.fredahl.engine.math;

import org.joml.Matrix4f;

/**
 *
 * Utility class with static common usage matrices
 *
 * @author Frederik Dahl
 * 22/12/2021
 */


public class Matrices {
    
    /**
     * "B matrix"
     * Used to convert normalized device coordinates (-1 to 1) to (0 to 1)
     */
    public final static Matrix4f B_MATRIX = new Matrix4f().translate(0.5f,0.5f,0.5f).scale(0.5f);
}
