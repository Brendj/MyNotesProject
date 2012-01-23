/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.pubkey;

import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.CharEncoding;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 14:45:12
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) {
        if (!(3 <= args.length && args.length <= 4)) {
            showUsage();
            return;
        }
        String keystoreFilename = args[0];
        String keystoreType = args[1];
        String keystorePassword = args[2];
        String publicKeyFilename = args.length >= 4 ? args[3] : null;
        try {
            // Create keystore class instance
            KeyStore keyStore = KeyStore.getInstance(keystoreType);

            // Load keystore
            keyStore.load(new FileInputStream(keystoreFilename), keystorePassword.toCharArray());

            // Open file for public key if its name was specified
            FileOutputStream publicKeyOutputStream = openFile(publicKeyFilename);
            try {
                // For all keystore alises ....
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    publishKeys(keyStore, aliases.nextElement(), keystorePassword, publicKeyOutputStream);
                }
            } finally {
                close(publicKeyOutputStream);
            }
        } catch (Exception e) {
            System.err.println("Failed");
            e.printStackTrace();
        }
    }

    static void publishKeys(KeyStore keyStore, String alias, String password, FileOutputStream publicKeyOutputStream)
            throws Exception {
        // Get private key text representation
        Key privateKey = keyStore.getKey(alias, password.toCharArray());
        String privateKeyText = DigitalSignatureUtils.convertToString(privateKey);

        // Get public key text representation
        Key publicKey = keyStore.getCertificate(alias).getPublicKey();
        String publicKeyText = DigitalSignatureUtils.convertToString(publicKey);

        // Output to screen
        System.out.println(String.format("Alias is: %s", alias));
        System.out.println(String.format("Public key is: %s", publicKeyText));
        System.out.println(String.format("Private key is: %s", privateKeyText));

        // Output to file
        write(publicKeyOutputStream, publicKeyText.getBytes(CharEncoding.US_ASCII));
    }

    private static FileOutputStream openFile(String filename) throws IOException {
        if (null == filename) {
            return null;
        }
        return new FileOutputStream(filename);
    }

    private static void close(Closeable closeable) throws Exception {
        if (null != closeable) {
            closeable.close();
        }
    }

    private static void write(OutputStream outputStream, byte[] data) throws IOException {
        if (null != outputStream && null != data) {
            outputStream.write(data);
        }
    }

    private static void showUsage() {
        System.out.println("Usage: keystore-filename keystore-type keystore-password [public-key-filename]");
    }
}