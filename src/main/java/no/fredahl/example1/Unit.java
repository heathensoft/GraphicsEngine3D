package no.fredahl.example1;

import no.fredahl.engine.math.Transform;
import org.joml.Matrix4f;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;

/**
 * 3D unit moving on a 2D plane
 *
 * @author Frederik Dahl
 * 04/11/2021
 */


public class Unit implements Selectable{
    
    private final Transform transform;
    private final Mesh mesh;
    private boolean selected;
    
    public Unit(Mesh mesh) {
        this.transform = new Transform();
        this.mesh = mesh;
        
    }
    
    @Override
    public boolean intersects(RayAabIntersection rayIntersection) {
        final Vector3f scale = transform.getScale();
        final Vector3f pos = transform.getPosition();
        final float minX = pos.x - scale.x / 2;
        final float minY = pos.y - scale.y / 2;
        final float minZ = pos.z - scale.z / 2;
        final float maxX = pos.x + scale.x / 2;
        final float maxY = pos.y + scale.y / 2;
        final float maxZ = pos.z + scale.z / 2;
        return rayIntersection.test(minX,minY,minZ,maxX,maxY,maxZ);
    }
    
    public void draw() {
        mesh.render();
    }
    
    @Override
    public Vector3f position() {
        return transform.getPosition();
    }
    
    @Override
    public void select() {
        selected = true;
    }
    
    @Override
    public void unselect() {
        selected = false;
    }
    
    @Override
    public boolean isSelected() {
        return selected;
    }
    
    public void translate(float x, float z) {
        transform.translate(x,0,z);
    }
    
    public void setPosition(float x, float z) {
        transform.setPosition(x,0,z);
    }
    
    public void setScale(float x, float y, float z) {
        transform.setScale(x,y,z);
    }
    
    public Matrix4f getModelToWorld() {
        return transform.get();
    }
    
    
    
}
