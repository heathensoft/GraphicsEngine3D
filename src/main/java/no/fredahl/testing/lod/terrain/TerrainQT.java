package no.fredahl.testing.lod.terrain;
import no.fredahl.engine.math.MathLib;

/**
 * QuadTree structure for terrain LOD and culling.
 *
 * @author Frederik Dahl
 * 05/04/2022
 */

public class TerrainQT {
    
    private final int size;
    private final int lim;
    private int far;
    
    public TerrainQT(int sizePow2, int basePow2, int farPlane) {
        sizePow2 = Math.max(1,sizePow2);
        basePow2 = Math.min(sizePow2,basePow2);
        int base = (int) Math.pow(2,basePow2);
        this.size = (int) Math.pow(2,sizePow2);
        this.lim = sizePow2 - MathLib.log2(base);
        this.far = farPlane * farPlane;
    }
    
    /**
     * insert point (pX,pY) and collect all leaf quads within
     * a certain range of the point, also culled by the far plane.
     * The quads have various dimensions based on proximity.
     * @param qtI iterator
     * @param pX point x
     * @param pY point y
     */
    public void query(Iterator qtI, int pX, int pY) {
        query(qtI,0,0, pX, pY,0,size);
    }
    
    private void query(Iterator qtI, int x0, int y0, int pX, int pY, int d, int s) {
        final int sH = s >> 1;
        final int cX = x0 + sH;
        final int cY = y0 + sH;
        final int vX = (cX - pX);
        final int vY = (cY - pY);
        final int v = vX * vX + vY * vY;
        final int thr = (s * s) << 1;
        if (v <= thr) { // LOD check
            if (d < lim) { d++;
                query(qtI,x0,y0,pX,pY,d,sH); // SW
                query(qtI,cX,y0,pX,pY,d,sH); // SE
                query(qtI,x0,cY,pX,pY,d,sH); // NW
                query(qtI,cX,cY,pX,pY,d,sH); // NE
                return;
            } // No faPlane check
            qtI.pass(cX,cY,s);
            return; // farPlane check
        } if (v < far) { qtI.pass(cX,cY,s);}
    }
    
    public void setFar(int far) {
        this.far = far * far;
    }
    
    public interface Iterator {
        /**
         * @param cX center x
         * @param cY center y
         * @param size quad dimensions
         */
        void pass(int cX, int cY, int size);
    }
}
