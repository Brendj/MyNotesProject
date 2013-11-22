/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

import org.junit.Test;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESTest {

    public static String byteArrayToHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Test
    public void encryptionWorksOkUsingJCE() throws Exception {

        String algorithm = "DESede";
        String transformation = "DESede/CBC/NoPadding";

        byte[] keyValue = hexStringToByteArray("D3C508A36CE10288012FDC5F1BA6534BD3C508A36CE10288");

        String PLAIN_TEXT="";

        DESedeKeySpec keySpec = new DESedeKeySpec(keyValue);

        /* Initialization Vector of 8 bytes set to zero. */
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);

        SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);

        Cipher decrypter = Cipher.getInstance(transformation);
        decrypter.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] CIPHER_TEXT=hexStringToByteArray("e6a305390b9b58d920967301f8230281dd23ac6fc6f03d328c78b8e48d6150eb");

        byte[] decrypted = decrypter.doFinal(CIPHER_TEXT);

        System.out.println(decrypted.toString());
    }
}
