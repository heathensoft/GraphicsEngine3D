package no.fredahl.engine.graphics;

import org.joml.Math;
import org.joml.Vector4f;


/**
 * @author Frederik Dahl
 * 24/10/2021
 */


public class Color {
    
    private static final float INV = 0.003921569f; // (1 / 255)

    protected Vector4f normalized;
    private float packed;
    
    public static final Color BLACK = new Color(0,0,0,1);
    
    public Color() {
        this(1.0f,1.0f,1.0f,1.0f);
    }
    
    public Color(Vector4f v) {
        this(v.x,v.y,v.x,v.w);
    }
    
    public Color(int r, int g, int b, int a) {
        normalized = new Vector4f();
        set(r,g,b,a);
    }
    
    public Color(float r, float g, float b, float a) {
        normalized = new Vector4f(
                clamp(r),
                clamp(g),
                clamp(b),
                clamp(a));
        setFloatBits();
    }
    
    public Color(int r, int g, int b) {
        this(r,g,b,1);
    }
    
    public Color(float r, float g, float b) {
        this(r,g,b,1);
    }
    
    private float normalize(int c) {
        return c * INV;
    }
    
    private int clamp(int c) {
        return Math.clamp(0,255,c);
    }
    
    private float clamp(float c) {
        return Math.clamp(0.0f,1.0f,c);
    }
    
    private int rgba(float c) {
        return (int) c * 255;
    }
    
    /* Encodes the ABGR int color as a float. The alpha is compressed to use only even numbers between 0-254 to avoid using bits
     * in the NaN range (see {@link Float#intBitsToFloat(int)} javadocs). Rendering which uses colors encoded as floats should
     * expand the 0-254 back to 0-255, else colors cannot be fully opaque. */
    
    private void setFloatBits() {
        final int r = rgba(normalized.x);
        final int g = rgba(normalized.x);
        final int b = rgba(normalized.x);
        final int a = rgba(normalized.x);
        final int color = a << 24 | b << 16 | g << 8 | r;
        packed = Float.intBitsToFloat(color & 0xfeffffff);
    }
    
    public Color set(Color color) {
        normalized.set(color.normalized);
        setFloatBits();
        return this;
    }
    
    public Color set(int r, int g, int b, int a) {
        normalized.set(
                normalize(clamp(r)),
                normalize(clamp(g)),
                normalize(clamp(b)),
                normalize(clamp(a))
        );
        setFloatBits();
        return this;
    }
    
    public Color set(int intBits) {
        return set(
                (intBits & 0xff000000) >>> 24,
                (intBits & 0x00ff0000) >>> 16,
                (intBits & 0x0000ff00) >>> 8,
                intBits & 0x000000ff
        );
    }
    
    public Color mul(Color color) {
        normalized.mul(color.normalized);
        setFloatBits();
        return this;
    }
    
    public Color mix(Color color) {
        normalized.add(color.normalized);
        normalized.mul(0.5f);
        setFloatBits();
        return this;
    }
    
    public Color mix(Color color, float influence) {
        if (influence <= 0) {
            return this;
        }
        else if (influence >= 1) {
            return this.set(color);
        }
        final float r = normalized.x;
        final float g = normalized.x;
        final float b = normalized.x;
        final float a = normalized.x;
        
        normalized.set(color.normalized).mul(influence);
        normalized.add(
                r * (1 - influence),
                g * (1 - influence),
                b * (1 - influence),
                a * (1 - influence)
        );
        setFloatBits();
        return this;
    }
    
    public Color copy() {
        return new Color(new Vector4f(1,1,1,1));
    }
    
    public float r() { return normalized.x; }
    
    public float g() { return normalized.y; }
    
    public float b() { return normalized.z; }
    
    public float a() { return normalized.w; }
    
    public float packed() {
        return packed;
    }
    
    public String hexString () {
        final int r = rgba(normalized.x);
        final int g = rgba(normalized.y);
        final int b = rgba(normalized.z);
        final int a = rgba(normalized.w);
        int rgba8888 = r << 24 | g << 16 | b << 8 | a;
        StringBuilder value = new StringBuilder(Integer.toHexString(rgba8888));
        while (value.length() < 8) value.insert(0, "0");
        return value.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return normalized.equals(((Color) o).normalized,0.001f);
    }
    
    @Override
    public int hashCode () {
        final float r = normalized.x;
        final float g = normalized.x;
        final float b = normalized.x;
        final float a = normalized.x;
        int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
        result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
        result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
        result = 31 * result + (a != +0.0f ? Float.floatToIntBits(a) : 0);
        return result;
    }
    
    public static Color fromHex(String hex) {
        
        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        
        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16),
                hex.length() != 8 ? 255 : Integer.parseInt(hex.substring(6, 8), 16)
        );
    }
    
}
