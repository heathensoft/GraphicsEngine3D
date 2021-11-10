package no.fredahl.engine.graphics.font;

import no.fredahl.engine.graphics.Texture;
import no.fredahl.engine.graphics.TextureRegion;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class Glyph extends TextureRegion {
    
    private char value;
    private float advance;
    private float offsetX;
    private float offsetY;
    
    
    public Glyph(Texture t, float u, float v, float u2, float v2) {
        super(t, u, v, u2, v2);
    }
}
