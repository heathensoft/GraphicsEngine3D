package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.graphics.BufferObject;
import no.fredahl.engine.graphics.Color;
import org.joml.Vector3f;
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
    
    public MaterialBlock(int bindingPoint) {
        this(bindingPoint,
                Material.DEBUG,
                Material.BLACK_PLASTIC,
                Material.BLACK_RUBBER,
                Material.BRASS,
                Material.BRONZE,
                Material.CHROME,
                Material.COPPER,
                Material.EMERALD,
                Material.GOLD,
                Material.GREEN_PLASTIC,
                Material.GREEN_RUBBER,
                Material.JADE,
                Material.OBSIDIAN,
                Material.PERL,
                Material.POLISHED_BRONZE,
                Material.POLISHED_COPPER,
                Material.POLISHED_GOLD,
                Material.POLISHED_SILVER,
                Material.RED_PLASTIC,
                Material.RED_RUBBER,
                Material.RUBY,
                Material.SILVER,
                Material.TIN,
                Material.WHITE_PLASTIC,
                Material.WHITE_RUBBER,
                Material.YELLOW_PLASTIC,
                Material.TURQUOISE);
    }
    
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
    
    public int count() {
        return count;
    }
    
    public void free() {
        uniformBuffer.unbind();
        uniformBuffer.free();
    }

}
