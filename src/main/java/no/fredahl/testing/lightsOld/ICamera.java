package no.fredahl.testing.lightsOld;

import org.joml.Matrix4f;

/**
 * @author Frederik Dahl
 * 05/11/2021
 */


public interface ICamera {
    
    Matrix4f projection();
    Matrix4f view();
    Matrix4f combined();
    Matrix4f inverseProjection();
    Matrix4f inverseView();
    Matrix4f inverseCombined();
    
}
