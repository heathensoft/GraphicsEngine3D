package no.fredahl.example1;

import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.graphics.Image;
import org.joml.Math;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static no.fredahl.example1.Texture.Config.LINEAR_REPEAT_2D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Frederik Dahl
 * 17/10/2021
 */


public class Texture {
    
    /**
     * @see Config
     */
    public interface TextureConfig {
        
        /**
         * @param fi internal format texture
         * @param w texture width
         * @param h texture height
         * @param f buffer format
         * @param b pixel buffer
         */
        
        void upload(int fi,int w,int h,int f, ByteBuffer b);
    }
    private final static GLBindings bindings = GLBindings.get();
    private final int id;
    private final int w;
    private final int h;
    
    
    public Texture(Image image) {
        this(image, LINEAR_REPEAT_2D);
    }
    
    public Texture(Image image, Config config) {
        this(image,config.get());
    }
    
    public Texture(int[] rgba, int width, int height) {
        this(rgba,width,height, LINEAR_REPEAT_2D);
    }
    
    public Texture(int[] rgba, int width, int height, Config config) {
        this(rgba,width,height,config.get());
    }
    
    public Texture(Image image, TextureConfig config) {
        w = image.width();
        h = image.height();
        int f;  // image format
        int fi; // internal format
        int stride = 4;
    
        switch (image.channels()) {
            case 3:
                fi = f = GL_RGB;
                if ((w & 3) != 0) {
                    stride = 2 - (w & 1);
                }
                break;
            case 4:
                f = GL_RGBA;
                fi = GL_RGBA8;
                break;
            default: throw new RuntimeException("Unsupported format");
        }
        id = glGenTextures();
        bindings.bindTexture(GL_TEXTURE_2D,id);
        glPixelStorei(GL_UNPACK_ALIGNMENT,stride);
        config.upload(fi, w, h, f, image.get());
        bindings.bindTexture(GL_TEXTURE_2D,0);
        image.free();
    }
    
    public Texture(int[] rgba, int width, int height, TextureConfig config) {
        
        w = width;
        h = height;
    
        ByteBuffer buffer = MemoryUtil.memAlloc(rgba.length * 4);
    
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = rgba[y * w + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // r
                buffer.put((byte) ((pixel >> 8 ) & 0xFF)); // g
                buffer.put((byte) ((pixel      ) & 0xFF)); // b
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // a
            }
        }
        buffer.flip();
        id = glGenTextures();
        bindings.bindTexture(GL_TEXTURE_2D,id);
        config.upload(GL_RGBA8,w,h,GL_RGBA,buffer);
        bindings.bindTexture(GL_TEXTURE_2D,0);
        MemoryUtil.memFree(buffer);
    }
    
    public void bind() {
        bindings.bindTexture(GL_TEXTURE_2D,id);
    }
    
    public void unbind() {
        bindings.bindTexture(GL_TEXTURE_2D,0);
    }
    
    public void free() {
        glDeleteTextures(id);
    }
    
    public int width() {
        return w;
    }
    
    public int height() {
        return h;
    }
    
    
    
    
    
    public enum Config {
        
        // Todo Add more when needed. Also only supports 1d 2d 3d
        LINEAR_REPEAT_2D(new DefaultConfig(GL_TEXTURE_2D)), // OpenGL Default
        NEAREST_REPEAT_2D(new DefaultConfig(GL_TEXTURE_2D,GL_UNSIGNED_BYTE,GL_REPEAT,GL_NEAREST)),
        ;
        
        TextureConfig config;
        
        Config(TextureConfig config) {
            this.config = config;
        }
        
        public TextureConfig get() {
            return config;
        }
    
        private static final class DefaultConfig implements TextureConfig {
        
            // https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml
        
            private static final float MIN_LOD = -1000.0f;
            private static final float MAX_LOD =  1000.0f;
            private static final float MIPMAP_NONE = -3000.0f;
        
            final int target;
            final int data_type;
            final int wrap_s;
            final int wrap_t;
            final int min_filter;
            final int mag_filter;
        
            final float lod_bias;
        
            public DefaultConfig(int t, int dt, int ws, int wt, int mif, int maf, float lod) {
                lod_bias = lod == MIPMAP_NONE ? lod : Math.clamp(MIN_LOD,MAX_LOD,lod);
                target = t;
                data_type = dt;
                wrap_s = ws;
                wrap_t = wt;
                min_filter = mif;
                mag_filter = maf;
            }
        
            public DefaultConfig(int t, int dt, int ws, int wt, int mif, int maf) {
                this(t,dt,ws,wt,mif,maf,MIPMAP_NONE);
            }
        
            public DefaultConfig(int t, int dt, int w, int f, float lod) {
                this(t,dt,w,w,f,f,lod);
            }
        
            public DefaultConfig(int t, int dt, int w, int f) {
                this(t,dt,w,w,f,f,MIPMAP_NONE);
            }
        
            public DefaultConfig(int t, int dt, float lod) {
                this(t,dt,GL_REPEAT,GL_LINEAR,lod);
            }
        
            public DefaultConfig(int t, int dt) {
                this(t,dt,MIPMAP_NONE);
            }
        
            public DefaultConfig(int t, float lod) {
                this(t,GL_UNSIGNED_BYTE,lod);
            }
        
            public DefaultConfig(int t) {
                this(t,GL_UNSIGNED_BYTE,MIPMAP_NONE);
            }
            
            
            @Override
            public void upload(int fi, int w, int h, int f, ByteBuffer b) {
                glTexParameteri(target, GL_TEXTURE_MAG_FILTER, mag_filter);
                glTexParameteri(target, GL_TEXTURE_MIN_FILTER, min_filter);
                glTexParameteri(target, GL_TEXTURE_WRAP_S, wrap_s);
                glTexParameteri(target, GL_TEXTURE_WRAP_T, wrap_t);
                switch (target) {
                    case GL_TEXTURE_1D: // height = border and must be 0
                        glTexImage1D(target,0,fi,w,h,f,data_type,b);
                        break;
                    case GL_TEXTURE_2D:
                        glTexImage2D(target,0,fi,w,h,0,f,data_type,b);
                        break;
                    case GL_TEXTURE_3D:
                        glTexImage3D(target,0,fi,w,h,0,0,f,data_type,b);
                        break;
                }
                if (lod_bias != MIPMAP_NONE) {
                    glGenerateMipmap(target);
                    glTexParameterf(target,GL_TEXTURE_MIN_LOD,MIN_LOD);
                    glTexParameterf(target,GL_TEXTURE_MAX_LOD,MAX_LOD);
                    glTexParameterf(target,GL_TEXTURE_LOD_BIAS,lod_bias);
                }
            }
        }
    }
}
