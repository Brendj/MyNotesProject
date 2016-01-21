/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report;

import ru.axetta.ecafe.processor.core.mail.Postman;
import ru.axetta.ecafe.processor.core.report.ReportDocument;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 18.01.16
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class ReportPoster implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReportPoster.class);
    private final Postman postman;
    private final String address;
    private final String subject;
    private final byte[] document;

    public ReportPoster(Postman postman, String address, String subject, byte[] document) {
        this.postman = postman;
        this.address = address;
        this.subject = subject;
        this.document = document;
    }

    public void run() {
        File temp = null;
        try {
            temp = File.createTempFile(String.format("DeliveredServicesReport_%s", System.currentTimeMillis()), ".xls");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, document);
            ReportDocument reportDocument = new ReportDocument(temp);
            postman.postReport(address, subject, reportDocument);
            logger.info("Temp file: " + temp.getAbsolutePath());
        }
        catch (Exception e) {
            logger.error("Error sending report by email", e);
        }
        finally {
            if (temp != null && temp.exists()) {
                try {
                    temp.delete();
                } catch (Exception e2) {
                    logger.error(String.format("Can't delete temp file (%s)", temp.getAbsolutePath()), e2);
                }
            }
        }
    }
}
