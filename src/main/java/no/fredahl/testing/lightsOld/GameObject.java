package no.fredahl.testing.lightsOld;

import no.fredahl.engine.math.Transform;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class GameObject {
    
    public Transform transform;
    public Mesh mesh;
    public int material;
    
    public GameObject(Mesh mesh, Transform transform, int material) {
        this.transform = transform;
        this.material = material;
        this.mesh = mesh;
    }
    
    
}
