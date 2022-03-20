package no.fredahl.engine.math;

import org.joml.FrustumIntersection;
import org.joml.Vector3f;
import org.joml.primitives.AABBf;

/**
 * @author Frederik Dahl
 * 31/12/2021
 */


public interface Cullable {
    
    boolean insideFrustum(FrustumIntersection frustum);
    
    boolean insideAABB(AABBf aabb);
    
    boolean insideSphere(Vector3f center, float radius);
    
    
}
