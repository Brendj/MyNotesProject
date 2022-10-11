/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.mail;

import ru.axetta.ecafe.processor.core.report.AutoReportPostman;
import ru.axetta.ecafe.processor.core.report.ReportDocument;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 16.12.2009
 * Time: 11:23:15
 * To change this template use File | Settings | File Templates.
 */
public class Postman implements AutoReportPostman {

    private static final Logger logger = LoggerFactory.getLogger(Postman.class);

    public static class SmtpSettings {
        public static final int defaultTimeout = 60000;
        public static final int defaultConnectionTimeout = 10000;

        private final String host;
        private final int port;
        private final boolean startTLS;
        private final String user;
        private final String password;
        private final int timeout;
        private final int connectionTimeout;

        public SmtpSettings(String host, int port) {
            this.host = host;
            this.port = port;
            this.startTLS = false;
            this.user = null;
            this.password = null;
            this.timeout = defaultTimeout;
            this.connectionTimeout = defaultConnectionTimeout;
        }

        public SmtpSettings(String host, int port, String user, String password) {
            this.host = host;
            this.port = port;
            this.startTLS = false;
            this.user = user;
            this.password = password;
            this.timeout = defaultTimeout;
            this.connectionTimeout = defaultConnectionTimeout;
        }

        public SmtpSettings(String host, int port, boolean startTLS, String user, String password, int timeout,
                            int connectionTimeout) {
            this.host = host;
            this.port = port;
            this.startTLS = startTLS;
            this.user = user;
            this.password = password;
            this.timeout = timeout;
            this.connectionTimeout = connectionTimeout;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public boolean isStartTLS() {
            return startTLS;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }
    }

    public static class MailSettings {

        private final SmtpSettings smtpSettings;
        private final InternetAddress fromAddress;
        private final InternetAddress copyAddress;

        public MailSettings(SmtpSettings smtpSettings, InternetAddress fromAddress, InternetAddress copyAddress) {
            this.smtpSettings = smtpSettings;
            this.fromAddress = fromAddress;
            this.copyAddress = copyAddress;
        }

        public SmtpSettings getSmtpSettings() {
            return smtpSettings;
        }

        public InternetAddress getFromAddress() {
            return fromAddress;
        }

        public InternetAddress getCopyAddress() {
            return copyAddress;
        }
    }

    private final MailSettings reportMailSettings;
    private final MailSettings supportMailSettings;

    public Postman(MailSettings reportMailSettings, MailSettings supportMailSettings) {
        this.reportMailSettings = reportMailSettings;
        this.supportMailSettings = supportMailSettings;
    }

    public void postReport(String address, String subject, ReportDocument reportDocument) throws Exception {
        postFiles(reportMailSettings, address, subject, reportDocument.getFiles(), "REPORT");
    }

    public void postEvent(String address, String subject, ReportDocument eventDocument) throws Exception {
        postFiles(reportMailSettings, address, subject, eventDocument.getFiles(), "EVENT");
    }

    public void postNotificationEmail(String address, String subject, String text) throws Exception {
        postText(supportMailSettings, address, subject, text, null, false);
    }

    public void postSupportEmail(String address, String subject, String text, List<ru.axetta.ecafe.processor.core.mail.File> files) throws Exception {
        postText(supportMailSettings, address, subject, text, files, true);
    }

