package no.fredahl.engine.math;

import org.joml.FrustumIntersection;

/**
 * @author Frederik Dahl
 * 31/12/2021
 */


public interface Cullable {
    
    boolean insideFrustum(FrustumIntersection frustum);
    
    boolean cullingEnabled();
    
    void enableCulling(boolean on);
    
}
