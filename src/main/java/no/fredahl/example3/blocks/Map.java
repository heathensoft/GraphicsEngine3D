package no.fredahl.example3.blocks;

import no.fredahl.engine.math.Camera;
import no.fredahl.engine.math.MathLib;
import no.fredahl.engine.utility.Utils;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.primitives.Planef;
import org.joml.primitives.Rayf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 18/02/2022
 */


public class Map {
 
    public final static int CHUNK_SIZE = 32;
    
    private String id;
    private BlockManager blockManager;
    
    private final Planef plane;
    private final Chunk[][] chunks;
    private final Vector2i cameraCoords;
    private final List<Chunk> centerChunks;
    private final List<Chunk> markedForRemoval;
    
    private final int[][] grid;
    private final int rows;
    private final int cols;
    
    private float accumulator;
    private boolean chunksAltered;
    
    
    public Map(String id, int chunkRows, int chunkCols) {
        this.id = id;
        rows = chunkRows * CHUNK_SIZE;
        cols = chunkCols * CHUNK_SIZE;
        grid = new int[rows][cols];
        cameraCoords = new Vector2i();
        chunks = new Chunk[chunkRows][chunkCols];
        Vector3f pointOnPlane = MathLib.vec3().set(0,0,0);
        Vector3f planeNormal = MathLib.vec3().set(0,0,1);
        plane = new Planef(pointOnPlane,planeNormal);
        centerChunks = new ArrayList<>(9);
        markedForRemoval = new ArrayList<>();
    }
    
    
    
    public void update(Camera camera, float dt) {
        
        if (chunksAltered) {
            accumulator = 1;
            chunksAltered = false;
        }
        else accumulator += dt;
        
        if (accumulator >= 1) {
            accumulator -= 1;
            Vector3f cameraPos = camera.position();
            updateCenterChunks(cameraPos);
            blockManager.buffers().distributeBlockBuffers(
                    centerChunks,cameraPos);
        }
        
    }
    
    private void updateCenterChunks(Vector3f cameraPosition) {
        int camX = (int)cameraPosition.x;
        int camY = (int)cameraPosition.y;
        if (camX == cameraCoords.x && camY == cameraCoords.y) return;
        cameraCoords.set(camX,camY);
        centerChunks.clear();
        int chunkX = camX / CHUNK_SIZE;
        int chunkY = camY / CHUNK_SIZE;
        int[][] adj = Utils.adjacentArray_9;
        for (int i = 0; i < adj.length; i++) {
            int xAdd = adj[i][0];
            int yAdd = adj[i][1];
            Chunk chunk = chunkFromChunkCoords(chunkX + xAdd,chunkY + yAdd);
            if (chunk != null) centerChunks.add(chunk);
        }
    }
    
    public int getBlock(Rayf ray) {
        Vector3f intersection = MathLib.vec3();
        if (MathLib.rayCast.intersectPlane(ray,plane,intersection))
            return getBlock((int)intersection.x,(int) intersection.y);
        return -1;
    }
    
    public int getBlock(int x, int y) {
        if (x < 0 || y < 0) return -1;
        if (x >= cols || y >= rows) return -1;
        return grid[y][x];
    }
    
    public Chunk chunkFromBlockCoords(int x, int y) {
        if (x < 0 || y < 0) return null;
        if (x >= cols || y >= rows) return null;
        int chunkX = x / CHUNK_SIZE;
        int chunkY = y / CHUNK_SIZE;
        return chunks[chunkY][chunkX];
    }
    
    public Chunk chunkFromChunkCoords(int x, int y) {
        if (x < 0 || y < 0) return null;
        int xMax = chunks[0].length - 1;
        int yMax = chunks.length - 1;
        if (x > xMax || y > yMax) return null;
        return chunks[y][x];
    }
    
    public List<Chunk> centerChunks() {
        return centerChunks;
    }
    
    public int getTypeID(int x, int y) {
        return BlockState.type(getBlock(x,y));
    }
    
    public void clearType(int x, int y) {
        Chunk chunk = chunkFromBlockCoords(x,y);
        if (chunk == null) return;
        int block = grid[y][x];
        if (BlockState.type(block) == 0 ) return;
        grid[y][x] = BlockState.setType(block,0);
        chunk.blockRemoved();
    }
    
    public void placeBlock(int typeID, int x, int y) {
        if (x < 0 || y < 0) return;
        if (x >= cols || y >= rows) return;
        int chunkX = x / CHUNK_SIZE;
        int chunkY = y / CHUNK_SIZE;
        Chunk chunk = chunks[chunkY][chunkX];
        if (chunk == null) {
            if (typeID == 0) return;
            chunk = new Chunk(
                    this,
                    chunkX * CHUNK_SIZE,
                    chunkY * CHUNK_SIZE);
            chunks[chunkY][chunkX] = chunk;
            grid[y][x] = typeID;
            chunk.blockInserted();
            chunksAltered = true; // todo
        }
        else {
            int block = grid[y][x];
            if (BlockState.sameType(block,typeID)) return;
            if (typeID == 0) chunk.blockRemoved();
            else if (BlockState.type(block) == 0) {
                chunk.blockInserted();
            } else chunk.blockAltered();
            grid[y][x] = BlockState.setType(block,typeID);
        }
        
    }
    
    public float distanceToPlane(Vector3f point) {
        return plane.distance(point.x,point.y,point.z);
    }
    
    public BlockManager blockManager() {
        return blockManager;
    }
    
    protected void setBlockManager(BlockManager blockManager) {
        this.blockManager = blockManager;
    }
    
    public String id() {
        return id;
    }
    
    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
    
    protected void setChunk(Chunk chunk, int x, int y) {
        chunks[y][x] = chunk;
    }
    
    protected int[][] grid() {
        return grid;
    }
    
    protected void setID(String id) {
        this.id = id;
    }
    
    protected Chunk[][] chunks() {
        return chunks;
    }
    
}
