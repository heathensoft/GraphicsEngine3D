package no.fredahl.engine.utility;


/**
 * Improved version of this:
 *
 * https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
 *
 * @author Frederik Dahl
 * XX/XX/2020
 */

public class TileMask {

    /*---------------------*\
    |  ___________________  |
    |  |  1  |  2  |  4  |  |
    |  ===================  |
    |  |  8  |  0  | 16  |  |
    |  ===================  |
    |  | 32  | 64  | 128 |  |
    |  -------------------  |
    \*---------------------*/
    
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

    public static int get(int mask) {
        return EIGHT_BIT[mask];
    }

    
    // How the values were calculated:
    
    
    /*
    public static void printNewMask() {

        final Map<Integer, Integer> originalMask = Map.ofEntries(
                entry(2,  1 ), entry(8,  2 ), entry(10, 3 ),
                entry(11, 4 ), entry(16, 5 ), entry(18, 6 ),
                entry(22, 7 ), entry(24, 8 ), entry(26, 9 ),
                entry(27, 10), entry(30, 11), entry(31, 12),
                entry(64, 13), entry(66, 14), entry(72, 15),
                entry(74, 16), entry(75, 17), entry(80, 18),
                entry(82, 19), entry(86, 20), entry(88, 21),
                entry(90, 22), entry(91, 23), entry(94, 24),
                entry(95, 25), entry(104,26), entry(106,27),
                entry(107,28), entry(120,29), entry(122,30),
                entry(123,31), entry(126,32), entry(127,33),
                entry(208,34), entry(210,35), entry(214,36),
                entry(216,37), entry(218,38), entry(219,39),
                entry(222,40), entry(223,41), entry(248,42),
                entry(250,43), entry(251,44), entry(254,45),
                entry(255,46), entry(0,  47)
        );

        ArrayList<Entry> entries = new ArrayList<>();

        final int n = 8;
        for (int k = 0; k < Math.pow(2, n); k++) {
            String bin = Integer.toBinaryString(k);
            while (bin.length() < n)
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
            boolean[] boolArray = new boolean[n];
            for (int j = 0; j < chars.length; j++) {
                boolArray[j] = chars[j] == '0';
            }

            int[] power = {1, 2, 4, 8, 16, 32, 64, 128};
            int converted_code = 0;

            int key = 0;
            for (int i = 0; i < boolArray.length; i++) {
                if (boolArray[i]) {
                    key += (1 << i);
                }
                if (boolArray[i]) {
                    if (i == 1 || i == 3 || i == 4 || i == 6){
                        converted_code += power[i];
                    }
                    else {
                        if (i == 0){
                            if (boolArray[1] && boolArray[3]) {
                                converted_code += power[i];
                            }
                        }
                        if (i == 2){
                            if (boolArray[1] && boolArray[4]) {
                                converted_code += power[i];
                            }
                        }
                        if (i == 5){
                            if (boolArray[3] && boolArray[6]) {
                                converted_code += power[i];
                            }
                        }
                        if (i == 7){
                            if (boolArray[4] && boolArray[6]) {
                                converted_code += power[i];
                            }
                        }
                    }
                }
            }
            int value = originalMask.get(converted_code);
            Entry e = new Entry(key,value);
            entries.add(e);
        }

        Collections.sort(entries);

        for (Entry e : entries) {
            System.out.println(e);
        }

    }


    private static class Entry implements Comparable<Entry>{

        int key, value;

        public Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Entry o) {
            return Integer.compare(this.key,o.key);
        }

        /*
        @Override
        public String toString() {
            return "entry("+key+","+value+"),";
        }
         */
}
