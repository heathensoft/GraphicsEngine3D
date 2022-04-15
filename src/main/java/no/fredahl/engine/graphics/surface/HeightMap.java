package no.fredahl.engine.graphics.surface;

import no.fredahl.engine.graphics.Image;
import no.fredahl.engine.utility.FastNoiseLite;
import org.joml.Math;

/**
 * Float-precision height-map. Not complete
 *
 * @author Frederik Dahl
 * 01/04/2022
 *
 */


public class HeightMap {
    
    private final int rows;
    private final int cols;
    private float bsl; // baseline
    private float amp; // amplitude
    protected final float[][] map;
    
    /**
     * @param fnl Noise function
     * @param rows rows
     * @param cols cols
     * @param amp amplitude
     * @param bsl baseline
     * @param x noise start x
     * @param y noise start y
     * @param pow exponent (pow(n,pow))
     * @param dis discrete (terraces)
     * @param abs absolute (abs(n))
     * @param neg negate (n=-n)
     */
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp, float bsl,
                     float x, float y, float pow, int dis, boolean abs, boolean neg) {
        this.map = new float[rows][cols];
        this.rows = rows;
        this.cols = cols;
        this.bsl = bsl;
        this.amp = amp;
        if (pow != 1f) {
            pow = Math.max(0.01f,Math.min(pow,10.0f));
            dis = dis == 0 ? 1 : dis;
            if (dis == 1) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        float e = fnl.GetNoise(c+x,r+y);
                        e = abs ? Math.abs(e) : (e + 1) / 2f;
                        e = (float) java.lang.Math.pow(e,pow);
                        e = (e * 2 - 1) * amp + bsl;
                        map[r][c] = neg ? -e : e;
                    }
                }
            } else {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        float e = fnl.GetNoise(c+x,r+y);
                        e = abs ? Math.abs(e) : (e + 1) / 2f;
                        e = (float) java.lang.Math.pow(e,pow);
                        e = Math.round(e * dis) / (float)dis;
                        e = (e * 2 - 1) * amp + bsl;
                        map[r][c] = neg ? -e : e;
                    }
                }
            }
        } else {
            dis = dis == 0 ? 1 : dis;
            if (dis == 1) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        float e = fnl.GetNoise(c+x,r+y);
                        e = abs ? (Math.abs(e) * 2 - 1) : e;
                        e = e * amp + bsl;
                        map[r][c] = neg ? -e : e;
                    }
                }
            } else {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        float e = fnl.GetNoise(c+x,r+y);
                        e = abs ? Math.abs(e) : (e + 1) / 2f;
                        e = Math.round(e * dis) / (float)dis;
                        e = (e * 2 - 1) * amp + bsl;
                        map[r][c] = neg ? -e : e;
                    }
                }
            }
        }
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp, float bsl,
                     float x, float y, float pow, int dis, boolean abs) {
        this(fnl, rows, cols, amp, bsl, x, y, pow, dis, abs,false);
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp, float bsl,
                     float x, float y, float pow, boolean abs) {
        this(fnl, rows, cols, amp, bsl, x, y, pow,1, abs);
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp,
                     float x, float y, float pow, boolean abs) {
        this(fnl, rows, cols, amp,0, x, y, pow,1, abs);
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp, float pow, boolean abs) {
        this(fnl, rows, cols, amp,0,0,0, pow,1, abs);
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp, float pow) {
        this(fnl, rows, cols, amp,0,0,0, pow,1,false);
    }
    
    public HeightMap(FastNoiseLite fnl, int rows, int cols, float amp) {
        this(fnl, rows, cols, amp,0,0,0,1f,1,false);
    }
    
    public HeightMap(Image img, float amp) {
        this(new DepthMap8(img),amp);
    }
    
    public HeightMap(Image img, float amp, float bsl) {
        this(new DepthMap8(img),amp,bsl);
    }
    
    public HeightMap(DepthMap8 dm, float amp) {
        this(dm,amp,0f);
    }
    
    public HeightMap(DepthMap8 dm, float amp, float bsl) {
        this.amp = amp;
        this.bsl = bsl;
        this.rows = dm.rows();
        this.cols = dm.cols();
        this.map = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float d = ((dm.data()[c + r * cols] & 0xff) / 255f);
                map[r][c] = (2 * d - 1) * amp + bsl;
            }
        }
    }
    
    
    
    public void blend_add(HeightMap h1) {
        blend_add(h1,false);
    }
    
    public void blend_add(HeightMap h1, boolean repeat) {
        float[][] m0 = this.map;
        float[][] m1 = h1.map;
        // recalculate wave properties
        float max = this.max() + h1.max();
        float min = this.min() + h1.min();
        amp = (max - min) / 2f;
        bsl = max - amp;
        if (this.rows == h1.rows && this.cols == h1.cols) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    m0[r][c] = m0[r][c] + m1[r][c];
                }
            }
        } else {
            if (repeat) {
                for (int r = 0; r < rows; r++) {
                    int modR = r % h1.rows;
                    for (int c = 0; c < cols; c++) {
                        int modC = c % h1.cols;
                        m0[r][c] = m0[r][c] + m1[modR][modC];
                    }
                }
            } else {
                float ratioW = (float) h1.cols / cols;
                float ratioH = (float) h1.rows / rows;
                for (int r = 0; r < rows; r++) {
                    int rn = (int) (r * ratioH);
                    for (int c = 0; c < cols; c++) {
                        int cn = (int) (c * ratioW);
                        m0[r][c] = m0[r][c] + m1[rn][cn];
                    }
                }
            }
        }
    }
    
    public void blend_map_add(HeightMap h1) {
        this.blend_map_add(h1, new BlendMap(rows,cols));
    }
    
    public void blend_map_add(HeightMap h1, BlendMap bm) {
        this.blend_map_add(h1, bm, false);
    }
    
    public void blend_map_add(HeightMap h1, BlendMap bm, boolean repeat) {
        float[][] m0 = this.map;
        float[][] m1 = h1.map;
        // recalculate wave properties
        float max = this.max() + h1.max();
        float min = this.min() + h1.min();
        amp = (max - min) / 2f;
        bsl = max - amp;
        if (this.rows == h1.rows && this.cols == h1.cols) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    m0[r][c] = bm.add(m0[r][c],m1[r][c],r,c);
                }
            }
        } else {
            if (repeat) {
                for (int r = 0; r < rows; r++) {
                    int modR = r % h1.rows;
                    for (int c = 0; c < cols; c++) {
                        int modC = c % h1.cols;
                        m0[r][c] = bm.add(m0[r][c],m1[modR][modC],r,c);
                    }
                }
            } else {
                float ratioW = (float) h1.cols / cols;
                float ratioH = (float) h1.rows / rows;
                for (int r = 0; r < rows; r++) {
                    int rn = (int) (r * ratioH);
                    for (int c = 0; c < cols; c++) {
                        int cn = (int) (c * ratioW);
                        m0[r][c] = bm.add(m0[r][c],m1[rn][cn],r,c);
                    }
                }
            }
        }
    }
    
    public void blend_lerp_uniform(HeightMap h1, float i0, float i1) {
        blend_lerp_uniform(h1, i0, i1,false);
    }
    
    public void blend_lerp_uniform(HeightMap h1, float i0, float i1, boolean repeat) {
        float e;
        float[][] m0 = this.map;
        float[][] m1 = h1.map;
        // recalculate wave properties
        float max = (i0 * this.max() + i1 * h1.max()) / (i0 + i1);
        float min = (i0 * this.min() + i1 * h1.min()) / (i0 + i1);
        amp = (max - min) / 2f;
        bsl = max - amp;
        if (this.rows == h1.rows && this.cols == h1.cols) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    e = i0 * m0[r][c] + i1 * m1[r][c];
                    m0[r][c] = e / (i0 + i1);
                }
            }
        } else {
            // If the dimensions doesn't match, we either repeat
            // or we scale the other map to fit this maps' resolution.
            if (repeat) {
                for (int r = 0; r < rows; r++) {
                    int modR = r % h1.rows;
                    for (int c = 0; c < cols; c++) {
                        int modC = c % h1.cols;
                        e = i0 * m0[r][c] + i1 * m1[modR][modC];
                        m0[r][c] = e / (i0 + i1);
                    }
                }
            } else {
                float ratioW = (float) h1.cols / cols;
                float ratioH = (float) h1.rows / rows;
                for (int r = 0; r < rows; r++) {
                    int rn = (int) (r * ratioH);
                    for (int c = 0; c < cols; c++) {
                        int cn = (int) (c * ratioW);
                        e = i0 * m0[r][c] + i1 * m1[rn][cn];
                        m0[r][c] = e / (i0 + i1);
                    }
                }
            }
        }
    }
    
    public void blend_lerp_map(HeightMap hM, BlendMap bM, boolean repeat) {
    
    }
    
    public void smoothen(boolean includeEdges) {
    
    }
    
    public DepthMap8 depthMap8() {
        return new DepthMap8(this);
    }
    
    public DepthMap16 depthMap16() {
        return new DepthMap16(this);
    }
    
    public NormalMap normalMap() {
        return new NormalMap(new DepthMap8(this), amp);
    }
    
    public float baseline() {
        return bsl;
    }
    
    public float amplitude() {
        return amp;
    }
    
    float max() {
        return bsl + amp;
    }
    
    float min() {
        return bsl - amp;
    }
    
    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
    
    /*
    public void smoothen(boolean includeEdges) {
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
     */
}
