package ru.axetta.ecafe.qiwi;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class ResponseVerify {
    private static final String response = "CLIENTID=26&OPID=7&RES=0&DESC=Payment is already registered. IdOfContragent == 56, IdOfPayment == 7&SIGNATURE=92DAC9CEA6052D6F562E0BD2A3D121B4A2F259ABE921DBF118098C202BD1829B2006DA3F9F2189A4945908E740B2E93DD6F4262F828F675CFEDB6B5E3FF8BE0DCE558B62D764CD96E14751261F7AF771C40D4D8F0FB1CC7811D7AF032428791409B5C8854D5690D32781F43D9DCD047BC2AAF70FD087EF6091359567AF3A58E8";
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDsuUGFoSW/giuxNEyZ4SITd/jJzlR3piPtR01BW4ih30ddX1IOaxDu90k84VB20NUgqzz1BIUbemiQ0HVUY/j+g2UFcVWpwQgJZkonay6JmkiNqbADesocu1Jx6EP/felYRL4XufnEPIJA3CD+Gzl9m89ukvj/WVGLw1owv0IUPwIDAQAB";


    static final String HEXES = "0123456789ABCDEF";
    final static String SIGNATURE_PARAM="&SIGNATURE=";
    private static final String SIGNATURE_ALGORITHM = "RSA";

    public static void main(String[] args) throws Exception {
        System.out.print(checkSignature(response));
    }

    static boolean checkSignature(String data) throws Exception {
        int pos=data.indexOf(SIGNATURE_PARAM);
        if (pos==-1) throw new Exception("Signature missing");
        String payload=data.substring(0, pos), signData=data.substring(pos+SIGNATURE_PARAM.length());
        Signature sign=Signature.getInstance("SHA1withRSA");
        sign.initVerify(convertToPublicKey(publicKey));
        byte[] signBytes=hex2ByteArray(signData);
        sign.update(payload.getBytes());
        return sign.verify(signBytes);
    }

    public static byte[] hex2ByteArray(String hexString) {
        byte[] out = new byte[hexString.length() / 2];
        int n = hexString.length();
        for (int i = 0; i < n; i += 2) {
            int hn = HEXES.indexOf(hexString.charAt(i));
            int ln = HEXES.indexOf(hexString.charAt(i + 1));
            out[i / 2] = (byte) ((hn << 4) | ln);
        }
        return out;
    }

    public static PublicKey convertToPublicKey(byte[] publicKeyData) throws Exception {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(SIGNATURE_ALGORITHM);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PublicKey convertToPublicKey(String publicKeyData) throws Exception {
        return convertToPublicKey(Base64.decodeBase64(publicKeyData.getBytes(CharEncoding.US_ASCII)));
    }

}
