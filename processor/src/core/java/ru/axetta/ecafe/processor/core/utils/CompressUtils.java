/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtils {
    public static String compressDataInBase64(String data) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(buffer);
        out.write(data.getBytes("UTF-8"));
        out.finish();
        byte[] compressedData=buffer.toByteArray();
        byte[] base64data= Base64.encodeBase64(compressedData);
        return new String(base64data, "UTF-8");
    }
    public static String decompressDataFromBase64(String data) throws Exception {
        byte[] base64data = Base64.decodeBase64(data.getBytes("UTF-8"));
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(base64data));
        byte b[]=new byte[1024];
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (;;) {
            int n=in.read(b);
            if (n==-1) break;
            buf.write(b, 0, n);
        }
        return new String(buf.toByteArray(), "UTF-8");
    }

}
