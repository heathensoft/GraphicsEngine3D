package no.fredahl.engine.math;

import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 30/10/2021
 */


public class OrthographicCamera extends Camera {
    
    public float zoom = 1f;
    
    public OrthographicCamera(Window window) {
        super(window);
        this.near = 0;
        this.far = 100;
    }
    
    @Override
    public void update() {
        final float left = - zoom * (window.viewportW() / 2f);
        final float right = zoom * (window.viewportW() / 2f);
        final float bottom = - zoom * (window.viewportH() / 2f);
        final float top = zoom * (window.viewportH() / 2f);
        projection.ortho(left,right,bottom,top,Math.abs(near),Math.abs(far),false);
        tmp2.set(position).add(direction);
        view.lookAt(position,tmp2,up);
        combined.set(projection).mul(view);
        inverseCombined.set(combined).invert();
    }
}
