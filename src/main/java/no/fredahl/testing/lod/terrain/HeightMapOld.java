package no.fredahl.testing.lod.terrain;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.Image;
import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Low memory-footprint heightmap, with functions to generate normal-data,
 * indices, upload as texture, blending maps, smoothening etc.
 * The heightmap plane normal is in the positive y-direction.
 *
 * Use draw mode GL_TRIANGLE_STRIP.
 *
 * Can store normal-data and height into 1 float per. vertex using one
 * of the methods: norm_pY_compact(), norm_pY_compact_direct().
 * When doing so, it is important to scale the height value in the
 * shader with heightmap scale(). With a const or uniform.
 * Here one byte is assigned for nX, nY, nZ and the height pY.
 * And stored in a float. Use:
 *
 * glVertexAttribPointer(index, 4, GL_UNSIGNED_BYTE, true, stride, pointer);
 *
 * @author Frederik Dahl
 * 26/03/2022
 */


public class HeightMapOld {
    
    /*
    To self:
    
    Let you get the date of a section only. i.e. row 32 to 64
    
    Positions as short or something.
   
    */
    
    public boolean centered = false;
    private float scale = 1.0f;
    public final float epsilon;
    private final int rows;
    private final int cols;
    private final float[][] heightmap;
    float tileSize;
    
    
    
    
    public HeightMapOld(Image image) {
        rows = image.height();
        cols = image.width();
        epsilon = cols < rows ? 1f / (rows - 1) : 1f / (cols - 1);
        heightmap = new float[rows][cols];
        int channels = image.channels();
        if (channels < 3) throw new RuntimeException("supports 3 or 4 channel images");
        ByteBuffer data = image.get();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                heightmap[r][c] = colorToHeight(c,r,cols,channels,data);
            }
        }
    }
    
    public short[] idx_short() {
        int p = 0;
        short[] indices = new short[numIndices()];
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = (short) (r * rows);
            for (int c = 0; c < cols; c++) {
                indices[p++] = (short) ((r * rows) + c);
                indices[p++] = (short) (((r + 1) * rows) + c);
            }if (r < rows - 2) indices[p++] = (short) (((r + 1) * rows) + (cols - 1));
        } return indices;
    }
    
    public void idx_short_direct(BufferObject bufferObject) {
        ShortBuffer indices = null;
        try { indices = MemoryUtil.memAllocShort(numIndices());
            for (int r = 0; r < rows - 1; r++) {
                if (r > 0) indices.put((short) (r * rows));
                for (int c = 0; c < cols; c++) {
                    indices.put((short) ((r * rows) + c));
                    indices.put((short) (((r + 1) * rows) + c));
                }if (r < rows - 2) indices.put((short) (((r + 1) * rows) + (cols - 1)));
            } bufferObject.bufferData(indices.flip());
        }finally {
            if (indices != null)
                MemoryUtil.memFree(indices);
        }
    }
    
    public int[] idx_int() {
        int p = 0;
        int[] indices = new int[numIndices()];
        for (int r = 0; r < rows - 1; r++) {
            if (r > 0) indices[p++] = r * rows;
            for (int c = 0; c < cols; c++) {
                indices[p++] = (r * rows) + c;
                indices[p++] = ((r + 1) * rows) + c;
            }if (r < rows - 2) indices[p++] = ((r + 1) * rows) + (cols - 1);
        } return indices;
    }
    
    public void idx_int_direct(BufferObject bufferObject) {
        IntBuffer indices = null;
        try { indices = MemoryUtil.memAllocInt(numIndices());
            for (int r = 0; r < rows - 1; r++) {
                if (r > 0) indices.put(r * rows);
                for (int c = 0; c < cols; c++) {
                    indices.put((r * rows) + c);
                    indices.put(((r + 1) * rows) + c);
                }if (r < rows - 2) indices.put(((r + 1) * rows) + (cols - 1));
            } bufferObject.bufferData(indices.flip());
        }finally {
            if (indices != null){
                MemoryUtil.memFree(indices);
            }
        }
    }
    
    public float[] pXYZ_float() {
        int p = 0;
        float startX = 0;
        float startY = 0;
        if (centered) {
            if (cols < rows) {
                startY = -0.5f;
                startX = - epsilon * (cols - 1) / 2;
            } else {
                startX = -0.5f;
                startY = - epsilon * (rows - 1) / 2;
            }
            startX *= scale;
            startY *= scale;
        }
        float[] positions = new float[numVertices() * 3];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                //positions[p++] = startX + c * tileSize();
                positions[p++] = heightmap[r][c] * scale;
                //positions[p++] = startY + r * tileSize();
            }
        } return positions;
    }
    
    public void pXYZ_float_direct(BufferObject bufferObject) {
        FloatBuffer positions = null;
        try { positions = MemoryUtil.memAllocFloat(numVertices() * 3);
            
            // if centered
            float startX =- (cols - 1) / 2f * tileSize;
            float startY = -(rows - 1) / 2f * tileSize;
            
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    positions.put(startX + c * tileSize);
                    positions.put(heightmap[r][c] * tileSize);
                    positions.put(startY + r * tileSize);
                }
            }
            positions.flip();
            bufferObject.bufferData(positions);
        }finally {
            if (positions != null)
                MemoryUtil.memFree(positions);
        }
    }
    
    public float[] pXZ_float() {
        int p = 0;
        float startX = 0;
        float startY = 0;
        if (centered) {
            if (cols < rows) {
                startY = -0.5f;
                startX = - epsilon * (cols - 1) / 2;
            } else {
                startX = -0.5f;
                startY = - epsilon * (rows - 1) / 2;
            }
            startX *= scale;
            startY *= scale;
        }
        float[] positions = new float[numVertices() * 2];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                //positions[p++] = startX + c * tileSize();
                //positions[p++] = startY + r * tileSize();
            }
        } return positions;
    }
    
    public void pXZ_float_direct(BufferObject bufferObject) {
        FloatBuffer positions = null;
        try { positions = MemoryUtil.memAllocFloat(numVertices() * 2);
            float startX = 0;
            float startY = 0;
            if (centered) {
                if (cols < rows) {
                    startY = -0.5f;
                    startX = - epsilon * (cols - 1) / 2;
                } else {
                    startX = -0.5f;
                    startY = - epsilon * (rows - 1) / 2;
                }
                startX *= scale;
                startY *= scale;
            }
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    //positions.put(startX + c * tileSize());
                    //positions.put(startY + r * tileSize());
                }
            }
            positions.flip();
            bufferObject.bufferData(positions);
        }finally {
            if (positions != null)
                MemoryUtil.memFree(positions);
        }
    }
    
    public float[] norm_float() {
        float[] normals = new float[numVertices() * 3];
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
    
    public int generateIndicesInt(BufferObject idx_buffer) {
        short val = 0;
        final int strips = rows - 1;
        final int degens = 2 * (strips - 1);
        final int stripPoints = 2 * rows;
        final int points = stripPoints * strips;
        final int numIndices = points + degens;
        IntBuffer indices = null;
        try{ indices = MemoryUtil.memAllocInt(numIndices);
            for (int p = 1; p < points; p++) {
                val += (p % 2 == 1) ? rows : -(rows - 1);
                if (p % stripPoints == 0) {
                    int prev = indices.get(indices.position() - 1);
                    indices.put(prev);
                    indices.put(val);
                } indices.put(val);
            } idx_buffer.bufferData(indices.flip());
        } finally {
            if (indices != null)
                MemoryUtil.memFree(indices);
        } return numIndices;
    }
    
    public void norm_float_direct(BufferObject bufferObject) {
        FloatBuffer normals = null;
        try { normals = MemoryUtil.memAllocFloat(numVertices() * 3);
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
                    normalVec.x = (hl - hr);
                    normalVec.z = (hd - hu);
                    normalVec.y = 2;// * tileSize;
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
    
    public float[] norm_pY_compact() {
        float[] data = new float[numVertices()];
        final int cBounds = cols - 1;
        final int rBounds = rows - 1;
        int p = 0;
        float hu, hr, hd, hl;
        float nX, nY, nZ, pY;
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
                nX = normalVec.x;
                nY = normalVec.y;
                nZ = normalVec.z;
                pY = heightmap[r][c];
                data[p++] = compact(nX,nY,nZ,pY);
            }
        } return data;
    }
    
    public void norm_pY_compact_direct(BufferObject bufferObject) {
        FloatBuffer data = null;
        try { data = MemoryUtil.memAllocFloat(numVertices());
            final int cBounds = cols - 1;
            final int rBounds = rows - 1;
            float hu, hr, hd, hl;
            float nX, nY, nZ, pY;
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
                    nX = normalVec.x;
                    nY = normalVec.y;
                    nZ = normalVec.z;
                    pY = heightmap[r][c];
                    data.put(compact(nX,nY,nZ,pY));
                }
            }
            data.flip();
            bufferObject.bufferData(data);
        }finally {
            if (data != null)
                MemoryUtil.memFree(data);
        }
    }
    
    /**
     * Blend two heightmaps together, storing the result in this.
     * @param other other heightmap
     * @param i1 "this" influence / weight
     * @param i2 "other" influence / weight
     * @param repeat repeat or "stretch" to fit
     */
    public void blend(HeightMapOld other, float i1, float i2, boolean repeat) {
        
        float e;
        float[][] m1 = this.heightmap;
        float[][] m2 = other.heightmap;
        
        if (this.rows == other.rows && this.cols == other.cols) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    e = i1 * m1[r][c] + i2 * m2[r][c];
                    m1[r][c] = e / (i1 + i2);
                }
            }
        } else {
            // If the dimensions doesn't match, we either repeat
            // or we scale the other map to fit this maps' resolution.
            if (repeat) {
                for (int r = 0; r < rows; r++) {
                    int mod2r = r % other.rows;
                    for (int c = 0; c < cols; c++) {
                        int mod2c = c % other.cols;
                        e = i1 * m1[r][c] + i2 * m2[mod2r][mod2c];
                        m1[r][c] = e / (i1 + i2);
                    }
                }
            } else {
                float ratioW = (float) other.cols / cols;
                float ratioH = (float) other.rows / rows;
                for (int r = 0; r < rows; r++) {
                    int r2 = (int) (r * ratioH);
                    for (int c = 0; c < cols; c++) {
                        int c2 = (int) (c * ratioW);
                        e = i1 * m1[r][c] + i2 * m2[r2][c2];
                        m1[r][c] = e / (i1 + i2);
                    }
                }
            }
        }
    }
    
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
    
    /**
     * When centered, positional data will be relative to the
     * center of the map. Centered is false by default.
     * @param centered bool
     */
    public void setCentered(boolean centered) {
        this.centered = centered;
    }
    
    public boolean isCentered() {
        return centered;
    }
    
    /**
     * Scale is length of the longest side of the heightmap.
     * if rows = 4 and cols = 3, the length in the row direction
     * would be 1 * scale. Altering the scale alters the tile-size.
     * @param scale scale of heightmap
     */
    public void setScale(float scale) {
        // scale = tileSize / epsilon
        this.scale = Math.max(1,scale);
    }
    
    /**
     * Sets the length and width of a "tile".
     * This will alter the scale of the heightmap.
     * @param tileSize the gap between two vertices on an axis
     */
    public void setTileSize(float tileSize) {
        //this.scale = tileSize / epsilon;
        this.tileSize = tileSize;
    }
    
    /*
    public float tileSize() {
        return scale * epsilon;
    }
    
     */
    
    public float scale() {
        return scale;
    }
    
    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
    
    public int numVertices() {
        return rows * cols;
    }
    
    public int numTriangles() {
        return 2 * (rows-1) * (cols-1);
    }
    
    public int numIndices() {
        int stripsReq = rows - 1;
        int degensReq = 2 * (stripsReq - 1);
        int verticesPerStrip = 2 * cols;
        return (verticesPerStrip * stripsReq) + degensReq;
    }
    
    /**
     * Stores normals and height as a single float.
     * (unsigned byte precision)
     * @param nX normals X value
     * @param nY normals Y value
     * @param nZ normals Z value
     * @param pY height value (Y)
     * @return a float storing the data of a point
     */
    private float compact(float nX, float nY, float nZ, float pY) {
        final int r = (int) nX * 255;
        final int g = (int) nY * 255;
        final int b = (int) nZ * 255;
        final int a = (int) pY * 255;
        final int i = a << 24 | b << 16 | g << 8 | r;
        return Float.intBitsToFloat(i & 0xfeffffff);
    }
    
    private static final int MAX_COLOUR = 256 * 256 * 256;
    
    private float colorToHeight(int x, int y, int w, int c, ByteBuffer data) {
        final byte r = data.get(x * c + y * c * w);
        final byte g = data.get(x * c + 1 + y * c * w);
        final byte b = data.get(x * c + 2 + y * c * w);
        final int rgb = ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
        return (float) rgb / (float) MAX_COLOUR;
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
    
}
