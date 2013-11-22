package ru.axetta.ecafe.processor.core.utils.rusmarc;

import java.io.UnsupportedEncodingException;

public abstract class FieldDecoder {
    public byte[] data;
    public String dataString;

    //private static BasicCyrillic basicCyrillic = new BasicCyrillic();

    public void read(String[] enc) {
        dataString = decode(data, enc[1].equals("  ") ? enc[0] : enc[1]);
        data = null;
    }

    private String decode(byte[] data, String encoding) {
        try {
            if (encoding.equals("50")) {
                return new String(data, "UTF8");
            } else if (encoding.equals("01")) {
                return new String(data, "ASCII").replace('$', '¤');//ISO 646 IRV отличается от ASCII только 1 символом
            } else if (encoding.equals("02")) {
                //return basicCyrillic.GetString(data);
                return null;//todo basicCyrillic
            } else if (encoding.equals("89")) {
                return new String(data, "Cp1251");
            } else if (encoding.equals("99")) {
                return new String(data, "KOI8_R");
            } else if (encoding.equals("79")) {
                return new String(data, "Cp866");
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
