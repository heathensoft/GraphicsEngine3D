package no.fredahl;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Frederik Dahl
 * 24/10/2021
 */


public class Main {
    
    
    public static boolean compare(float f1, float f2, float epsilon) {
        return Math.abs(f1-f2) <= epsilon * Math.max(1.0f, Math.max(Math.abs(f1),Math.abs(f2)));
    }
    
    public static void main(String[] args) {
    
        float one = 0.0001f;
        float two = 0.00009f;
        
        boolean equals = compare(one,two,0.00001f);
        System.out.println(equals);
    }
    
    
}
