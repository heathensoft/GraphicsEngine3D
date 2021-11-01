package no.fredahl.engine.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Math.round;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 * @author Frederik Dahl
 * 18/10/2021
 */


public class Image {
    
    private int w;
    private int h;
    private int c;
    
    private ByteBuffer data;
    
    private Image() { }
    
    
    public static Image fromResource(String imagePath) {
        return fromResource(imagePath,false);
    }
    
    public static Image fromResource(String imagePath, boolean v_flip) {
        
        Image image = new Image();
        ByteBuffer imageBuffer;
        
        
        try {
            imageBuffer = resourceToBuffer(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
    
            
            if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            } else System.out.println("OK with reason: " + stbi_failure_reason());
            System.out.println("Image width: " + w.get(0));
            System.out.println("Image height: " + h.get(0));
            System.out.println("Image components: " + c.get(0));
            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));
            
            stbi_set_flip_vertically_on_load(v_flip);
            
            // Decode the image
            image.data = stbi_load_from_memory(imageBuffer, w, h, c, 0);
            if (image.get() == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }
            
            image.w = w.get(0);
            image.h = h.get(0);
            image.c = c.get(0);
        }
        return image;
    }
    
    public static void premultiplyAlpha(Image image) {
        final ByteBuffer b = image.get();
        final int w = image.w;
        final int h = image.h;
        final int c = image.c;
        if (c == 4) {
            int stride = w * 4;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * stride + x * 4;
                    float alpha = (b.get(i + 3) & 0xFF) / 255.0f;
                    b.put(i + 0, (byte)round(((b.get(i + 0) & 0xFF) * alpha)));
                    b.put(i + 1, (byte)round(((b.get(i + 1) & 0xFF) * alpha)));
                    b.put(i + 2, (byte)round(((b.get(i + 2) & 0xFF) * alpha)));
                }
            }
        }
    }
    
    private static ByteBuffer resourceToBuffer(String path) throws IOException {
        ByteBuffer img;
        Path p = Path.of(path);
        final int eol = -1;
        if (Files.isReadable(p)) {
            try (SeekableByteChannel fc = Files.newByteChannel(p)) {
                img = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (true) {if (fc.read(img) == eol) break;}}}
        else { try (InputStream source = Image.class.getClassLoader().getResourceAsStream(path)
            ) { if (source == null) throw new IOException("Could not locate resource");
                try (ReadableByteChannel rbc = Channels.newChannel(source)
                ) { img = createByteBuffer(Byte.SIZE * 1024);
                    while (true) {
                        int bytes = rbc.read(img);
                        if (bytes == eol) break;
                        if (img.remaining() == 0) {
                            int newCapacity = (int)(img.capacity() * 1.75f);
                            ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
                            img.flip();
                            img = newBuffer.put(img);
                        }
                    }
                }
            }
        }
        return memSlice(img.flip());
    }
    
    
    public void toPNG(String path) {
        final int stride = w * c;
        stbi_write_png(path,w,h,c, data,stride);
    }
    
    public void free() {
        stbi_image_free(data);
        data = null;
    }
    
    public int width() {
        return w;
    }
    
    public int height() {
        return h;
    }
    
    public int channels() {
        return c;
    }
    
    public ByteBuffer get() {
        return data;
    }
    
    public int resolution() {
        return w * h;
    }
    
    public int size() {
        return resolution() * c;
    }
    
}
