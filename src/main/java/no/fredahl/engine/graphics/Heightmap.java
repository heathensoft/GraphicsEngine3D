package no.fredahl.engine.graphics;

import no.fredahl.engine.utility.noise.INoise;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * If you think of a heightmap. Seen from above. The X-axis points to the right, The Y-axis points down.
 * This is reflected in the 2D array. With rows corresponding to the Y-axis and cols to the X-axis.
 *
 * @author Frederik Dahl
 * 28/11/2021
 */


public class Heightmap {
    
    private final float[][] heightmap;
    private final int vertices;
    private final int triangles;
    private final int indices;
    private final float minHeight;
    private final float maxHeight;
    private final int rows;
    private final int cols;
    
    public Heightmap(INoise noise, int rows, int cols, float x0, float y0, float delta, float minH, float maxH) {
        this.rows = rows;
        this.cols = cols;
        this.minHeight = minH;
        this.maxHeight = maxH;
        this.vertices = rows * cols;
        int stripsReq = rows - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * cols;
        this.indices = (verticesPerStrip * stripsReq) + degensReq;
        this.triangles = 2 * (rows-1) * (cols-1);
        heightmap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            float y = y0 + delta * r;
            for (int c = 0; c < cols; c++) {
                float x = x0 + delta * c;
                heightmap[r][c] = minH + (maxH-minH) * noise.query(x,y);
            }
        }
    }
    
    public Heightmap(Image image, float minH, float maxH) {
        final int rows = image.height();
        final int cols = image.width();
        final int channels = image.channels();
        this.rows = rows;
        this.cols = cols;
        this.minHeight = minH;
        this.maxHeight = maxH;
        this.vertices = rows * cols;
        int stripsReq = rows - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * cols;
        this.indices = (verticesPerStrip * stripsReq) + degensReq;
        this.triangles = 2 * (rows-1) * (cols-1);
        if (channels < 3) throw new RuntimeException("supports 3 or 4 channel images");
        final ByteBuffer data = image.get();
        heightmap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heightmap[r][c] = minH + (maxH-minH) * colorToHeight(c,r,cols,channels,data);
            }
        }
        image.free();
    }
    
    public void normals(FloatBuffer buffer) {
        buffer.clear();
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        final float mid = (minHeight + maxHeight) / 2;
        float hu, hr, hd, hl;
        Vector3f normalVec = new Vector3f();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hr = c < cBounds ? heightmap[r][c+1] : mid;
                hd = r < rBounds ? heightmap[r+1][c] : mid;
                hu = r > 0 ? heightmap[r-1][c] : mid;
                hl = c > 0 ? heightmap[r][c-1] : mid;
                normalVec.z = hl - hr;
                normalVec.x = hd - hu;
                normalVec.y = 2.0f;
                normalVec.normalize();
                buffer.put(normalVec.x);
                buffer.put(normalVec.y);
                buffer.put(normalVec.z);
            }
        }
        buffer.flip();
    }
    
    public void normals(float[] normals) {
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        final float mid = (minHeight + maxHeight) / 2;
        int p = 0;
        float hu, hr, hd, hl;
        Vector3f normalVec = new Vector3f();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hr = c < cBounds ? heightmap[r][c+1] : mid;
                hd = r < rBounds ? heightmap[r+1][c] : mid;
                hu = r > 0 ? heightmap[r-1][c] : mid;
                hl = c > 0 ? heightmap[r][c-1] : mid;
                normalVec.z = hl - hr;
                normalVec.x = hd - hu;
                normalVec.y = 2.0f;
                normalVec.normalize();
                normals[p++] = normalVec.x;
                normals[p++] = normalVec.y;
                normals[p++] = normalVec.z;
            }
        }
    }
    
    public void indices(ShortBuffer buffer) {
        buffer.clear();
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) buffer.put((short) (r * rows));
            for (int c = 0; c < cols; c++) {
                buffer.put((short) ((r * rows) + c));
                buffer.put((short) (((r + 1) * rows) + c));
            }if (r < rows - 2) buffer.put((short) (((r + 1) * rows) + (cols - 1)));
        }
        buffer.flip();
    }
    
    public void indices(IntBuffer buffer) {
        buffer.clear();
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) buffer.put(r * rows);
            for (int c = 0; c < cols; c++) {
                buffer.put((r * rows) + c);
                buffer.put(((r + 1) * rows) + c);
            }if (r < rows - 2) buffer.put(((r + 1) * rows) + (cols - 1));
        }
        buffer.flip();
    }
    
    public void indices(short[] indices) {
        int p = 0;
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = (short) (r * rows);
            for (int c = 0; c < cols; c++) {
                indices[p++] = (short) ((r * rows) + c);
                indices[p++] = (short) (((r + 1) * rows) + c);
            }if (r < rows - 2) indices[p++] = (short) (((r + 1) * rows) + (cols - 1));
        }
    }
    
    public void indices(int[] indices) {
        int p = 0;
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = r * rows;
            for (int c = 0; c < cols; c++) {
                indices[p++] = (r * rows) + c;
                indices[p++] = ((r + 1) * rows) + c;
            }if (r < rows - 2) indices[p++] = ((r + 1) * rows) + (cols - 1);
        }
    }
    
    private static final int MAX_COLOUR = 256 * 256 * 256;
    
    private float colorToHeight(int x, int y, int w, int c, ByteBuffer data) {
        byte r = data.get(x * c + y * c * w);
        byte g = data.get(x * c + 1 + y * c * w);
        byte b = data.get(x * c + 2 + y * c * w);
        int rgb = ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
        return (float) rgb / (float) MAX_COLOUR;
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
    
    public float minHeight() {
        return minHeight;
    }
    
    public float maxHeight() {
        return maxHeight;
    }
    
    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
}
