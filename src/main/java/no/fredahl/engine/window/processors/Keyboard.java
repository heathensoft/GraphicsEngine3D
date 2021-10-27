package no.fredahl.engine.window.processors;

import no.fredahl.engine.window.events.CharPressEvents;
import no.fredahl.engine.window.events.KeyPressEvents;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;

/**
 *
 * https://learn.parallax.com/support/reference/ascii-table-0-127
 *
 * @author Frederik Dahl
 * 26/10/2021
 */


public class Keyboard {
    
    private boolean update;
    private final boolean[] keys;
    private final boolean[] pkeys;
    private final KeyPressEvents keyPressEvents;
    private final CharPressEvents charPressEvents;
    private TextProcessor textProcessor;
    
    public Keyboard(KeyPressEvents keyPressEvents, CharPressEvents charPressEvents) {
        this.keys = new boolean[GLFW_KEY_LAST];
        this.pkeys = new boolean[GLFW_KEY_LAST];
        this.keyPressEvents = keyPressEvents;
        this.charPressEvents = charPressEvents;
        this.charPressEvents.ignore(true);
        this.charPressEvents.clear();
    }
    
    private final Collector keyCollector = new Collector() {
        @Override
        public void next(int key) {
            if (key > 0) {
                keys[key] = true;
                if (textProcessor != null) {
                    if (key < 0x20 || key == 0x7F) {
                        textProcessor.npcPress(key);
                    }
                }
            }
            else {
                key = Math.abs(key);
                keys[key] = false;
                if (textProcessor != null) {
                    if (key < 0x20 || key == 0x7F) {
                        textProcessor.npcRelease(key);
                    }
                }
            }
        }
    };
    
    private final Collector charCollector = new Collector() {
        @Override
        public void next(int key) {
            if (textProcessor != null) {
                textProcessor.printable((byte)key);
            }
        }
    };
    
    public void collect() {
        
        if (update) {
            System.arraycopy(keys, 0, pkeys, 0, GLFW_KEY_LAST);
            update = false;
        }
        update = keyPressEvents.collect(keyCollector);
        charPressEvents.collect(charCollector);
    }
    
    public boolean pressed(int keycode) {
        if (keycode > GLFW_KEY_LAST) return false;
        return keys[keycode];
    }
    
    public boolean pressed(int keycode1, int keycode2) {
        return pressed(keycode1) && pressed(keycode2);
    }
    
    public boolean justPressed(int keycode) {
        if (keycode >= GLFW_KEY_LAST) return false;
        return keys[keycode] && !pkeys[keycode];
    }
    
    public boolean justPressed(int keycode, int mod) {
        return pressed(mod) && justPressed(keycode);
    }
    
    public boolean justReleased(int keycode) {
        return pkeys[keycode] && !keys[keycode];
    }
    
    public void setTextProcessor(TextProcessor processor) {
        if (textProcessor != null) {
            charPressEvents.ignore(true);
            charPressEvents.collect(charCollector);
        }
        textProcessor = processor;
        if (textProcessor != null)
            charPressEvents.ignore(false);
    }
}
