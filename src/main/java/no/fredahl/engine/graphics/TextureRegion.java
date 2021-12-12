package no.fredahl.engine.graphics;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author Frederik Dahl
 * 11/12/2021
 */


public class TextureRegion {
    
    private int w; // width pixels
    private int h; // height pixels
    private int r; // rows of sub-regions
    private int c; // cols of sub-regions
    private int e; // num sub-regions
    
    private float sw; // sub-region w normalized
    private float sh; // sub-region h normalized
    
    private float u;
    private float v;
    private float u2;
    private float v2;
    
    private Texture t;
    
    public TextureRegion(Texture texture) {
        this(texture,0,0, texture.width(), texture.height());
    }
    
    public TextureRegion(Texture texture, int x, int y, int w, int h) {
        this.t = texture;
        setRegion(x, y, w, h);
        subDivide(1,1);
    }
    
    public TextureRegion(Texture texture, float u, float v, float u2, float v2) {
        this.t = texture;
        setRegion(u, v, u2, v2);
        subDivide(1,1);
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
    
    public void set (TextureRegion region) {
        t = region.t;
        setRegion(region.u, region.v, region.u2, region.v2);
        subDivide(region.r,region.c);
    }
    
    public void subDivide(int x, int y) {
        this.r = y;
        this.c = x;
        this.e = x * y;
        this.sh = Math.abs(v2-v) / (float) r;
        this.sw = Math.abs(u2-u) / (float) c;
    }
    
    /**
     * If you have the texture coordinates for a mesh in Texture space,
     * you can use this to convert them to region-space if the prior
     * texture now is refitted to a texture-atlas.
     * @param texCoords the texture-coordinates to be adjusted
     */
    public void toRegionCoords(float[] texCoords) {
        final float xRatio = (float) w / (float) t.width();
        final float yRatio = (float) h / (float) t.height();
        //final float u = isFlippedX() ? this.u2 : this.u;
        //final float v = isFlippedX() ? this.v2 : this.v;
        final int l = texCoords.length / 2;
        int iX, iY;
        for (int i = 0; i < l; i++) {
            iX = 2 * i;
            iY = iX + 1;
            texCoords[iX] = texCoords[iX] * xRatio + u;
            texCoords[iY] = texCoords[iY] * yRatio + v;
        }
    }
    
    public void toRegionUVs(Vector4f texUVs) {
        final float xRatio = (float) w / (float) t.width();
        final float yRatio = (float) h / (float) t.height();
        texUVs.x = texUVs.x * xRatio + u;
        texUVs.y = texUVs.y * yRatio + v;
        texUVs.z = texUVs.z * xRatio + u;
        texUVs.w = texUVs.w * yRatio + v;
    }
    
    private final static Vector4f tmpV4f = new Vector4f();
    
    /**
     * @param index sub-region index
     * @return the sub-region coordinates
     */
    public Vector4f subRegionUVs(int index) {
        final int i = index % e;
        final int col = i % c;
        final int row = i / c;
        final float x = u + col * sw;
        final float y = v + row * sh;
        return tmpV4f.set(x,y,x+sw,y+sh);
    }
    
    private final static Vector2f tmpV2f = new Vector2f();
    
    /**
     * If you have the uv-coordinates for the region at index 0,
     * you can add them with the uvOffset for index n
     * to get the uv-coordinates for the region at index n
     *
     * @param index sub-region index
     * @return the offset from index 0 to index argument
     */
    public Vector2f uvOffset(int index) {
        final int i = index % e;
        final int col = i % c;
        final int row = i / c;
        return tmpV2f.set(u + col * sw, v + row * sh);
    }
    
    public TextureRegion subRegion(int index) {
        Vector4f v = subRegionUVs(index);
        return new TextureRegion(t,v.x,v.y,v.z,v.w);
    }
    
    public void setTexture(Texture texture) {
        this.t = texture;
    }
    
    public Texture texture() {
        return t;
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
    
    public int rows() {
        return r;
    }
    
    public int cols() {
        return c;
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
    
    public void setRows(int r) {
        this.r = r;
        this.e = r * c;
        this.sh = Math.abs(v2-v) / (float) r;
    }
    
    public void setCols(int c) {
        this.c = c;
        this.e = c * r;
        this.sw = Math.abs(u2-u) / (float) c;
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
    
    public boolean isFlippedX () {
        return u > u2;
    }
    
    public boolean isFlippedY () {
        return v > v2;
    }
    
    public void flipX() {
        float temp = u;
        u = u2;
        u2 = temp;
    }
    
    public void flipY() {
        float temp = v;
        v = v2;
        v2 = temp;
    }
    
    
}
