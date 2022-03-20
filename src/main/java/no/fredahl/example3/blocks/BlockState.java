package no.fredahl.example3.blocks;

/**
 *
 * A block is a 32-bit integer of information:
 *
 * [LSB]
 * 8-bit TYPE
 * 8-bit UV_MASK to identify texture-region uv-index
 * 4-bit FACE_MASK for which block-faces to render
 * 12-bit available space for later
 * [MSB]
 *
 *
 *
 * @author Frederik Dahl
 * 25/01/2022
 */


public class BlockState {
    
    
    // UV-MASK                // FACE-MASK
    /*---------------------*\ /*---------------------*\
    |  ___________________  | |  ___________________  |
    |  |  1  |  2  |  4  |  | |  |     |  1  |     |  |
    |  ===================  | |  ===================  |
    |  |  8  | BLK | 16  |  | |  |  2  | BLK |  4  |  |
    |  ===================  | |  ===================  |
    |  | 32  | 64  | 128 |  | |  |     |  8  |     |  |
    |  -------------------  | |  -------------------  |
    \*---------------------*/ /*---------------------*/
    
    private static final byte[] EIGHT_BIT = {
            
            47, 47, 1 , 1 , 47, 47, 1 , 1 , 2 , 2 , 3 , 4 , 2 , 2 , 3 , 4 ,
            5 , 5 , 6 , 6 , 5 , 5 , 7 , 7 , 8 , 8 , 9 , 10, 8 , 8 , 11, 12,
            47, 47, 1 , 1 , 47, 47, 1 , 1 , 2 , 2 , 3 , 4 , 2 , 2 , 3 , 4 ,
            5 , 5 , 6 , 6 , 5 , 5 , 7 , 7 , 8 , 8 , 9 , 10, 8 , 8 , 11, 12,
            13, 13, 14, 14, 13, 13, 14, 14, 15, 15, 16, 17, 15, 15, 16, 17,
            18, 18, 19, 19, 18, 18, 20, 20, 21, 21, 22, 23, 21, 21, 24, 25,
            13, 13, 14, 14, 13, 13, 14, 14, 26, 26, 27, 28, 26, 26, 27, 28,
            18, 18, 19, 19, 18, 18, 20, 20, 29, 29, 30, 31, 29, 29, 32, 33,
            47, 47, 1 , 1 , 47, 47, 1 , 1 , 2 , 2 , 3 , 4 , 2 , 2 , 3 , 4 ,
            5 , 5 , 6 , 6 , 5 , 5 , 7 , 7 , 8 , 8 , 9 , 10, 8 , 8 , 11, 12,
            47, 47, 1 , 1 , 47, 47, 1 , 1 , 2 , 2 , 3 , 4 , 2 , 2 , 3 , 4 ,
            5 , 5 , 6 , 6 , 5 , 5 , 7 , 7 , 8 , 8 , 9 , 10, 8 , 8 , 11, 12,
            13, 13, 14, 14, 13, 13, 14, 14, 15, 15, 16, 17, 15, 15, 16, 17,
            34, 34, 35, 35, 34, 34, 36, 36, 37, 37, 38, 39, 37, 37, 40, 41,
            13, 13, 14, 14, 13, 13, 14, 14, 26, 26, 27, 28, 26, 26, 27, 28,
            34, 34, 35, 35, 34, 34, 36, 36, 42, 42, 43, 44, 42, 42, 45, 46
        
    };
    
    public static int type(final int block) {
        return block & 0xFF;
    }
    
    public static int setType(final int block, final int type) {
        return (block & 0xFFFF_FF00) | type;
    }
    
    public static int uvMask(final int block) {
        return (block >> 0x08) & 0xFF;
    }
    
    public static int setUVMask(final int block, final int mask) {
        return (block & 0xFFFF_00FF) | (mask << 0x08);
    }
    
    public static int uvIndex(int block) {
        return EIGHT_BIT[(block >> 0x08) & 0xFF];
    }
    
    public static int faceMask(final int block) {
        return (block >> 0x10) & 0x0F;
    }
    
    public static int setFaceMask(final int block, final int mask) {
        return (block & 0xFFF0_FFFF) | (mask << 0x10);
    }
    
    public static boolean renderFrontOnly(final int block) {
        return (block & 0x000F_0000) == 0x000F_0000;
    }
    
    public static boolean renderTop(final int block) {
        return (block & 0x0001_0000) == 0;
    }
    
    public static boolean renderLeft(final int block) {
        return (block & 0x0002_0000) == 0;
    }
    
    public static boolean renderRight(final int block) {
        return (block & 0x0004_0000) == 0;
    }
    
    public static boolean renderBottom(final int block) {
        return (block & 0x0008_0000) == 0;
    }
    
    public static boolean sameType(final int block1, final int block2) {
        return ((block1 ^ block2) & 0xFF) == 0;
    }
    
    
}
