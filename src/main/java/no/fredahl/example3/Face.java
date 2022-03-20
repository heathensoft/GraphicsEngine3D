package no.fredahl.example3;

/**
 * @author Frederik Dahl
 * 15/01/2022
 */


public enum Face {
    
    //  v1------v4
    //  |        |
    //  |        |
    //  v2------v3
    
    FRONT(
            0,1,1,
            0,0,1,
            1,0,1,
            1,1,1,
            0,0,1),
    RIGHT(
            1,1,1,
            1,0,1,
            1,0,0,
            1,1,0,
            1,0,0),
    REAR(
            1,1,0,
            1,0,0,
            0,0,0,
            0,1,0,
            0,0,-1),
    LEFT(
            0,1,0,
            0,0,0,
            0,0,1,
            0,1,1,
            -1,0,0),
    TOP(
            0,1,0,
            0,1,1,
            1,1,1,
            1,1,0,
            0,1,0),
    BOTTOM(
            0,0,1,
            0,0,0,
            1,0,0,
            1,0,1,
            0,-1,0);
    
    public final float x1, y1, z1;
    public final float x2, y2, z2;
    public final float x3, y3, z3;
    public final float x4, y4, z4;
    public final float nx, ny, nz;
    
    Face(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float nx, float ny, float nz) {
        
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
        this.x3 = x3; this.y3 = y3; this.z3 = z3;
        this.x4 = x4; this.y4 = y4; this.z4 = z4;
        this.nx = nx; this.ny = ny; this.nz = nz;
    }
}
