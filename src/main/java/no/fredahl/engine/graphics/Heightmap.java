package no.fredahl.engine.graphics;

import no.fredahl.engine.utility.noise.INoise;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Frederik Dahl
 * 29/11/2021
 */


public class Heightmap {
    
    private final int rows;
    private final int cols;
    private final int vertices;
    private final int triangles;
    private final int indices;
    private final float startX;
    private final float startY;
    private final float epsilon;
    private final float amplitude;
    private final float[][] heightmap;
    
    /**
     * @param noise the noise implement
     * @param nX0 the noise query start position for x
     * @param nY0 the noise query start position for y
     * @param freq the noise frequency
     * @param amp the noise amplitude (distance from zero to wave top / bottom)
     * @param rows vertices in the y dimension
     * @param cols vertices in the x dimension
     */
    public Heightmap(INoise noise, float nX0, float nY0, float freq, float amp, int rows, int cols) {
        this.amplitude = amp;
        this.rows = rows;
        this.cols = cols;
        if (cols < rows) {
            startY = -0.5f;
            epsilon = 1f / (rows - 1);
            startX = - epsilon * (cols - 1) / 2;
        } else {
            startX = -0.5f;
            epsilon = 1f / (cols - 1);
            startY = - epsilon * (rows - 1) / 2;
        }
        int stripsReq = rows - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * cols;
        this.vertices = rows * cols;
        this.triangles = 2 * (rows-1) * (cols-1);
        this.indices = (verticesPerStrip * stripsReq) + degensReq;
        heightmap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            float y = nY0 + freq * r;
            for (int c = 0; c < cols; c++) {
                float x = nX0 + freq * c;
                heightmap[r][c] = -amp + (2 * amp) * noise.query(x,y);
            }
        }
    }
    
    public Heightmap(Image image, float amp) {
        final int rows = image.height();
        final int cols = image.width();
        final int channels = image.channels();
        this.amplitude = amp;
        this.rows = rows;
        this.cols = cols;
        if (cols < rows) {
            startY = -0.5f;
            epsilon = 1f / (rows - 1);
            startX = - epsilon * (cols - 1) / 2;
        } else {
            startX = -0.5f;
            epsilon = 1f / (cols - 1);
            startY = - epsilon * (rows - 1) / 2;
        }
        int stripsReq = rows - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * cols;
        this.vertices = rows * cols;
        this.triangles = 2 * (rows-1) * (cols-1);
        this.indices = (verticesPerStrip * stripsReq) + degensReq;
        if (channels < 3) throw new RuntimeException("supports 3 or 4 channel images");
        final ByteBuffer data = image.get();
        heightmap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float raw = colorToHeight(c,r,cols,channels,data);
                heightmap[r][c] = -amp + (2 * amp) * raw;
            }
        } image.free();
    }
    
    private static final int MAX_COLOUR = 256 * 256 * 256;
    
    private float colorToHeight(int x, int y, int w, int c, ByteBuffer data) {
        byte r = data.get(x * c + y * c * w);
        byte g = data.get(x * c + 1 + y * c * w);
        byte b = data.get(x * c + 2 + y * c * w);
        int rgb = ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
        return (float) rgb / (float) MAX_COLOUR;
    }
    
    public float[] positions() {
        int p = 0;
        float[] positions = new float[vertices * 3];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                positions[p++] = startX + c * epsilon;
                positions[p++] = heightmap[r][c];
                positions[p++] = startY + r * epsilon;
            }
        } return positions;
    }
    
    public void bufferPositionData(BufferObject bufferObject) {
        FloatBuffer positions = null;
        try { positions = MemoryUtil.memAllocFloat(vertices * 3);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    positions.put(startX + c * epsilon);
                    positions.put(heightmap[r][c]);
                    positions.put(startY + r * epsilon);
                }
            }
            positions.flip();
            bufferObject.bufferData(positions);
        }finally {
            if (positions != null)
                MemoryUtil.memFree(positions);
        }
    }
    
    public float[] normals() {
        float[] normals = new float[vertices * 3];
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        int p = 0;
        float hu, hr, hd, hl;
        Vector3f normalVec = new Vector3f();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hr = c < cBounds ? heightmap[r][c+1] : heightmap[r][c];
                hd = r < rBounds ? heightmap[r+1][c] : heightmap[r][c];
                hu = r > 0 ? heightmap[r-1][c] : heightmap[r][c];
                hl = c > 0 ? heightmap[r][c-1] : heightmap[r][c];
                normalVec.x = (hl - hr) / 2f;
                normalVec.z = (hd - hu) / 2f;
                normalVec.y = epsilon;
                normalVec.normalize();
                normals[p++] = normalVec.x;
                normals[p++] = normalVec.y;
                normals[p++] = normalVec.z;
            }
        } return normals;
    }
    
    public void bufferNormalsData(BufferObject bufferObject) {
        FloatBuffer normals = null;
        try { normals = MemoryUtil.memAllocFloat(vertices * 3);
            final int cBounds = cols - 1;
            final int rBounds = rows - 1;
            int p = 0;
            float hu, hr, hd, hl;
            Vector3f normalVec = new Vector3f();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    hr = c < cBounds ? heightmap[r][c+1] : heightmap[r][c];
                    hd = r < rBounds ? heightmap[r+1][c] : heightmap[r][c];
                    hu = r > 0 ? heightmap[r-1][c] : heightmap[r][c];
                    hl = c > 0 ? heightmap[r][c-1] : heightmap[r][c];
                    normalVec.x = (hl - hr) / 2f;
                    normalVec.z = (hd - hu) / 2f;
                    normalVec.y = epsilon;
                    normalVec.normalize();
                    normals.put(normalVec.x);
                    normals.put(normalVec.y);
                    normals.put(normalVec.z);
                }
            }
            normals.flip();
            bufferObject.bufferData(normals);
        }finally {
            if (normals != null)
                MemoryUtil.memFree(normals);
        }
    }
    
    public short[] indicesShort() {
        short[] indices = new short[this.indices];
        int p = 0;
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = (short) (r * rows);
            for (int c = 0; c < cols; c++) {
                indices[p++] = (short) ((r * rows) + c);
                indices[p++] = (short) (((r + 1) * rows) + c);
            }if (r < rows - 2) indices[p++] = (short) (((r + 1) * rows) + (cols - 1));
        } return indices;
    }
    
    public void bufferIndexDataShort(BufferObject bufferObject) {
        ShortBuffer indices = null;
        try { indices = MemoryUtil.memAllocShort(this.indices);
            for (int r = 0; r < rows - 1; r++) {
                if (r > 0) indices.put((short) (r * rows));
                for (int c = 0; c < cols; c++) {
                    indices.put((short) ((r * rows) + c));
                    indices.put((short) (((r + 1) * rows) + c));
                }if (r < rows - 2) indices.put((short) (((r + 1) * rows) + (cols - 1)));
            }
            indices.flip();
            bufferObject.bufferData(indices);
        }finally {
            if (indices != null)
                MemoryUtil.memFree(indices);
        }
    }
    
    public int[] indicesInt() {
        int[] indices = new int[this.indices];
        int p = 0;
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = r * rows;
            for (int c = 0; c < cols; c++) {
                indices[p++] = (r * rows) + c;
                indices[p++] = ((r + 1) * rows) + c;
            }if (r < rows - 2) indices[p++] = ((r + 1) * rows) + (cols - 1);
        } return indices;
    }
    
    public void bufferIndexDataInt(BufferObject bufferObject) {
        IntBuffer indices = null;
        try { indices = MemoryUtil.memAllocInt(this.indices);
            for (int r = 0; r < rows - 1; r++) {
                if (r > 0) indices.put(r * rows);
                for (int c = 0; c < cols; c++) {
                    indices.put((r * rows) + c);
                    indices.put(((r + 1) * rows) + c);
                }if (r < rows - 2) indices.put(((r + 1) * rows) + (cols - 1));
            }
            indices.flip();
            bufferObject.bufferData(indices);
        }finally {
            if (indices != null){
                MemoryUtil.memFree(indices);
            }
        }
    }
    
    public float get(int x, int y) {
        return heightmap[y][x];
    }
    
    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
    
    public int vertices() {
        return vertices;
    }
    
    public int triangles() {
        return triangles;
    }
    
    public int indices() {
        return indices;
    }
    
    public float startX() {
        return startX;
    }
    
    public float startY() {
        return startY;
    }
    
    public float epsilon() {
        return epsilon;
    }
    
    public float amplitude() {
        return amplitude;
    }
}
