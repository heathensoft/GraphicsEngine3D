package no.fredahl.example1;

import org.joml.RayAabIntersection;
import org.joml.Vector3f;

/**
 * @author Frederik Dahl
 * 04/11/2021
 */

// From the JOML documentation:
// If many boxes need to be tested against the same ray, then the {RayAabIntersection} class is likely more efficient.

public interface Selectable {
    
    
    boolean intersects(RayAabIntersection rayIntersection);
    
    Vector3f position();
    
    void select();
    
    void unselect();
    
    boolean isSelected();
    
}
