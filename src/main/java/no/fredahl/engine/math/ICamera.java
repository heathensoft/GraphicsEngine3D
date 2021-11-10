package no.fredahl.engine.math;

import org.joml.Matrix4f;

/**
 * @author Frederik Dahl
 * 05/11/2021
 */


public interface ICamera {
 
    Matrix4f getProjectionMatrix();
    Matrix4f getWorldToViewMatrix();
}
