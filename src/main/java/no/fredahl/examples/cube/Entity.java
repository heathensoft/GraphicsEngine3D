package no.fredahl.examples.cube;

import no.fredahl.engine.Transform;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class Entity {
 
    public final Transform transform;
    public final Mesh mesh;
    
    
    public Entity(Mesh mesh) {
        this.transform = new Transform();
        this.mesh = mesh;
    }
    
}
