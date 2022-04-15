package no.fredahl.engine.graphics.surface;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.utility.FastNoiseLite;
import org.joml.Math;

import java.util.Random;

/**
 * Used to blend two Heightmaps
 * All values are normalized [0-1]
 *
 * @author Frederik Dahl
 * 02/04/2022
 */


public class BlendMap {
    
    private final int rows;
    private final int cols;
    private final float[][] map;
    private float avg = 0;
    
    
    public BlendMap(Image img) {
        this(new DepthMap8(img));
    }
    
    public BlendMap(DepthMap8 dm) {
        this.rows = dm.rows();
        this.cols = dm.cols();
        this.map = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                avg += map[r][c] = ((dm.data()[c + r * cols] & 0xff) / 255f);
            }
        } avg /= rows * cols;
    }
    
    public BlendMap(HeightMap hm) {
        this.rows = hm.rows();
        this.cols = hm.cols();
        this.map = new float[rows][cols];
        float[][] m = hm.map;
        float amp = hm.amplitude();
        float bsl = hm.baseline();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                avg += map[r][c] = ((m[r][c] - bsl) / amp + 1) / 2f;
            }
        } avg /= rows * cols;
    }
    
    public BlendMap(int rows, int cols) {
        this(rows,cols,new FastNoiseLite(new Random().nextInt()));
    }
    
    public BlendMap(int rows, int cols, FastNoiseLite fnl) {
        this(rows,cols,fnl,false);
    }
    
    public BlendMap(int rows, int cols, FastNoiseLite fnl, boolean abs) {
        this.rows = rows;
        this.cols = cols;
        this.map = new float[rows][cols];
        if (abs) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    avg += map[r][c] = Math.abs(fnl.GetNoise(c,r));
                }
            }
        } else {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    avg += map[r][c] = (fnl.GetNoise(c,r) + 1)/2f;
                }
            }
        } avg /= rows * cols;
    }
    
    public void balance(float low, float high) {
        float adj = (high / (low + high)) - avg;
        int itr = 0;
        while (Math.abs(adj) > 0.10f && itr < 3) {
            avg = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    float v = map[r][c];
                    v -= (v * adj);
                    v = Math.max(0f,Math.min(v,1f));
                    avg += map[r][c] = v;
                }
            } itr++;
            avg /= rows * cols;
            adj = avg - (low / (low + high));
        }
    }
    
    public float lerp(float h0, float h1, int r, int c) {
        return Math.lerp(h0,h1,m(r,c));
    }
    
    /**
     * @param h0 base
     * @param h1 add
     * @param r row
     * @param c col
     * @return h0 + h1 * blendMap(r,c)
     */
    public float add(float h0, float h1, int r, int c) {
        return h0 + h1 * m(r,c);
    }
    
    private float m(int r, int c) {
        return map[r%rows][c%cols];
    }
    
}
