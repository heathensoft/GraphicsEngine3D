package no.fredahl.example3.blocks;

import no.fredahl.engine.utility.FileUtility;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 19/02/2022
 */


public class BlockSerializer {
    
    
    private final static Charset charset = StandardCharsets.UTF_8;
    private final static byte[] SERIALIZER_HEADER = "BlockMap:".getBytes(charset);
    private final static int MAP_HEADER_SIZE = Integer.BYTES + 2 + 2 * Short.BYTES;
    private final static int CHUNK_HEADER_SIZE = 2 * Integer.BYTES + Short.BYTES;
    private final static int BLOCK_SIZE = 3;
    
    // BLOCK: | byte type | byte x | byte y |
    // MAP_HEADER: | int bytes | byte chunkRows | byte chunkCols | short chunks | short idSize | [+id]
    // CHUNK_HEADER: | int x | int y | short blockCount |
    
    
    public static void serialize(Map map, String externalFolder) throws IOException {
        String fileName = map.id() + ".bin";
        Path path = Paths.get(externalFolder, fileName);
        SerializedMap serializedMap = new SerializedMap(map);
        ByteBuffer buffer = null;
        int bufferSize = SERIALIZER_HEADER.length + serializedMap.size();
        try {
            buffer = MemoryUtil.memAlloc(bufferSize);
            buffer.put(SERIALIZER_HEADER);
            serializedMap.get(buffer);
            buffer.flip();
            FileUtility.writer.write(path,buffer);
            
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public static Map deserialize(Path path) throws Exception {
        ByteBuffer buffer = FileUtility.reader.readToBuffer(path);
        byte[] headerBytes = new byte[SERIALIZER_HEADER.length];
        buffer.get(headerBytes);
        String header = new String(headerBytes,charset);
        if (!header.equals(new String(SERIALIZER_HEADER,charset)))
            throw new IOException("Corrupted binary");
        int bytes = buffer.getInt();
        final int rows = buffer.get();
        final int cols = buffer.get();
        final int chunks = buffer.getShort();
        final int idSize = buffer.getShort();
        byte[] idBytes = new byte[idSize];
        buffer.get(idBytes);
        String id = new String(idBytes,charset);
        Map map = new Map(id,rows,cols);
        int[][] grid = map.grid();
        try {
            for (int i = 0; i < chunks; i++) {
                int chunkX = buffer.getInt();
                int chunkY = buffer.getInt();
                int x0 = chunkX * Map.CHUNK_SIZE;
                int y0 = chunkY * Map.CHUNK_SIZE;
                short blocks = buffer.getShort();
                Chunk chunk = new Chunk(map,x0,y0,blocks);
                map.setChunk(chunk,chunkX,chunkY);
                for (int j = 0; j < blocks; j++) {
                    int type = buffer.get();
                    int x = buffer.get() + x0;
                    int y = buffer.get() + y0;
                    grid[y][x] = type;
                }
        
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Corrupted binary:",e);
        }
        return map;
    }
    
    private final static class SerializedMap {
        
        private int contentBytes;
        private final byte rows, cols;
        private final List<SerializedChunk> serializedChunks;
        private final byte[] id;
        
        
        public SerializedMap(Map map) {
            Chunk[][] chunks = map.chunks();
            this.id = map.id().getBytes(charset);
            this.rows = (byte) chunks.length;
            this.cols = (byte) chunks[0].length;
            this.serializedChunks = new ArrayList<>(rows * cols);
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    Chunk chunk = chunks[y][x];
                    if (chunk == null || chunk.noBlocks()) continue;
                    addChunk(map,chunk);
                }
            }
        }
        
        public void addChunk(Map map, Chunk chunk) {
            SerializedChunk serializedChunk = new SerializedChunk(map,chunk);
            contentBytes += serializedChunk.size();
            serializedChunks.add(serializedChunk);
        }
        
        public void get(ByteBuffer buffer) {
            buffer.putInt(contentBytes).put(rows).put(cols);
            buffer.putShort((short)(serializedChunks.size())).putShort((short) id.length).put(id);
            for (SerializedChunk serializedChunk : serializedChunks) {
                serializedChunk.get(buffer);
            }
        }
        
        public int size() {
            return contentBytes + MAP_HEADER_SIZE + id.length;
        }
    }
    
    private final static class SerializedChunk {
        
        private final byte[] blockData;
        private final int chunkX, chunkY;
        
        public SerializedChunk(Map map, Chunk chunk) {
            this.blockData = new byte[BLOCK_SIZE * chunk.blockCount()];
            int x0 = chunk.x0();
            int y0 = chunk.y0();
            this.chunkX = x0 / Map.CHUNK_SIZE;
            this.chunkY = y0 / Map.CHUNK_SIZE;
            int i = 0;
            int[][] blocks = map.grid();
            for (byte y = 0; y < Map.CHUNK_SIZE; y++) {
                for (byte x = 0; x < Map.CHUNK_SIZE; x++) {
                    int block = blocks[y0 + y][x0 + x];
                    if (block != 0) {
                        blockData[i++] = (byte) BlockState.type(block);
                        blockData[i++] = x;
                        blockData[i++] = y;
                    }
                }
            }
        }
        
        public void get(ByteBuffer buffer) {
            buffer.putInt(chunkX).putInt(chunkY).putShort((short) (blockData.length / 3));
            buffer.put(blockData);
        }
        
        public int size() {
            return CHUNK_HEADER_SIZE + blockData.length;
        }
        
    }
}
