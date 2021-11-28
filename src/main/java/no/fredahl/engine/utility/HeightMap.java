package no.fredahl.engine.utility;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.utility.noise.INoise;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

/**
 * Heightmaps should have values in the interval [0 - 1]
 * This is the case for maps created with images and raw FastNoiseLite. The INoise interface should also abide by this.
 * If you think of a heightmap. Seen from above. The X-axis points to the right, The Y-axis points down.
 * This is reflected in the 2D array. With rows corresponding to the Y-axis and cols to the X-axis.
 *
 * @author Frederik Dahl
 * 27/11/2021
 */


public class HeightMap {
    
    public static float[][] create(INoise noise, float startX, float startY, float increment, int rows, int cols) {
        float[][] heightMap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            float y = startY + increment * r;
            for (int c = 0; c < cols; c++) {
                float x = startX + increment * c;
                heightMap[r][c] = noise.query(x,y);
            }
        }
        return heightMap;
    }
    
    public static float[][] create(Image image) {
        final int rows = image.height();
        final int cols = image.width();
        final int channels = image.channels();
        if (channels < 3)
            throw new RuntimeException("supports 3 or 4 channel images");
        final ByteBuffer data = image.get();
        float[][] heightmap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heightmap[r][c] = colorToHeight(c,r,cols,channels,data);
            }
        }
        image.free();
        return heightmap;
    }
    
    public static float[] normals(float[][] heightmap) {
        final int rows = heightmap.length;
        final int cols = heightmap[0].length;
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        int pointer = 0;
        float hu, hr, hd, hl;
        float[] normals = new float[rows * cols * 3];
        Vector3f normalVec = new Vector3f();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r > 0 && r < rBounds && c > 0 && c < cBounds) {
                    hu = heightmap[r-1][c];
                    hr = heightmap[r][c+1];
                    hd = heightmap[r+1][c];
                    hl = heightmap[r][c-1];
                    // The ordering here might be off
                    normalVec.z = hl - hr;
                    normalVec.x = hd - hu;
                    normalVec.y = 2.0f;
                    normalVec.normalize();
                    normals[pointer++] = normalVec.x;
                    normals[pointer++] = normalVec.y;
                    normals[pointer++] = normalVec.z;
                } else {
                    normals[pointer++] = 0.0f;
                    normals[pointer++] = 1.0f;
                    normals[pointer++] = 0.0f;
                }
            }
        }
        return normals;
    }
    
    public static short[] indices(float[][] heightmap) {
        final int rows = heightmap.length;
        final int cols = heightmap[0].length;
        final int numStripsRequired = rows - 1;
        final int numDegeneratesRequired = 2 * (numStripsRequired - 1);
        final int verticesPerStrip = 2 * cols;
        final short[] indices = new short[(verticesPerStrip * numStripsRequired) + numDegeneratesRequired];
        int pointer = 0;
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0)
                indices[pointer++] = (short) (r * rows);
            for (int c = 0; c < cols; c++) {
                indices[pointer++] = (short) ((r * rows) + c);
                indices[pointer++] = (short) (((r + 1) * rows) + c);
            }
            if (r < rows - 2)
                indices[pointer++] = (short) (((r + 1) * rows) + (cols - 1));
        }
        return indices;
    }
    
    private static final int MAX_COLOUR = 256 * 256 * 256;
    // if you wanted the actual value of lets say red, you need to: short red = 0xff & r. Why? Java, that's why.
    private static float colorToHeight(int x, int y, int w, int c, ByteBuffer data) {
        byte r = data.get(x * c + y * c * w);
        byte g = data.get(x * c + 1 + y * c * w);
        byte b = data.get(x * c + 2 + y * c * w);
        int rgb = ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
        return (float) rgb / (float) MAX_COLOUR;
    }
}
