package no.fredahl.engine.utility;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author Frederik Dahl
 * XX/XX/2020
 */

public class SerializerOld {


    public static void writeBytes(byte[] dest, int[] pointer, boolean[] src) {
        int numBytes = (int) (Math.ceil((float)src.length / 8));
        int bitsWritten = 0;
        for (int entry = 0; entry < numBytes; entry++, pointer[0]++) {
            for (int bit = 0; bit < 8 && bitsWritten < src.length; bit++) {
                if (src[bitsWritten++]) dest[pointer[0]] |= (128 >> bit);
            }
        }
    }

    public static void writeBytes(byte[] dest, boolean[] src) {
        int numBytes = (int) (Math.ceil((float)src.length / 8));
        int bitsWritten = 0;
        for (int entry = 0; entry < numBytes; entry++) {
            for (int bit = 0; bit < 8 && bitsWritten < src.length; bit++) {
                if (src[bitsWritten++]) dest[entry] |= (128 >> bit);
            }
        }
    }

    public static void writeBytes(byte[] dest, int[] pointer, byte[] src) {
        int length = src.length;
        System.arraycopy(src,0,dest,pointer[0],length);
        pointer[0] += length;
    }

    public static void writeBytes(byte[] dest, byte[] src) {
        System.arraycopy(src,0,dest,0,src.length);
    }

