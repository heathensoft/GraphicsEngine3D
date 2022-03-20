package no.fredahl.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 03/01/2022
 */


public interface DirectionalLight {
    
    Vector3f direction();
    
    void setDirection(Vector3f direction, boolean normalize);
    
    void setDirection(float x, float y, float z, boolean normalize);
    
    void rotateAxis(float radians, Vector3f axis);
    
    void rotateAxis(float radians, float x, float y, float z);
    
    void rotateX(float radians);
    
    void rotateY(float radians);
    
    void rotateZ(float radians);
}
