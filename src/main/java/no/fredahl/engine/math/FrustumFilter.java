package no.fredahl.engine.math;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * @author Frederik Dahl
 * 31/12/2021
 */


public class FrustumFilter {
    
    private final FrustumIntersection frustum;
    
    public FrustumFilter() {
        this(new FrustumIntersection());
    }
    
    public FrustumFilter(FrustumIntersection frustum) {
        this.frustum = frustum;
    }
    
    public void setFrustum(Matrix4f combined) {
        frustum.set(combined);
    }
    
    public void setFrustum(Matrix4f combined, boolean allowTestSpheres) {
        frustum.set(combined,allowTestSpheres);
    }
    
    public void filter(ArrayList<Cullable> source, ArrayList<Cullable> dest) {
        dest.clear();
        for (Cullable cullable : source) {
            if (cullable.cullingEnabled()) {
                if (cullable.insideFrustum(frustum))
                    dest.add(cullable);
            } else dest.add(cullable);
        }
    }
    
    public boolean insideFrustum(Cullable cullable) {
        return cullable.insideFrustum(frustum);
    }
    
}