    public static void writeBytes(byte[] dest, int[] pointer, short[] src) {
        for (short value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, short[] src) {
        for (int i = 0; i < src.length; i++) {
            dest[i * 2] = (byte) ((src[i] >> 8) & 0xff);
            dest[i * 2 +1] = (byte)  (src[i] & 0xff);
        }
    }

    public static void writeBytes(byte[] dest, int[] pointer, char[] src) {
        for (char value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, char[] src) {
        for (int i = 0; i < src.length; i++) {
            dest[i*2  ] = (byte) ((src[i] >> 8) & 0xff);
            dest[i*2+1] = (byte)  (src[i] & 0xff);
        }
    }

    public static void writeBytes(byte[] dest, int[] pointer, int[] src) {
        for (int value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, int[] src) {
        for (int i = 0; i < src.length; i++) {
            dest[i*4  ] = (byte) ((src[i] >> 24) & 0xff);
            dest[i*4+1] = (byte) ((src[i] >> 16) & 0xff);
            dest[i*4+2] = (byte) ((src[i] >> 8) & 0xff);
            dest[i*4+3] = (byte) ( src[i] & 0xff);
        }
    }

    public static void writeBytes(byte[] dest, int[] pointer, long[] src) {
        for (long value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, long[] src) {
        int[] pointer = {0};
        for (long value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, int[] pointer, float[] src) {
        for (float value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, float[] src) {
        int[] pointer = {0};
        for (float value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, int[] pointer, double[] src) {
        for (double value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, double[] src) {
        int[] pointer = {0};
        for (double value : src) writeBytes(dest, pointer, value);
    }

    public static void writeBytes(byte[] dest, int[] pointer, boolean value) {
        dest[pointer[0]++] = (byte) (value ? 1 : 0);
    }

    public static void writeBytes(byte[] dest, boolean value) {
        dest[0] = (byte) (value ? 1 : 0);
    }

    public static void writeBytes(byte[] dest, int[] pointer, byte value) {
        dest[pointer[0]++] = value;
    }

    public static void writeBytes(byte[] dest, byte value) {
        dest[0] = value;
    }

    public static void writeBytes(byte[] dest, int[] pointer, short value) {
        dest[pointer[0]++] = (byte) ((value >> 8) & 0xff);
        dest[pointer[0]++] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, short value) {
        dest[0] = (byte) ((value >> 8) & 0xff);
        dest[1] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, int[] pointer, char value) {
        dest[pointer[0]++] = (byte) ((value >> 8) & 0xff);
        dest[pointer[0]++] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, char value) {
        dest[0] = (byte) ((value >> 8) & 0xff);
        dest[1] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, int[] pointer, int value) {
        dest[pointer[0]++] = (byte) ((value >> 24) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 16) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 8) & 0xff);
        dest[pointer[0]++] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, int value) {
        dest[0] = (byte) ((value >> 24) & 0xff);
        dest[1] = (byte) ((value >> 16) & 0xff);
        dest[2] = (byte) ((value >> 8) & 0xff);
        dest[3] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, int[] pointer, long value) {
        dest[pointer[0]++] = (byte) ((value >> 56) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 48) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 40) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 32) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 24) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 16) & 0xff);
        dest[pointer[0]++] = (byte) ((value >> 8) & 0xff);
        dest[pointer[0]++] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, long value) {
        dest[0] = (byte) ((value >> 56) & 0xff);
        dest[1] = (byte) ((value >> 48) & 0xff);
        dest[2] = (byte) ((value >> 40) & 0xff);
        dest[3] = (byte) ((value >> 32) & 0xff);
        dest[4] = (byte) ((value >> 24) & 0xff);
        dest[5] = (byte) ((value >> 16) & 0xff);
        dest[6] = (byte) ((value >> 8) & 0xff);
        dest[7] = (byte) (value & 0xff);
    }

    public static void writeBytes(byte[] dest, int[] pointer, float value) {
        writeBytes(dest, pointer, Float.floatToIntBits(value));
    }

    public static void writeBytes(byte[] dest, float value) {
        writeBytes(dest, Float.floatToIntBits(value));
    }

    public static void writeBytes(byte[] dest, int[] pointer, double value) {
        writeBytes(dest, pointer, Double.doubleToLongBits(value));
    }

    public static void writeBytes(byte[] dest, double value) {
        writeBytes(dest, Double.doubleToLongBits(value));
    }

    // returns the byteSize of the encoded string
    public static int writeBytes(byte[] dest, int[] pointer, String string, Charset encoding) {
        byte[] bytes = string.getBytes(encoding);
        writeBytes(dest,pointer,bytes);
        return bytes.length;
    }

    public static boolean readBoolean(byte[] src) {
        return src[0] != 0;
    }

    public static boolean readBoolean(byte[] src, int[] pointer) {
        return src[pointer[0]++] != 0;
    }

    public static void readBoolArray(byte [] src, int[] pointer, boolean[] dest){
        int numBytes = (int) (Math.ceil(dest.length / 8f));
        pointer[0] += numBytes;
        for(int i = 0; i < numBytes; i++){
            for(int j = i << 3, k = 7; k >= 0 && j < dest.length; j++, k--){
                dest[j] = (src[i] & (1 << k)) != 0;
            }
        }
    }

    public static byte readByte(byte[] src) {
        return (byte)(src[0] & 0xff);
    }

    public static byte readByte(byte[] src, int[] pointer) {
        byte value = (byte)(src[pointer[0]] & 0xff);
        pointer[0]++;
        return value;
    }

    public static void readByteArray(byte[] src, int[] pointer, byte[] dest) {
        int length = dest.length;
        System.arraycopy(src, pointer[0], dest, 0, length);
        pointer[0] += length;
    }

    public static short readShort(byte[] src) {
        return (short)((src[0] & 0xff ) << 8 | (src[1] & 0xff));
    }

    public static short readShort(byte[] src, int[] pointer) {
        short value = (short)((src[pointer[0]] & 0xff ) << 8 | (src[pointer[0] + 1] & 0xff));
        pointer[0] += 2;
        return value;
    }

    public static void readShortArray(byte[] src, int[] pointer, short[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readShort(src,pointer);
    }

    public static char readChar(byte[] src) {
        return (char) ((src[0] & 0xff ) << 8 | (src[1] & 0xff));
    }

    public static char readChar(byte[] src, int[] pointer) {
        char value = (char) ((src[pointer[0]] & 0xff ) << 8 | (src[pointer[0] + 1] & 0xff));
        pointer[0] += 2;
        return value;
    }

    public static void readCharArray(byte[] src, int[] pointer, char[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readChar(src,pointer);
    }

    public static int readInt(byte[] src) {
        return  ((src[0] & 0xff) << 24 |
                 (src[1] & 0xff) << 16 |
                 (src[2] & 0xff) << 8 |
                 (src[3] & 0xff));
    }

    public static int readInt(byte[] src, int[] pointer) {
        int value = ((src[pointer[0]] & 0xff) << 24 |
                     (src[pointer[0] + 1] & 0xff) << 16 |
                     (src[pointer[0] + 2] & 0xff) << 8 |
                     (src[pointer[0] + 3] & 0xff));
        pointer[0] += 4;
        return value;
    }

    public static void readIntArray(byte[] src, int[] pointer, int[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readInt(src,pointer);
    }

    public static long readLong(byte[] src) {
        return ((src[0] & 0xffL) << 56L |
                (src[1] & 0xffL) << 48L |
                (src[2] & 0xffL) << 40L |
                (src[3] & 0xffL) << 32L |
                (src[4] & 0xffL) << 24L |
                (src[5] & 0xffL) << 16L |
                (src[6] & 0xffL) << 8L |
                (src[7] & 0xffL));
    }

    public static long readLong(byte[] src, int[] pointer) {
        long value = ((src[pointer[0]] & 0xffL) << 56L |
                      (src[pointer[0] + 1] & 0xffL) << 48L |
                      (src[pointer[0] + 2] & 0xffL) << 40L |
                      (src[pointer[0] + 3] & 0xffL) << 32L |
                      (src[pointer[0] + 4] & 0xffL) << 24L |
                      (src[pointer[0] + 5] & 0xffL) << 16L |
                      (src[pointer[0] + 6] & 0xffL) << 8L |
                      (src[pointer[0] + 7] & 0xffL));
        pointer[0] += 8;
        return value;
    }

    public static void readLongArray(byte[] src, int[] pointer, long[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readLong(src,pointer);
    }

    public static float readFloat(byte[] src) {
        return Float.intBitsToFloat(readInt(src));
    }

    public static float readFloat(byte[] src, int[] pointer) {
        return Float.intBitsToFloat(readInt(src,pointer));
    }

    public static void readFloatArray(byte[] src, int[] pointer, float[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readFloat(src,pointer);
    }

    public static double readDouble(byte[] src) {
        return Double.longBitsToDouble(readLong(src));
    }

    public static double readDouble(byte[] src, int[] pointer) {
        return Double.longBitsToDouble(readLong(src,pointer));
    }

    public static void readDoubleArray(byte[] src, int[] pointer, double[] dest) {
        for (int i = 0; i < dest.length; i++)
            dest[i] = readDouble(src,pointer);
    }

    public static String readString(byte[] src, int bytes, Charset encoding) {
        return new String(src, 0, bytes, encoding);
    }

    public static String readString(byte[] src, int[] pointer, int bytes, Charset encoding) {
        String value = new String(src, pointer[0], bytes, encoding);
        pointer[0] += bytes;
        return value;
    }

    public static byte charsetToByte(Charset charset) throws UnsupportedCharsetException {
        if      (charset.equals(StandardCharsets.US_ASCII)) return (byte) 0;
        else if (charset.equals(StandardCharsets.UTF_8))    return (byte) 1;
        else if (charset.equals(StandardCharsets.UTF_16))   return (byte) 2;
        else if (charset.equals(StandardCharsets.UTF_16BE)) return (byte) 3;
        else if (charset.equals(StandardCharsets.UTF_16LE)) return (byte) 4;
        else throw new UnsupportedCharsetException(charset.name());
    }

    public static Charset byteToCharset(byte format) throws UnsupportedCharsetException{
        switch (format) {
            case 0: return StandardCharsets.US_ASCII;
            case 1: return StandardCharsets.UTF_8;
            case 2: return StandardCharsets.UTF_16;
            case 3: return StandardCharsets.UTF_16BE;
            case 4: return StandardCharsets.UTF_16LE;
            default: throw new UnsupportedCharsetException("Invalid Charset Identifier");
        }
    }

    public static byte msb4(byte data) {
        return (byte)((data >> 4) & 0x0F);
    }

    public static byte lsb4(byte data) {
        return (byte)(data & 0x0F);
    }

    public static byte merge(byte msb4, byte lsb4) {
        return (byte)(msb4 << 4 | lsb4);
    }

}
