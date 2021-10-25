package no.fredahl.engine.graphics;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 23/10/2021
 */


public class VAO {
    
    private static final int SLOTS = 8;
    
    private final int id;
    private int capacity;
    private int vertexCount;
    private final GLObject manager;
    private final List<VBO> bufferObjects;
    private final boolean[] slots;
    private IndexBuffer indices = null;
    
    protected VAO(GLObject manager, int id) {
        this.bufferObjects = new ArrayList<>(4);
        this.slots = new boolean[SLOTS];
        this.manager = manager;
        this.id = id;
    }
    
    protected int id() {
        return id;
    }
    
    protected void addVBO(VBO vbo) throws Exception {
        VertexAttribute[] attributes = vbo.attributes();
        for (VertexAttribute attribute : attributes) {
            int index = attribute.index;
            if (index >= SLOTS) {
                throw new Exception("Index >= Slots");
            }
            else if (slots[index]) {
                throw new Exception("Occupied slot");
            }
            slots[index] = true;
        }
        bufferObjects.add(vbo);
    }
    
    protected List<VBO> bufferObjects() {
        return bufferObjects;
    }
    
    protected void setVertexCount(int count) {
        this.vertexCount = count;
    }
    
    protected void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public void storeData(VertexAttribute attribute, float[] data) {
        manager.storeDataInAttributeList(this,attribute,data);
    }
    
    public void bind() {
        manager.bind(this);
    }
    
    public int vertexCount() {
        return vertexCount;
    }
    
    public int capacity() {
        return capacity;
    }
    
    public int vertexSize() {
        int size = 0;
        for (VBO vbo: bufferObjects) {
            size += vbo.stride();
        }
        return size;
    }
    
    public int sizeFloats() {
        return vertexSize() * vertexCount;
    }
    
    public int sizeBytes() {
        return sizeFloats() * Float.BYTES;
    }
    
    
}
