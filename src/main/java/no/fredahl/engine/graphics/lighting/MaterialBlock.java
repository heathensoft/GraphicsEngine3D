package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.BufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

/**
 * @author Frederik Dahl
 * 18/12/2021
 */


public class MaterialBlock {
    
    private final BufferObject uniformBuffer;
    private final int bindingPoint;
    private final int count;
    
    public MaterialBlock(int bindingPoint, Material ...materials) {
        
        this.count = materials.length;
        this.bindingPoint = bindingPoint;
        this.uniformBuffer = new BufferObject(GL_UNIFORM_BUFFER,GL_STATIC_DRAW);
        this.uniformBuffer.bind();
        FloatBuffer buffer = null;
        try {
            buffer = MemoryUtil.memAllocFloat(Material.sizeInFloats(count));
            for (int i = 0; i < count; i++) {
                materials[i].getSTD140(buffer);
            }
            this.uniformBuffer.bufferData(buffer.flip());
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
        this.uniformBuffer.bindBufferBase(bindingPoint);
    }
    
    public String glslUniformBlock() {
        
        return "layout(std140, binding = "+ bindingPoint +") uniform Materials {\n" +
                       "    Material list[" + count + "];\n" +
                       "} ubo_materials;";
    }
    
    public String glslMaterial() {
        
        return "struct Material {\n" +
                       "    vec3 ambient;\n" +
                       "    float emission;\n" +
                       "    vec3 diffuse;\n" +
                       "    float alpha;\n" +
                       "    vec3 specular;\n" +
                       "    float shine;\n" +
                       "};";
    }
    
    public int bindingPoint() {
        return bindingPoint;
    }
    
    public void free() {
        
        uniformBuffer.unbind();
        uniformBuffer.free();
    }

}
