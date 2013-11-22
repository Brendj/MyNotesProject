package ru.axetta.ecafe.util;

/**
 * Created by IntelliJ IDEA.
 * User: Игорь
 * Date: 11.02.2011
 * Time: 23:30:10
 * To change this template use File | Settings | File Templates.
 */
public class ConversionUtils {

    static final String HEXES = "0123456789ABCDEF";

    public static String byteArray2Hex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static byte[] hex2ByteArray(String hexString) {
        byte[] out = new byte[hexString.length() / 2];

        int n = hexString.length();

        for (int i = 0; i < n; i += 2) {
            //make a bit representation in an int of the hex value
            int hn = HEXES.indexOf(hexString.charAt(i));
            int ln = HEXES.indexOf(hexString.charAt(i + 1));

            //now just shift the high order nibble and add them together
            out[i / 2] = (byte) ((hn << 4) | ln);
        }

        return out;
    }

}
