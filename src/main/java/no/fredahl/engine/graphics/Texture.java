package no.fredahl.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Frederik Dahl
 * 04/12/2021
 *
 * Example initialization:
 *
 * Texture tex = new Texture(GL_TEXTURE_2D);
 * tex.bind();
 * tex.setTextureWrapST(GL_REPEAT);
 * tex.setFilter(GL_LINEAR);
 * tex.tex2D(image);
 * tex.generateMipMap();
 * tex.unbind();
 *
 */
 


public class Texture {
    
    private final static GLBindings bindings = GLBindings.get();
    
    private static final float MIN_LOD = -1000.0f;
    private static final float MAX_LOD =  1000.0f;
    
    private final int id;
    private final int target;
    private int depth;
    private int width;
    private int height;
    
    public Texture(int target) {
        this.id = glGenTextures();
        this.target = target;
        this.width = 0;
        this.height = 0;
        this.depth = 0;
    }
    
    
    public void tex2D(Image image) {
        
        this.width = image.width();
        this.height = image.height();
        int internalFormat; // GPU side
        int format; // client memory
        int stride = 4;
    
        switch (image.channels()) {
            case 3:
                internalFormat = format = GL_RGB;
                if ((width & 3) != 0) {
                    stride = 2 - (width & 1);
                }break;
            case 4:
                format = GL_RGBA;
                internalFormat = GL_RGBA8;
                break;
            default:
                throw new RuntimeException("Unsupported format");
        }
        
        glPixelStorei(GL_UNPACK_ALIGNMENT,stride);
        glTexImage2D(target,
                0,
                internalFormat,
                width,
                height,
                0,
                format,
                GL_UNSIGNED_BYTE,
                image.get());
    }
    
    public void tex2D(int[] rgba, int width, int height) {
        
        ByteBuffer buffer = MemoryUtil.memAlloc(rgba.length * 4);
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = rgba[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // r
                buffer.put((byte) ((pixel >> 8 ) & 0xFF)); // g
                buffer.put((byte) ((pixel      ) & 0xFF)); // b
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // a
            }
        }
        
        buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target,
                0,
                GL_RGBA8,
                width,
                height,
                0,
                GL_BGRA,
                GL_UNSIGNED_BYTE,
                buffer);
        
        MemoryUtil.memFree(buffer);
    
        this.width = width;
        this.height = height;
    }
    
    public void tex2D(int level, int internalFormat, int width, int height, int texelFormat, int type) {
        glTexImage2D(target,level,internalFormat,width,height,0,texelFormat,type,(ByteBuffer) null);
        this.width = width;
        this.height = height;
    }
    
    public void magFilter(int magFilter) {
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
    }
    
    public void minFilter(int minFilter) {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
    }
    
    public void filter(int minFilter, int magFilter) {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
    }
    
    public void filter(int filter) {
        filter(filter,filter);
    }
    
    public void wrapS(int wrapS) {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
    }
    
    public void wrapT(int wrapT) {
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
    }
    
    public void wrapR(int wrapR) {
        glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
    }
    
    public void wrapST(int wrapS, int wrapT) {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
    }
    
    public void wrapSTR(int wrapS, int wrapT, int wrapR) {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
    }
    
    public void wrapST(int wrap) {
        wrapST(wrap,wrap);
    }
    
    public void wrapSTR(int wrap) {
        wrapSTR(wrap,wrap,wrap);
    }
    
    public void generateMipMap(float lodBias, float min, float max) {
        glGenerateMipmap(target);
        glTexParameterf(target,GL_TEXTURE_MIN_LOD,min);
        glTexParameterf(target,GL_TEXTURE_MAX_LOD,max);
        glTexParameterf(target,GL_TEXTURE_LOD_BIAS,lodBias);
    }
    
    public void generateMipMap(float lodBias) {
        generateMipMap(lodBias, MIN_LOD, MAX_LOD);
    }
    
    public void generateMipMap() {
        generateMipMap(0.0f);
    }
    
    public void bind() {
        bindings.bindTexture(target,id);
    }
    
    public void unbind() {
        bindings.bindTexture(target,0);
    }
    
    public void delete() {
        glDeleteTextures(id);
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int depth() {
        return depth;
    }
    
    public int id() {
        return id;
    }
}
