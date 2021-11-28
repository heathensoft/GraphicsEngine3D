package no.fredahl.engine.utility;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.utility.noise.INoise;

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
        float[][] heightMap = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heightMap[r][c] = colorToHeight(c,r,cols,channels,data);
            }
        }
        image.free();
        return heightMap;
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
