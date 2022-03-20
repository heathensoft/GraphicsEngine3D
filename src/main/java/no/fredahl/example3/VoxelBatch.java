package no.fredahl.example3;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.GLBindings;
import no.fredahl.engine.utility.Disposable;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @author Frederik Dahl
 * 18/01/2022
 */


public class VoxelBatch implements Disposable {
    
    private static final GLBindings bindings = GLBindings.get();
    private static final int MAX_SIZE = Short.MAX_VALUE >> 2; // 8191
    private static final int FACE_STRIDE = 3 + 1 + 1; // pos | color | flags
    private static final int VOXEL_STRIDE = FACE_STRIDE * 6;
    private final FloatBuffer vertexData;
    private final BufferObject staticVBO;
    private final BufferObject instanceVBO;
    private final BufferObject ebo;
    private final int vao;
    private final int size;
    private boolean rendering;
    private int renderCalls;
    private int facesCount;
    
    
    
    public VoxelBatch(int capacity) {
        
        size = Math.min(capacity,MAX_SIZE);
        vertexData = MemoryUtil.memAllocFloat(FACE_STRIDE * size);
        vao = glGenVertexArrays();
        bindings.bindAttributeArray(vao);
        ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        ebo.bind();
        ebo.bufferData(generateIndices(size));
        instanceVBO = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        instanceVBO.bind();
        instanceVBO.bufferData((long) vertexData.capacity() * Float.BYTES);
        staticVBO = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        staticVBO.bind();
        FloatBuffer faceData = null;
        int faceDataSize = 6 * 4 * 3; // 6 faces 4 vertices 3 floats
        try {
            
            faceData = MemoryUtil.memAllocFloat(faceDataSize);
            
            Face top = Face.TOP;
            Face lef = Face.LEFT;
            Face rig = Face.RIGHT;
            Face bot = Face.BOTTOM;
            Face fro = Face.FRONT;
            Face rea = Face.REAR;
    
            faceData.put(top.x1).put(top.y1).put(top.z1);
            faceData.put(lef.x1).put(lef.y1).put(lef.z1);
            faceData.put(rig.x1).put(rig.y1).put(rig.z1);
            faceData.put(bot.x1).put(bot.y1).put(bot.z1);
            faceData.put(fro.x1).put(fro.y1).put(fro.z1);
            faceData.put(rea.x1).put(rea.y1).put(rea.z1);
    
            faceData.put(top.x2).put(top.y2).put(top.z2);
            faceData.put(lef.x2).put(lef.y2).put(lef.z2);
            faceData.put(rig.x2).put(rig.y2).put(rig.z2);
            faceData.put(bot.x2).put(bot.y2).put(bot.z2);
            faceData.put(fro.x2).put(fro.y2).put(fro.z2);
            faceData.put(rea.x2).put(rea.y2).put(rea.z2);
    
            faceData.put(top.x3).put(top.y3).put(top.z3);
            faceData.put(lef.x3).put(lef.y3).put(lef.z3);
            faceData.put(rig.x3).put(rig.y3).put(rig.z3);
            faceData.put(bot.x3).put(bot.y3).put(bot.z3);
            faceData.put(fro.x3).put(fro.y3).put(fro.z3);
            faceData.put(rea.x3).put(rea.y3).put(rea.z3);
    
            faceData.put(top.x4).put(top.y4).put(top.z4);
            faceData.put(lef.x4).put(lef.y4).put(lef.z4);
            faceData.put(rig.x4).put(rig.y4).put(rig.z4);
            faceData.put(bot.x4).put(bot.y4).put(bot.z4);
            faceData.put(fro.x4).put(fro.y4).put(fro.z4);
            faceData.put(rea.x4).put(rea.y4).put(rea.z4);
            
            staticVBO.bufferData(faceData.flip());
            
        }finally {
            if (faceData != null) {
                MemoryUtil.memFree(faceData);
            }
        }
        
        
        staticVBO.bind();
    
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 18*Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 18*Float.BYTES, 3*Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 18*Float.BYTES, 6*Float.BYTES);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 18*Float.BYTES, 9*Float.BYTES);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 3, GL_FLOAT, false, 18*Float.BYTES, 12*Float.BYTES);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 3, GL_FLOAT, false, 18*Float.BYTES, 15*Float.BYTES);
        
        instanceVBO.bind();
        
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 3, GL_FLOAT, false, 5*Float.BYTES, 0);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 4, GL_UNSIGNED_BYTE, true, 5*Float.BYTES, 3*Float.BYTES);
        glEnableVertexAttribArray(8);
        glVertexAttribPointer(8, 1, GL_FLOAT, false, 5*Float.BYTES, 4*Float.BYTES);
        
        glVertexAttribDivisor(6, 1);
        glVertexAttribDivisor(7, 1);
        glVertexAttribDivisor(8, 1);
        
        bindings.unbindAttributeArray();
    }
    
    private short[] generateIndices(int faces) {
        int len = faces * 6;
        final short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        } return indices;
    }
    
    public void begin() {
        if (rendering) throw new IllegalStateException();
        renderCalls = 0;
        rendering = true;
    }
    
    public void end() {
        if (!rendering) throw new IllegalStateException();
        if (facesCount > 0) flush();
        rendering = false;
    }
    
    public void drawVoxel(int x, int y, int z, float color) {
        if(size - (facesCount) <= 0) flush();
        vertexData.put(x).put(y).put(z).put(color).put(0);
        vertexData.put(x).put(y).put(z).put(color).put(1);
        vertexData.put(x).put(y).put(z).put(color).put(2);
        vertexData.put(x).put(y).put(z).put(color).put(3);
        vertexData.put(x).put(y).put(z).put(color).put(4);
        vertexData.put(x).put(y).put(z).put(color).put(5);
        facesCount += 6;
    }
    
    private void flush() {
        if (facesCount == 0) return;
        vertexData.flip();
        render();
        renderCalls++;
        vertexData.clear();
        //System.out.println(facesCount);
        facesCount = 0;
    }
    
    private void render() {
        bindings.bindAttributeArray(vao);
        instanceVBO.bind();
        //instanceVBO.bufferData(vertexData);
        instanceVBO.bufferSubData(vertexData,0);
        glDrawElementsInstanced(GL_TRIANGLES, facesCount * 6,GL_UNSIGNED_SHORT,0, facesCount);
    }
    
    public void dispose() {
        if (vertexData != null) MemoryUtil.memFree(vertexData);
        bindings.bindBufferObject(GL_ARRAY_BUFFER, 0);
        staticVBO.free();
        instanceVBO.free();
        ebo.free();
        bindings.bindAttributeArray(0);
        glDeleteVertexArrays(vao);
    }
    
    public int size() {
        return size;
    }
    
    public int renderCalls() {
        return renderCalls;
    }
    
    public boolean isRendering() {
        return rendering;
    }
}
