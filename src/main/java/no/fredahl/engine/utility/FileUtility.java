package no.fredahl.engine.utility;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 * @author Frederik Dahl
 * 14/11/2021
 */


public class FileUtility {
    
    public static final WriterUtility writer = new WriterUtility();
    public static final ReaderUtility reader = new ReaderUtility();
    public static final ResourceUtility resource = new ResourceUtility();
    
    public static final class ReaderUtility {
    
        private ReaderUtility() {}
        
        public ByteBuffer readToBuffer(Path path) throws IOException {
            ByteBuffer buffer;
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path,StandardOpenOption.READ)) {
                buffer = BufferUtils.createByteBuffer((int)byteChannel.size() + 1);
                while (byteChannel.read(buffer) != -1); // *intentional*
            }buffer.flip();
            return memSlice(buffer);
        }
        
        public Stream<String> readLines(Path path, Charset charset) throws IOException {
            return Files.lines(path, charset);
        }
    
        public Stream<String> readLines(Path path) throws IOException {
            return Files.lines(path);
        }
        
        public List<String> readLinesToList(Path path, Charset charset) throws IOException {
            return readLines(path,charset).collect(Collectors.toList());
        }
    
        public List<String> readLinesToList(Path path) throws IOException {
            return readLines(path).collect(Collectors.toList());
        }
        
        public String toString(Path path, Charset charset) throws IOException {
            return Files.readString(path, charset);
        }
    
        public String toString(Path path) throws IOException {
            return Files.readString(path);
        }
    }
    
    public static final class WriterUtility {
    
        private WriterUtility() {}
    
        public void write(Path path, ByteBuffer source) throws IOException {
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)){
                byteChannel.write(source);
            }
        }
        
        public void write(Path path, byte[] bytes) throws IOException {
            Files.write(path,bytes);
        }
        
        public void write(Path path, String string, Charset charset) throws IOException {
            Files.writeString(path,string,charset);
        }
    
        public void write(Path path, String string) throws IOException {
            Files.writeString(path,string);
        }
    
        public void write(Path path, Iterable<? extends CharSequence> lines, Charset charset) throws IOException {
            Files.write(path,lines,charset);
        }
    
        public void write(Path path, Iterable<? extends CharSequence> lines) throws IOException {
            Files.write(path,lines);
        }
    
        public void append(Path path, String string, Charset charset) throws IOException {
            Files.writeString(path,string,charset,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
    
        public void append(Path path, String string) throws IOException {
            Files.writeString(path,string,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
    
        public void append(Path path, byte[] bytes) throws IOException {
            Files.write(path, bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
    
        public void append(Path path, Iterable<? extends CharSequence> lines, Charset charset) throws IOException {
            Files.write(path,lines,charset,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
    
        public void append(Path path, Iterable<? extends CharSequence> lines) throws IOException {
            append(path,lines, StandardCharsets.UTF_8);
        }
    
        public void append(Path path, ByteBuffer source) throws IOException {
            try (SeekableByteChannel byteChannel = Files.newByteChannel(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND)){
                byteChannel.write(source.flip());
            }
        }
    }
    
    public final static class ResourceUtility {
    
        private final Class<?> c = FileUtility.class;
        
        private ResourceUtility() {}
        
        
        public ByteBuffer toBuffer(String file, int size) throws IOException {
            ByteBuffer result;
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                try (ReadableByteChannel bc = Channels.newChannel(is)){
                    result = BufferUtils.createByteBuffer(Math.max(128,size));
                    while (true) {
                        int bytes = bc.read(result);
                        if (bytes == -1) break;
                        if (result.remaining() == 0) {
                            size = result.capacity() * 2;
                            ByteBuffer b = BufferUtils.createByteBuffer(size);
                            result = b.put(result.flip());
                        }
                    }
                }
            }
            return MemoryUtil.memSlice(result.flip());
        }
        
        // todo:
        /*
        public boolean copy(String file, Path to) throws IOException {
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                Files.copy(is,to);
            }
        }
        
         */
        
        public Stream<String> asLines(String file, Charset charset) throws IOException {
            Stream<String> result;
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                result = new BufferedReader(new InputStreamReader(is,charset)).lines();
            }return result;
        }
    
        public Stream<String> asLines(String file) throws IOException {
            return asLines(file,StandardCharsets.UTF_8);
        }
        
        public String toString(String file, Charset charset) throws IOException {
            StringBuilder builder = new StringBuilder();
            try (InputStream is = stream(file)){
                if (is == null) throw new IOException("Unable to read: " + file);
                BufferedReader bf = new BufferedReader(new InputStreamReader(is,charset));
                String line;
                while ((line = bf.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
            }
            return builder.toString();
        }
    
        public String toString(String file) throws IOException {
            return toString(file,StandardCharsets.UTF_8);
        }
        
        private InputStream stream(String file) {
            return c.getClassLoader().getResourceAsStream(file);
        }
    }
}
