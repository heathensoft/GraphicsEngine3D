package no.fredahl.engine.graphics;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Frederik Dahl
 * 26/11/2021
 */


public class Color {
    
    public static final Vector4f WHITE_RGBA = new Vector4f(1.0f,1.0f,1.0f,1.0f);
    public static final Vector4f BLACK_RGBA = new Vector4f(0.0f,0.0f,0.0f,0.0f);
    
    public static final Vector3f WHITE_RGB = new Vector3f(1.0f,1.0f,1.0f);
    public static final Vector3f BLACK_RGB = new Vector3f(0.0f,0.0f,0.0f);
    
    // todo: Just clean up this class in general + Mixing with influence
    
    public static float packed(float r, float g, float b, float a) {
        final int red = rgba(clamp(r));
        final int gre = rgba(clamp(g));
        final int blu = rgba(clamp(b));
        final int alp = rgba(clamp(a));
        final int i = alp << 24 | blu << 16 | gre << 8 | red;
        return Float.intBitsToFloat(i & 0xfeffffff);
    }
    
    public static float packed(Vector4f color) {
        final int r = rgba(clamp(color.x));
        final int g = rgba(clamp(color.y));
        final int b = rgba(clamp(color.z));
        final int a = rgba(clamp(color.w));
        final int i = a << 24 | b << 16 | g << 8 | r;
        return Float.intBitsToFloat(i & 0xfeffffff);
    }
    
    public static float packed(Vector3f color) {
        final int r = rgba(clamp(color.x));
        final int g = rgba(clamp(color.y));
        final int b = rgba(clamp(color.z));
        final int a = rgba(clamp(1.0f));
        final int i = a << 24 | b << 16 | g << 8 | r;
        return Float.intBitsToFloat(i & 0xfeffffff);
    }
    
    public Vector4f fromRGBA(int r, int g, int b, int a) {
        Vector4f color = new Vector4f();
        color.x = normalize(clamp(r));
        color.y = normalize(clamp(g));
        color.z = normalize(clamp(b));
        color.w = normalize(clamp(a));
        return color;
    }
    
    public Vector3f fromRGB(int r, int g, int b) {
        Vector3f color = new Vector3f();
        color.x = normalize(clamp(r));
        color.y = normalize(clamp(g));
        color.z = normalize(clamp(b));
        return color;
    }
    
    public Vector4f fromHex(String hex) {
        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        return fromRGBA(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16),
                hex.length() != 8 ? 255 : Integer.parseInt(hex.substring(6, 8), 16)
        );
    }
    
    public String toHex(Vector4f color) {
        final int r = rgba(clamp(color.x));
        final int g = rgba(clamp(color.y));
        final int b = rgba(clamp(color.z));
        final int a = rgba(clamp(color.w));
        int rgba8888 = r << 24 | g << 16 | b << 8 | a;
        StringBuilder value = new StringBuilder(Integer.toHexString(rgba8888));
        while (value.length() < 8) value.insert(0, "0");
        return value.toString();
    }
    
    public String toHex(Vector3f color) {
        final int r = rgba(clamp(color.x));
        final int g = rgba(clamp(color.y));
        final int b = rgba(clamp(color.z));
        final int a = rgba(clamp(1.0f));
        int rgba8888 = r << 24 | g << 16 | b << 8 | a;
        StringBuilder value = new StringBuilder(Integer.toHexString(rgba8888));
        while (value.length() < 8) value.insert(0, "0");
        return value.toString();
    }
    
    private static float normalize(int c) {
        return c * 0.003921569f;
    }
    
    private static int rgba(float c) {
        return (int) (c * 255);
    }
    
    private static int clamp(int c) {
        return Math.clamp(0,255,c);
    }
    
    private static float clamp(float c) {
        return Math.clamp(0.0f,1.0f,c);
    }
}
