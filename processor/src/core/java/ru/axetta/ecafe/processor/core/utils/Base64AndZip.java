/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 27.07.12
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class Base64AndZip {

    private static final int BUFFER = 2048;

    public static byte[] zip(byte[] data) throws IOException {
        BufferedInputStream origin = new BufferedInputStream(new ByteArrayInputStream(data), BUFFER);

        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

        ZipEntry entry = new ZipEntry("main");
        out.putNextEntry(entry);
        int count;
        byte[] buffer = new byte[BUFFER];
        while ((count = origin.read(buffer, 0, BUFFER)) != -1) {
            out.write(buffer, 0, count);
        }
        origin.close();
        out.close();
        return dest.toByteArray();
    }

    public static byte[] unzip(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream dest = new BufferedOutputStream(byteArrayOutputStream, BUFFER);

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
        int count;
        byte buffer[] = new byte[BUFFER];
        zis.getNextEntry();
        while ((count = zis.read(buffer, 0, BUFFER)) != -1) {
            dest.write(buffer, 0, count);
        }
        dest.flush();
        dest.close();
        zis.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] encode(byte[] data) {
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String result = base64Encoder.encode(data);
        return result.getBytes();
    }

    public static byte[] decode(byte[] data) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        return base64Decoder.decodeBuffer(new String(data));
    }

    public static byte[] unzipAndDecode(byte[] data) throws IOException {
        return decode(unzip(data));
    }

    public static byte[] encodeAndZip(byte[] data) throws IOException {
        return zip(encode(data));
    }
}
