package no.fredahl.engine.utility;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 *
 * @author Frederik Dahl
 * 18/10/2021
 */

public final class IO {
    
    static public final String INTERNAL_PREFIX;
    static public final String EXTERNAL_PREFIX;
    static public final Set<Character> COMMON_INVALID;
    
    static { try {
        INTERNAL_PREFIX = new File("").getAbsolutePath() + File.separator;
        EXTERNAL_PREFIX = System.getProperty("user.home") + File.separator;
    }catch (SecurityException e) {
        throw new SecurityException("No permission to access System Properties",e);
    }
        COMMON_INVALID = Set.of(
                '#','%','&','{','}','\\','<','>',
                '*','?', '/',' ', '$','!','\'',
                '\"',':','@','+','`','|','=');
    }
    
    private IO() {}
    
    public static ByteBuffer resourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        Path path = Path.of(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) { /* intentional */ }
            }
        } else {
            try (InputStream source = IO.class.getClassLoader().getResourceAsStream(resource);
                 ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) break;
                    if (buffer.remaining() == 0)
                        buffer = resizeBuffer(buffer,(int)(buffer.capacity() * 1.75f));
                }
            }
        }
        buffer.flip();
        return memSlice(buffer);
    }
    
    public static List<String> lineByLine(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }
    
    public static String projectPath(String filename, String... folders) {
        return path(filename,null,false,folders);
    }
    
    public static String externalPath(String filename, String... folders) {
        return path(filename,null,true,folders);
    }
    
    public static String path(String filename, String extension, boolean external, String... folders) {
        
        boolean directorySpecified = folders != null;
        boolean fileSpecified = filename != null;
        boolean usingExtension = extension != null;
        boolean appendPunctuation = false;
        
        if (!directorySpecified && !fileSpecified)
            throw new IllegalArgumentException("");
        
        if (fileSpecified) {
            if (usingExtension) {
                int extensionLength = extension.length();
                if (extensionLength == 0)
                    throw new IllegalArgumentException();
                appendPunctuation = extension.charAt(0) != '.';
                if (!appendPunctuation) {
                    if (extensionLength == 1)
                        throw new IllegalArgumentException();
                }
                for (int i = appendPunctuation ? 0 : 1; i < extensionLength; i++) {
                    if (COMMON_INVALID.contains(extension.charAt(i)))
                        throw new IllegalArgumentException();
                }
            }
            int filenameLength = filename.length();
            if (filenameLength == 0)
                throw new IllegalArgumentException();
            for (int i = 0; i < filenameLength; i++) {
                if (COMMON_INVALID.contains(filename.charAt(i)))
                    throw new IllegalArgumentException();
            }
        }
        
        StringBuilder path = new StringBuilder(external ? EXTERNAL_PREFIX : INTERNAL_PREFIX);
        
        if (directorySpecified) {
            int folderCount = folders.length;
            if (folderCount > 0) {
                for (String folderName : folders) {
                    int nameLength = folderName.length();
                    if (nameLength == 0)
                        throw new IllegalArgumentException();
                    for (int j = 0; j < nameLength; j++) {
                        if (COMMON_INVALID.contains(folderName.charAt(j)))
                            throw new IllegalArgumentException();
                    }
                    path.append(folderName).append(File.separator);
                }
            }
        }
        if (fileSpecified) {
            path.append(filename);
            if (usingExtension) {
                if (appendPunctuation)
                    path.append('.');
                path.append(extension);
            }
        }
        return path.toString();
    }
    
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
    
    private static boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }
    
}
