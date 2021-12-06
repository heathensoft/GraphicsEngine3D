package no.fredahl.engine.graphics.texture;

import no.fredahl.engine.graphics.GLBindings;
import org.joml.Math;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LOD;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Frederik Dahl
 * 04/12/2021
 */


public abstract class Texture {
    
    private final static GLBindings bindings = GLBindings.get();
    private int id;
    
    public abstract void bind();
    
    public abstract void unbind();
    
    public void free() {
        glDeleteTextures(id);
    }
    
    
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
    
    public enum Config {
        
        // Todo Add more when needed. Also only supports 1d 2d 3d
        LINEAR_REPEAT_2D(new DefaultConfig(GL_TEXTURE_2D)), // OpenGL Default
        NEAREST_REPEAT_2D(new DefaultConfig(GL_TEXTURE_2D,GL_UNSIGNED_BYTE,GL_REPEAT,GL_NEAREST)),
        ;
        
        no.fredahl.engine.graphics.Texture.TextureConfig config;
        
        Config(no.fredahl.engine.graphics.Texture.TextureConfig config) {
            this.config = config;
        }
        
        public no.fredahl.engine.graphics.Texture.TextureConfig get() {
            return config;
        }
        
        private static final class DefaultConfig implements no.fredahl.engine.graphics.Texture.TextureConfig {
            
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
