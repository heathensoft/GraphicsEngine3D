package no.fredahl.engine.graphics.lighting;

import no.fredahl.engine.math.Sphere;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Frederik Dahl
 * 01/01/2022
 */


public class LightPriorityFilter {
    
    private final static float MOD = 1f;
    private final List<E> elements;
    private final int maxCapacity;
    private final Sphere bounds;
    
    public LightPriorityFilter(int maxCapacity) {
        this.elements = new ArrayList<>(maxCapacity);
        for (int i = 0; i < maxCapacity; i++)
            elements.add(new E());
        this.maxCapacity = maxCapacity;
        this.bounds = new Sphere();
    }
    
    public LightPriorityFilter() {
        this(32);
    }
    
    /*
    public void updateBounds(Camera camera) {
        Vector3f dir = camera.direction();
        Vector3f pos = camera.position();
        float near = camera.near();
        float far = camera.far();
        float x = dir.x * near + pos.x;
        float y = dir.y * near + pos.y;
        float z = dir.z * near + pos.z;
        bounds.set(x,y,z,(far-near) * MOD);
    }
    
     */
    
    private static final Comparator<E> comparator = (o1, o2) -> o1.compareTo(o2.d);
    
    private static final class E implements Comparable<Integer> {
        
        private int i, d;
        
        public void set(int i, int d) {
            this.d = d;
            this.i = i;
        }
        
        @Override
        public int compareTo(Integer o) {
            return Integer.compare(d,o);
        }
    }
    
    // todo: when filtering casters, and the caster does not fit cap.
    //  then this class should try to add it instead as a light component only
    /**
     * culls and puts the n-closest lights to the the camera near-plane center
     * into the dest list, where n >= cap
     *
     * @param source lights to be filtered
     * @param dest lights passed
     * @param cap the size limit of "dest"
     */
    @SuppressWarnings("all")
    public void filter(List<PointLight> source, List<PointLight> dest, int cap) {
        
        // clear will be done elsewhere. cap -= dest.size()
        
        dest.clear();
        
        if (!source.isEmpty()) {
            int passed = 0;
            int toFilter = Math.min(source.size(),maxCapacity);
            for (int i = 0; i < toFilter; i++) {
                int dist = (int) Math.abs(source.get(i).position.distance(bounds.x,bounds.y,bounds.z));
                if (dist < bounds.r) elements.get(passed++).set(i,dist);
            }
            if (passed > cap) {
                List<E> subList = elements.subList(0, passed);
                subList.sort(comparator);
                for (int i = 0; i < cap; i++) {
                    dest.add(source.get(subList.get(i).i));
                }
            }
            else if (passed > 0) {
                for (int i = 0; i < passed; i++) {
                    dest.add(source.get(elements.get(i).i));
                }
            }
        }
    }
    
    public void clear() {
    
    }
}