    private static Session createMailSession(MailSettings mailSettings) throws Exception {
        Properties properties = new Properties();
        final SmtpSettings smtpSettings = mailSettings.getSmtpSettings();
        properties.put("mail.smtp.ssl.checkserveridentity", "false");
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.host", smtpSettings.getHost());
        properties.put("mail.smtp.port", Integer.toString(smtpSettings.getPort()));
        properties.put("mail.smtp.timeout", Integer.toString(smtpSettings.getTimeout()));
        properties.put("mail.smtp.connectiontimeout", Integer.toString(smtpSettings.getConnectionTimeout()));
        if (smtpSettings.isStartTLS()) {
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.port", smtpSettings.getPort());
            properties.put("mail.smtp.socketFactory.fallback", "false");
        }
        Authenticator smtpAauthenticator = null;
        if (null != smtpSettings.getUser()) {
            properties.put("mail.smtp.auth", "true");
            smtpAauthenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpSettings.getUser(), smtpSettings.getPassword());
                }
            };
        }
        return Session.getInstance(properties, smtpAauthenticator);
    }

    private static String readFile(File file) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new java.io.FileInputStream(file), "Cp1251"));

        String str = "";
        String line;

        while ((line = in.readLine()) != null) {
            str += line;
        }

        in.close();
        return str;
    }

    private static void postFiles(MailSettings mailSettings, String address, String subject, List<File> files,
            String mode) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Posting file with subject \"%s\" to \"%s\"", subject, address));
        }
        Session mailSession = createMailSession(mailSettings);
        MimeMessage mailMessage = new MimeMessage(mailSession);
        mailMessage.setFrom(mailSettings.getFromAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
        if (null != mailSettings.getCopyAddress()) {
            mailMessage.setRecipient(Message.RecipientType.CC, mailSettings.getCopyAddress());
        }
        mailMessage.setSubject(StringUtils.defaultString(subject), "utf-8");
        mailMessage.setSentDate(new Date());

        boolean singleContentBody = false;
        if (1 == files.size()) {
            File file = files.iterator().next();
            String extension = FilenameUtils.getExtension(file.getAbsolutePath());
            if (StringUtils.equalsIgnoreCase(extension, "htm") || StringUtils.equalsIgnoreCase(extension, "html")) {
                singleContentBody = true;
                if (mode.equals("EVENT")) {
                    FileDataSource fileDataSource = new FileDataSource(file);
                    mailMessage.setDataHandler(new DataHandler(fileDataSource));
                } else if (mode.equals("REPORT")) {
                    String s = readFile(file);
                    mailMessage.setContent(s, "text/html; charset=windows-1251");
                }
            }
        }
        if (!singleContentBody) {
            Multipart mailBody = new MimeMultipart();
            for (File file : files) {
                FileDataSource fileDataSource = new FileDataSource(file);
                MimeBodyPart mimeAttach = new MimeBodyPart();
                mimeAttach.setDataHandler(new DataHandler(fileDataSource));
                mimeAttach.setFileName(fileDataSource.getName());
                mailBody.addBodyPart(mimeAttach);
            }
            mailMessage.setContent(mailBody);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Starting posting files with subject \"%s\" to \"%s\"", subject, address));
        }
        try {
            Transport.send(mailMessage);
        } catch (Exception e) {
            logger.error("Failed to transfer email", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Finished posting files with subject \"%s\" to \"%s\"", subject, address));
        }
    }

    private static void postText(MailSettings mailSettings, String address, String subject, String text, List<ru.axetta.ecafe.processor.core.mail.File> files, boolean sendCopyEmail)
            throws Exception {
        logger.info(String.format("Posting text with subject \"%s\" to \"%s\"", subject, address));
        Session mailSession = createMailSession(mailSettings);
        MimeMessage mailMessage = new MimeMessage(mailSession);
        mailMessage.setFrom(mailSettings.getFromAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
        if (sendCopyEmail && null != mailSettings.getCopyAddress()) {
            mailMessage.setRecipient(Message.RecipientType.CC, mailSettings.getCopyAddress());
        }
        mailMessage.setSubject(StringUtils.defaultString(subject), "utf-8");
        mailMessage.setSentDate(new Date());

        // Set the email attachment file
        if (files!=null && !files.isEmpty()) {
            // Set the email message text.
            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(text);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messagePart);

            for (ru.axetta.ecafe.processor.core.mail.File file : files) {
                File f = file.getFile();
                String fileName = file.getFileName();
                final String contentType = file.getContentType();
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(f) {
                    @Override
                    public String getContentType() {
                        return contentType;
                    }
                };
                attachmentPart.setDataHandler(new DataHandler(fileDataSource));
                attachmentPart.setFileName(fileName);
                multipart.addBodyPart(attachmentPart);
            }

            mailMessage.setContent(multipart);
        }
        else {
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(text, "text/html; charset=\"UTF-8\"");
            mailMessage.setDataHandler(new DataHandler(byteArrayDataSource));
        }

        logger.info(String.format("Starting posting text with subject \"%s\" to \"%s\"", subject, address));
        try {
            Transport.send(mailMessage);
        } catch (Exception e) {
            logger.error("Failed to transfer email", e);
            throw e;
        }
        logger.info(String.format("Finished posting text with subject \"%s\" to \"%s\"", subject, address));
    }

}
