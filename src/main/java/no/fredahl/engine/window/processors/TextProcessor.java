package no.fredahl.engine.window.processors;

/**
 *
 * Processor for old-school ascii characters [ 0-127 ]
 *
 * @author Frederik Dahl
 * 27/10/2021
 */


public interface TextProcessor {
    
    /**
     * @param character "non-printable-character"
     */
    void npcPress(int character);
    
    /**
     * @param character "non-printable-character"
     */
    void npcRelease(int character);
    
    /**
     * @param character ascii  [32 - 126]
     */
    void printable(byte character);
}
