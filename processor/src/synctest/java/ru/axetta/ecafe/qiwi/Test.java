package ru.axetta.ecafe.qiwi;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;


public class Test {

    public static void main( String[] args )
            throws Exception
    {
        String url = "https://78.46.34.200:8443/processor/payment-std?CLIENTID=200022&SUM=100&TIME=1406290983&OPID=1005002614&TERMID=500100&PID=qiwi&V=1";
        String singnature = generateSignature(url);
        String resultUrl = url + "&SIGNATURE=" + singnature;
        System.out.print(resultUrl);
    }
    //full url
    private static String generateSignature(String url)throws Exception{
        // Read keystore
        KeyStore ks = KeyStore.getInstance("pkcs12","SunJSSE");
        InputStream ksin = new FileInputStream(new File("F:\\backup\\Информация\\сертификаты\\тестовый сервер\\test_server\\qiwi_test\\ispp_qiwi_test.pfx"));
        ks.load(ksin, "1".toCharArray());  /// 1 - password for  pfx key
        ksin.close();

        // get private key from keystore
        Key key = ks.getKey(ks.aliases().nextElement(),"1".toCharArray());

        // Create signature
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign((PrivateKey)key);
        signature.update(url.getBytes());
        byte[] dataSignature = signature.sign(); // Get signature
        return (byteArray2Hex(dataSignature)); // return signature
    }


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


}