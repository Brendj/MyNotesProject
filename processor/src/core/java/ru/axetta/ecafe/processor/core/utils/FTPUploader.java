/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by i.semenov on 19.06.2017.
 */
public class FTPUploader {

    private static final int BUFFER_SIZE = 4096;
    private static final Logger logger = LoggerFactory.getLogger(FTPUploader.class);
    private final int port;
    private final FTPClient ftpClient;
    private final String server;
    private final String user;
    private final String password;

    public FTPUploader(String server, int port, String user, String password) {
        ftpClient = new FTPClient();
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public boolean uploadTextFile(String localFileName, String remoteFolder) {
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password); //URLEncoder.encode() user and pass ???
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            File localFile = new File(localFileName);
            String remoteFile = remoteFolder + "/" + localFile.getName();
            InputStream inputStream = new FileInputStream(localFile);
            boolean done = ftpClient.storeFile(remoteFile, inputStream);
            inputStream.close();
            return done;
        } catch (Exception e) {
            logger.error("Can't upload file by ftp", e);
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                logger.error("Error cleanup ftp connection", ex);
            }
        }
    }

    public static void uploadFile(String address, String user, String pass, String file) throws IOException {
        String ftpUrl = String.format("ftp://%s:%s@%s/%s;type=i", user, pass, address, file);

        OutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();

            outputStream = conn.getOutputStream();
            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            logger.info("File uploaded");
        } catch (IOException ex) {
            logger.error("Error sending file by ftp:", ex);
        }  finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }
    }
}
