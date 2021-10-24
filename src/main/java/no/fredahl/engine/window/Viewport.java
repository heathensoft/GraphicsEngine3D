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
    
    public synchronized void update(int width, int height) {
        if (!aspectLocked) {
            ar = (float) width / height;
        }
        int aw = width;
        int ah = (int)((float)aw / ar);
        if (ah > height) {
            ah = height;
            aw = (int)((float)ah * ar);
        }
        x = (int) (((float) width / 2f) - ((float)aw / 2f));
        y = (int) (((float) height / 2f) - ((float)ah / 2f));
        w = aw;
        h = ah;
        iw = 1f / w;
        ih = 1f / h;
    }
    
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
