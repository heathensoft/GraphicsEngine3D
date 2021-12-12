package no.fredahl.example1;

/**
 * @author Frederik Dahl
 * 19/10/2021
 */


public class TextureRegion {
    
    private  int w;
    private  int h;
    
    private float u;
    private float v;
    private float u2;
    private float v2;
    
    private Texture t;
    
    
    public TextureRegion (Texture t) {
        this.t = t;
        setRegion(0, 0, t.width(), t.height());
    }
    
    public TextureRegion (Texture t, int w, int h) {
        this.t = t;
        setRegion(0, 0, w, h);
    }
    
    public TextureRegion (Texture t, int x, int y, int w, int h) {
        this.t = t;
        setRegion(x, y, w, h);
    }
    
    public TextureRegion (Texture t, float u, float v, float u2, float v2) {
        this.t = t;
        setRegion(u, v, u2, v2);
    }
    
    public void setRegion (Texture t) {
        this.t = t;
        setRegion(0, 0, t.width(), t.height());
    }
    
    public void setRegion (int x, int y, int w, int h) {
        
        float invTexWidth = 1f / t.width();
        float invTexHeight = 1f / t.height();
        
        setRegion(
                x * invTexWidth,
                y * invTexHeight,
                (x + w) * invTexWidth,
                (y + h) * invTexHeight);
        
        this.w = Math.abs(w);
        this.h = Math.abs(h);
    }
    
    public void setRegion (float u, float v, float u2, float v2) {
        
        int texWidth = t.width();
        int texHeight = t.height();
        
        w = Math.round(Math.abs(u2 - u) * texWidth);
        h = Math.round(Math.abs(v2 - v) * texHeight);
        
        if (w == 1 && h == 1) {
            float adjustX = 0.25f / texWidth;
            u += adjustX;
            u2 -= adjustX;
            float adjustY = 0.25f / texHeight;
            v += adjustY;
            v2 -= adjustY;
        }
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }
    
    public void setRegion (TextureRegion r) {
        t = r.t;
        setRegion(r.u, r.v, r.u2, r.v2);
    }
    
    public void setRegion (TextureRegion r, int x, int y, int w, int h) {
        t = r.t;
        setRegion(r.x() + x, r.y() + y, w, h);
    }
    
    public int w() {
        return w;
    }
    
    public int h() {
        return h;
    }
    
    public float u() {
        return u;
    }
    
    public float v() {
        return v;
    }
    
    public float u2() {
        return u2;
    }
    
    public float v2() {
        return v2;
    }
    
    public int x() {
        return Math.round(u * t.width());
    }
    
    public int y() {
        return Math.round(v * t.height());
    }
    
    public void setW(int w) {
        this.w = w;
    }
    
    public void setX (int x) {
        setU(x / (float) t.width());
    }
    
    public void setY (int y) {
        setV(y / (float) t.height());
    }
    
    public void setH(int h) {
        this.h = h;
    }
    
    public void setU(float u) {
        w = Math.round(Math.abs(u2 - (this.u = u)) * t.width());
    }
    
    public void setV(float v) {
        h = Math.round(Math.abs(v2 - (this.v = v)) * t.height());
    }
    
    public void setU2(float u2) {
        w = Math.round(Math.abs((this.u2 = u2) - u) * t.width());
    }
    
    public void setV2(float v2) {
        h = Math.round(Math.abs((this.v2 = v2) - v) * t.height());
    }
    
    public Texture texture() {
        return t;
    }
    
    public void setT(Texture t) {
        this.t = t;
    }
    
    public void flip (boolean x, boolean y) {
        if (x) {
            float temp = u;
            u = u2;
            u2 = temp;
        }
        if (y) {
            float temp = v;
            v = v2;
            v2 = temp;
        }
    }
    
    public boolean isFlippedX () {
        return u > u2;
    }
    
    public boolean isFlippedY () {
        return v > v2;
    }
    
    
    
    public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight, boolean bleedFix) {
        
        return new TextureRegion(texture).split(tileWidth,tileHeight,bleedFix);
    }
    
    public TextureRegion[][] split(int tileWidth, int tileHeight) {
        return split(tileWidth,tileHeight,false);
    }
    
    public TextureRegion[] split(int tileWidth, int tileHeight, int count, int offset, boolean bleedFix) {
        
        final int rows = h / tileHeight;
        final int cols = w / tileWidth;
        
        final float invWidth = 1f / w;
        final float invHeight = 1f / h;
        final float fix = bleedFix ? 0.001f : 0;
        
        int x = x();
        int y = y();
        
        int pointer = 0;
        int startX = x;
        
        TextureRegion[] result = new TextureRegion[count];
        
        out:
        
        for (int row = 0; row < rows; row++, y += tileHeight, x = startX) {
            
            for (int col = 0; col < cols; col++, x += tileWidth) {
                
                if (offset > 0) offset--;
                
                else { if (count-- == 0) break out;
                    
                    float u =  (x + fix) * invWidth;
                    float u2 = (x + tileWidth  - fix) * invWidth;
                    float v  = (y + fix) * invHeight;
                    float v2 = (y + tileHeight - fix) * invHeight;
                    
                    result[pointer++] = new TextureRegion(t,u,v,u2,v2);
                }
            }
        }
        return result;
    }
    
    public TextureRegion[][] split(int tileWidth, int tileHeight, boolean bleedFix) {
        
        final int rows = h / tileHeight;
        final int cols = w / tileWidth;
        
        int x = x();
        int y = y();
        
        int startX = x;
        
        TextureRegion[][] tiles = new TextureRegion[rows][cols];
        
        for (int row = 0; row < rows; row++, y += tileHeight, x = startX) {
            
            for (int col = 0; col < cols; col++, x += tileWidth) {
                
                TextureRegion r;
                
                if (bleedFix) {
                    
                    float fix = 0.0001f;
                    float invWidth = 1f / w;
                    float invHeight = 1f / h;
                    
                    float u =  (x + fix) * invWidth;
                    float u2 = (x + tileWidth  - fix) * invWidth;
                    float v  = (y + fix) * invHeight;
                    float v2 = (y + tileHeight - fix) * invHeight;
                    
                    r = new TextureRegion(t,u,v,u2,v2);
                }
                else r = new TextureRegion(t, x, y, tileWidth, tileHeight);
                
                tiles[row][col] = r;
            }
        }
        return tiles;
    }
    
}
