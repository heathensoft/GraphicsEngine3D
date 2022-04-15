package no.fredahl.engine.graphics;

import no.fredahl.engine.math.MathLib;
import org.joml.Vector3f;
import org.joml.Math;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Remember to free Images.
 *
 * @author Frederik Dahl
 * 25/11/2021
 */


public class Image {
    
    private final int width;
    private final int height;
    private final int channels;
    
    private ByteBuffer data;
    
    public Image(ByteBuffer rawData) {
        this(rawData,false);
    }
    
    public Image(ByteBuffer rawData, boolean flip) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            if (!stbi_info_from_memory(rawData, w, h, c))
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            /*
            System.out.println("Image width: " + w.get(0));
            System.out.println("Image height: " + h.get(0));
            System.out.println("Image components: " + c.get(0));
            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(rawData));
             */
            stbi_set_flip_vertically_on_load(flip);
            // Decode the image
            data = stbi_load_from_memory(rawData, w, h, c, 0);
            if (data == null)
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            width = w.get(0);
            height = h.get(0);
            channels = c.get(0);
        }
    }
    
    
    public static void premultiplyAlpha(Image image) {
        final ByteBuffer b = image.get();
        final int w = image.width;
        final int h = image.height;
        final int c = image.channels;
        if (c == 4) {
            int stride = w * 4;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * stride + x * 4;
                    float alpha = (b.get(i + 3) & 0xFF) / 255.0f;
                    b.put(i + 0, (byte)Math.round(((b.get(i + 0) & 0xFF) * alpha)));
                    b.put(i + 1, (byte)Math.round(((b.get(i + 1) & 0xFF) * alpha)));
                    b.put(i + 2, (byte)Math.round(((b.get(i + 2) & 0xFF) * alpha)));
                }
            }
        }
    }
    
    public static void flattenNormalmap(Image image, String path, float maxAngleDeg) {
        final ByteBuffer buffer = image.get();
        final int w = image.width;
        final int h = image.height;
        final int c = image.channels;
        if (c >= 3) {
            final float n = 1 / 255f;
            maxAngleDeg = Math.max(0,Math.min(90, maxAngleDeg));
            final float maxAngle = Math.toRadians(maxAngleDeg);
            Vector3f flatSurface = new Vector3f(0,0,1);
            int stride = w * c;
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    
                    int i = row * stride + col * c;
                    int r = (buffer.get(i) & 0xFF);
                    int g = (buffer.get(i + 1) & 0xFF);
                    int b = (buffer.get(i + 2) & 0xFF);
                    
                    Vector3f color = MathLib.vec3();
                    color.set(r * n,g * n,b * n);
    
                    Vector3f normal = MathLib.vec3();
                    normal.x = (color.x - 0.5f) * 2f;
                    normal.y = (color.y - 0.5f) * 2f;
                    normal.z = (color.z - 0.5f) * 2f;
                    
                    float angle = flatSurface.angle(normal);
                    
                    if (angle > maxAngle) {
                        
                        float rotation = maxAngle - angle;
                        Vector3f cross = MathLib.vec3();
                        flatSurface.cross(normal,cross);
                        normal.rotateAxis(rotation,cross.x,cross.y,cross.z);
                        normal.normalize();
                        
                        color.x = normal.x * 0.5f + 0.5f;
                        color.y = normal.y * 0.5f + 0.5f;
                        color.z = normal.z * 0.5f + 0.5f;
                        
                        r = Math.round(color.x * 255);
                        g = Math.round(color.y * 255);
                        b = Math.round(color.z * 255);
    
                        buffer.put(i, (byte) r);
                        buffer.put(i + 1, (byte) g);
                        buffer.put(i + 2, (byte) b);
                    }
                }
            }
            image.toPNG(path);
        }
    }
    
    public void toPNG(String path) {
        final int stride = width * channels;
        stbi_write_png(path,width,height,channels,data,stride);
    }
    
    public void free() {
        stbi_image_free(data);
        data = null;
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int channels() {
        return channels;
    }
    
    public ByteBuffer get() {
        return data;
    }
    
    public int resolution() {
        return width * height;
    }
    
    public int size() {
        return resolution() * channels;
    }
}
