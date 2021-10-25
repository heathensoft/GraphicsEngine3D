package no.fredahl.engine.window;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class Viewport {
    
    boolean aspectLocked;
    private int x, y, w, h;
    private float ar, iw, ih;
    
    
    public Viewport(int width, int height) {
        update(width, height);
    }
    
    // todo: why did I make this synchronized again?
    // should not be any reason to.
    public synchronized void update(int width, int height) {
        if (!aspectLocked) {
            ar = (float) width / height;
        }
        int aw = width;
        int ah = Math.round ((float)aw / ar);
        if (ah > height) {
            ah = height;
            aw = Math.round((float)ah * ar);
        }
        x = Math.round(((float) width / 2f) - ((float)aw / 2f));
        y = Math.round(((float) height / 2f) - ((float)ah / 2f));
        w = aw;
        h = ah;
        iw = 1f / w;
        ih = 1f / h;
    }
    
    // why did I make this synchronized again?
    public synchronized void lockAspectRatio(boolean on) {
        aspectLocked = on;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
    
    public int width() {
        return w;
    }
    
    public int height() {
        return h;
    }
    
    public float aspectRatio() {
        return ar;
    }
    
    public float inverseWidth() {
        return iw;
    }
    
    public float inverseHeight() {
        return ih;
    }
}
