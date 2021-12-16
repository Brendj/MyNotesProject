/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.file;

import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.ESPattached;
import ru.axetta.ecafe.processor.core.persistence.OrgFile;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

import static ru.axetta.ecafe.processor.core.image.ImageUtils.generateHashFileName;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private static final String FILES_DIRECTORY = "files";
    private static final String DELIMITER = "/";
    private static final String DOT = ".";

    public static final Long FILES_SIZE_LIMIT = 104857600L;     // 100MB
    public static final Long MAX_FILE_SIZE = 3145728L;          // 3MB

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

    public static String saveFile(Long idOfOrg, byte[] file, String fileExt) throws IOException, FileIsTooBigException {
        String hashFileName = generateHashFileName(16);
        return saveFile(idOfOrg, file, hashFileName, fileExt);
    }

    public static String saveFile(Long idOfOrg, byte[] file, String fileName, String fileExt) throws IOException,
            FileIsTooBigException {
        if (file.length > MAX_FILE_SIZE)
            throw new FileIsTooBigException("file is too big (max size - 3MB)");
        writeByteArraysToFile(formFilePath(idOfOrg, fileName, fileExt), file);
        return fileName;
    }

    public static String saveFile(Long idOfRequestESP, String filedata, String fullfileName) throws IOException
    {
        try {
            StringBuilder tmp = new StringBuilder();
            tmp.append(idOfRequestESP);
            tmp.append(DELIMITER);
            tmp.append(fullfileName);
            String path = tmp.toString();
            String filepath = getBaseFilePathForESP() + path;
            writeByteArraysToFile(filepath, decodeFromeBase64(filedata));
            return path;
        } catch (Exception e)
        {
            return "";
        }
    }

    public static Boolean removeFile(String path) throws SecurityException {
        File file = new File(path);
        return file.delete();
    }

    public static String loadFile(String path) {
        byte[] fileData = null;
        try {
            fileData = loadFileAsBytesArray(getBaseFilePathForESP() + path);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new String(encodeToBase64(fileData));
    }

    public static String getBaseFilePathForESP()
    {
        return System.getProperty("jboss.server.base.dir") + DELIMITER + FILES_DIRECTORY + DELIMITER + "ESP" + DELIMITER;
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

    public static Long getFilesSizeByOrgList(List<OrgFile> orgFileList) {
        Long result = 0L;
        for (OrgFile o : orgFileList) {
            result += fileSize(o.getOrgOwner().getIdOfOrg(), o.getName(), o.getExt());
        }
        return  result;
    }

    public static boolean isFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static class FileUtilsException extends Exception {
        public FileUtilsException() {}

        public FileUtilsException(String message) {
            super(message);
        }
    }

    public static class NotEnoughFreeSpaceException extends FileUtilsException {
        public NotEnoughFreeSpaceException() {}

        public NotEnoughFreeSpaceException(String message) {
            super(message);
        }
    }

    public static class FileIsTooBigException extends FileUtilsException {
        public FileIsTooBigException() {}

        public FileIsTooBigException(String message) {
            super(message);
        }
    }
}
