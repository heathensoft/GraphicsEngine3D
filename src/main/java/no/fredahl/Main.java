package no.fredahl;

import org.joml.Math;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Frederik Dahl
 * 24/10/2021
 */


public class Main {
    
    public static class Attribute implements Comparable<Attribute> {
    
        public int index;
        
        public Attribute(int index) {
            this.index = index;
        }
        
        @Override
        public int compareTo(Attribute o) {
            return Integer.compare(this.index,o.index);
        }
    }
    
    public static void main(String[] args) {
    
        Attribute[] array;
        
        array = sort(
                new Attribute(2),
                new Attribute(3),
                new Attribute(0),
                new Attribute(1));
        
        for (Attribute attribute : array) {
            System.out.println(attribute.index);
        }
        
    }
    
    public static Attribute[] sort(Attribute... attributes) {
        Arrays.sort(attributes);
        return attributes;
    }
}
