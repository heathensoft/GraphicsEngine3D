package no.fredahl.engine.math;

import no.fredahl.engine.window.Window;

/**
 * @author Frederik Dahl
 * 30/10/2021
 */


public class PerspectiveCamera extends Camera {
    
    private static final float PI_HALF = (float) Math.PI / 2;
    public float fov = (float) Math.toRadians(60.0f);
    
    public PerspectiveCamera(Window window) {
        super(window);
    }
    
    
    public void forward(float amount) {
        position.add(tmp1.set(direction).mul(amount));
        
    }
    
    public void backward(float amount) {
        position.add(tmp1.set(direction).mul(-amount));
    }
    
    public void strafeRight(float amount) {
        tmp1.set(direction).rotateY(PI_HALF).mul(amount);
        position.add(tmp1);
    }
    
    public void strafeLeft(float amount) {
        tmp1.set(direction).rotateY(PI_HALF).mul(-amount);
        position.add(tmp1);
    }
    
    @Override
    public void update() {
        
        projection.identity().setPerspective(
                fov,
                window.aspectRatio(),
                Math.abs(near),
                Math.abs(far),
                false);
        tmp2.set(position).add(direction);
        view.identity().lookAt(position,tmp2,up);
        combined.set(projection).mul(view);
        inverseCombined.set(combined).invert();
    }
}
