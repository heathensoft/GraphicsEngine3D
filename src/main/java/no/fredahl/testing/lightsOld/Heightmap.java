package no.fredahl.testing.lightsOld;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.Image;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Well optimized. Memory usage (buffer size) and performance.
 * Its mesh is one continuous triangle strip. So use GL_TRIANGLE_STRIP to render.
 * You can generate a heightmap from an image or any noise function.
 *
 * Its shortest axis is normalized to unit size 1. so for a map with rows = 100, cols = 50, the y-axis length would be 1,
 * and the x-axis length would be 2. The map position is its center.
 * Epsilon is the normalized grid size. If you have 2 rows and 2 cols (4 vertices or one "tile") this would be 1.
 *
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
     * @param ng the noise implement
     * @param nX0 the noise query start position for x
     * @param nY0 the noise query start position for y
     * @param freq the noise frequency
     * @param amp the noise amplitude (distance from zero to wave top / bottom)
     * @param rows vertices in the y dimension
     * @param cols vertices in the x dimension
     */
    public Heightmap(NoiseGenerator ng, float nX0, float nY0, float freq, float amp, int rows, int cols) {
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
                heightmap[r][c] = -amp + (2 * amp) * ng.query(x,y);
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
    
    public float[] positionsXZ() {
        int p = 0;
        float[] positions = new float[vertices * 2];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                positions[p++] = startX + c * epsilon;
                positions[p++] = startY + r * epsilon;
            }
        } return positions;
    }
    
    public void bufferPositionDataXZ(BufferObject bufferObject) {
        FloatBuffer positions = null;
        try { positions = MemoryUtil.memAllocFloat(vertices * 2);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    positions.put(startX + c * epsilon);
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
        int p = 0;
        short[] indices = new short[this.indices];
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
        int p = 0;
        int[] indices = new int[this.indices];
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
    
    public void smoothen(boolean includeEdges){
        float sum;
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        if (includeEdges) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    sum = 0;
                    if (r == 0) {
                        if (c == 0) {
                            for (byte[] adj : nw) {
                                sum += heightmap[r+adj[0]][c+adj[1]];
                            } heightmap[r][c] = sum/4;
                        } else if (c == cBounds) {
                            for (byte[] adj : ne) {
                                sum += heightmap[r+adj[0]][c+adj[1]];
                            } heightmap[r][c] = sum/4;
                        } else { for (byte[] adj : n) {
                            sum += heightmap[r+adj[0]][c+adj[1]];
                        } heightmap[r][c] = sum/6;
                        } continue;
                    } if (r == rBounds) {
                        if (c == 0) {
                            for (byte[] adj : sw) {
                                sum += heightmap[r+adj[0]][c+adj[1]];
                            } heightmap[r][c] = sum/4;
                        } else if (c == cBounds) {
                            for (byte[] adj : se) {
                                sum += heightmap[r+adj[0]][c+adj[1]];
                            } heightmap[r][c] = sum/4;
                        } else { for (byte[] adj : s) {
                            sum += heightmap[r+adj[0]][c+adj[1]];
                        } heightmap[r][c] = sum/6;
                        } continue;
                    } if (c == 0) {
                        for (byte[] adj : w) {
                            sum += heightmap[r+adj[0]][c+adj[1]];
                        } heightmap[r][c] = sum/6;
                    } else if (c == cBounds) {
                        for (byte[] adj : e) {
                            sum += heightmap[r+adj[0]][c+adj[1]];
                        } heightmap[r][c] = sum/6;
                    } else { for (byte[] adj : m) {
                        sum += heightmap[r+adj[0]][c+adj[1]];
                    } heightmap[r][c] = sum/9; }
                }
            }
        } else {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (r > 0 && r < rBounds && c > 0 && c < cBounds) {
                        sum = 0; for (byte[] adj : m) {
                            sum += heightmap[r+adj[0]][c+adj[1]];
                        } heightmap[r][c] = sum/9;
                    }
                }
            }
        }
    }
    
    private static final byte[][] m = {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1},{ 0, 0},{ 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
    };
    
    private static final byte[][] n = {
            { 0,-1},{ 0, 0},{ 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
    };
    
    private static final byte[][] ne = {
            { 0,-1},{ 0, 0},
            { 1,-1},{ 1, 0}
    };
    
    private static final byte[][] e = {
            {-1,-1},{-1, 0},
            { 0,-1},{ 0, 0},
            { 1,-1},{ 1, 0}
    };
    
    private static final byte[][] se = {
            {-1,-1},{-1, 0},
            { 0,-1},{ 0, 0}
    };
    
    private static final byte[][] s = {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1},{ 0, 0},{ 0, 1}
    };
    
    private static final byte[][] sw = {
            {-1, 0},{-1, 1},
            { 0, 0},{ 0, 1},
    };
    
    private static final byte[][] w = {
            {-1, 0},{-1, 1},
            { 0, 0},{ 0, 1},
            { 1, 0},{ 1, 1}
    };
    
    private static final byte[][] nw = {
            { 0, 0},{ 0, 1},
            { 1, 0},{ 1, 1}
    };
    
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
