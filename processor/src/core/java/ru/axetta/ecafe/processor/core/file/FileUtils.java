/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.file;

import ru.axetta.ecafe.processor.core.image.ImageUtils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static ru.axetta.ecafe.processor.core.image.ImageUtils.generateHashFileName;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private static final String FILES_DIRECTORY = "files";
    private static final String DELIMITER = "/";
    private static final String DOT = ".";

    public static String formFilePath(Long idOfOrg, String hashFileName, String fileExt) {
        return System.getProperty("jboss.server.base.dir") + DELIMITER + FILES_DIRECTORY + DELIMITER +
                generateFileName(idOfOrg, hashFileName) + DOT + fileExt;
    }

    public static String generateFileName(Long idOfOrg, String hashFileName) {
        StringBuilder tmp = new StringBuilder();
        tmp.append(idOfOrg);
        tmp.append(DELIMITER);
        tmp.append(hashFileName);
        return tmp.toString();
    }

    public static String saveFile(Long idOfOrg, byte[] file, String fileExt) throws IOException {
        String hashFileName = generateHashFileName(16);
        return saveFile(idOfOrg, file, hashFileName, fileExt);
    }

    public static String saveFile(Long idOfOrg, byte[] file, String fileName, String fileExt) throws IOException {
        writeByteArraysToFile(formFilePath(idOfOrg, fileName, fileExt), file);
        return fileName;
    }

    public static String loadFile(Long idOfOrg, String fileName, String fileExt) {
        String path= formFilePath(idOfOrg, fileName, fileExt);
        byte[] fileData = null;
        try {
            fileData = loadFileAsBytesArray(path);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new String(encodeToBase64(fileData));
    }

    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {
        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
    }

    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {
        File file = new File(fileName);
        BufferedOutputStream writer = null;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(file));
            writer.write(content);
        } catch (NullPointerException e) {
            file.getParentFile().getParentFile().mkdir();
            file.getParentFile().mkdir();
            writer = new BufferedOutputStream(new FileOutputStream(file));
            writer.write(content);
        } catch (IOException e) {
            file.getParentFile().getParentFile().mkdir();
            file.getParentFile().mkdir();
            writer = new BufferedOutputStream(new FileOutputStream(file));
            writer.write(content);
        }
        writer.flush();
        writer.close();
    }

    public static byte[] decodeFromeBase64(String fileBase64Data) {
        return Base64.decodeBase64(fileBase64Data.getBytes());
    }

    public static byte[] encodeToBase64(byte[] fileData) {
        return Base64.encodeBase64(fileData);
    }

    public static Boolean removeFile(Long idOfOrg, String fileName, String fileExt) throws SecurityException {
        String path= formFilePath(idOfOrg, fileName, fileExt);
        File file = new File(path);
        return file.delete();
    }

    public static Long fileSize(Long idOfOrg, String fileName, String fileExt) {
        String path = formFilePath(idOfOrg, fileName, fileExt);
        File file = new File(path);
        if (file.exists())
            return file.length();
        return -1L;
    }
}
