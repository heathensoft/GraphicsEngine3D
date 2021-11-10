package no.fredahl.engine.graphics.font;

import no.fredahl.engine.graphics.Texture;

/**
 * @author Frederik Dahl
 * 09/11/2021
 */


public class AsciiFont {
    
    private String name;
    private Texture texture;
    private float height;
    private float leading;
    
    private final Glyph[] glyphs = new Glyph[Byte.MAX_VALUE + 1];
}
