/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.payment.PaymentLogger;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.manager.IntegroLogger;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 15:51:14
 * To change this template use File | Settings | File Templates.
 */
public class ProcessLogger implements SyncLogger, PaymentLogger, IntegroLogger {

    private static final Logger logger = LoggerFactory.getLogger(ProcessLogger.class);
    private final String syncRequestLogPath;
    private final String syncResponseLogPath;
    private final String paymentRequestLogPath;
    private final String paymentResponseLogPath;
    private final String intgeroRequestLogPath;
    private final String intgeroResponseLogPath;

    public ProcessLogger(String syncRequestLogPath, String syncResponseLogPath, String paymentRequestLogPath,
            String paymentResponseLogPath, String intgeroRequestLogPath, String intgeroResponseLogPath) {
        this.syncRequestLogPath = syncRequestLogPath;
        this.syncResponseLogPath = syncResponseLogPath;
        this.paymentRequestLogPath = paymentRequestLogPath;
        this.paymentResponseLogPath = paymentResponseLogPath;
        this.intgeroRequestLogPath = intgeroRequestLogPath;
        this.intgeroResponseLogPath = intgeroResponseLogPath;
    }

    @Override
    public void registerIntegroRequest(Document requestDocument, long idOfOrg, String idOfSync) {
        if (intgeroRequestLogPath==null) return;
        try {
            File file = createFile(intgeroRequestLogPath, idOfOrg, idOfSync, "in");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(requestDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to save intgero request, idOfOrg == %s", idOfOrg),
                    e);
        }
    }

    @Override
    public void registerIntegroResponse(Document responseDocument, long idOfOrg, String idOfSync) {
        if (intgeroResponseLogPath==null) return;
        try {
            File file = createFile(intgeroResponseLogPath, idOfOrg, idOfSync, "out");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(responseDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to save intgero response, idOfOrg == %s", idOfOrg), e);
        }
    }

    public void registerSyncRequest(Document requestDocument, long idOfOrg, String idOfSync) {
        try {
            File file = createFile(syncRequestLogPath, idOfOrg, idOfSync, "in");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(requestDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to save sync request, idOfOrg == %s, idOfSync == %s", idOfOrg, idOfSync),
                    e);
        }
    }

    public void registerSyncResponse(Document responseDocument, long idOfOrg, String idOfSync) {
        try {
            File file = createFile(syncResponseLogPath, idOfOrg, idOfSync, "out");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(responseDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to save sync response, idOfOrg == %s, idOfSync == %s", idOfOrg, idOfSync), e);
        }
    }

    public void registerPaymentRequest(Document requestDocument, long idOfContragent, String idOfSync) {
        try {
            File file = createFile(paymentRequestLogPath, idOfContragent, idOfSync, "in");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(requestDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to save payment request, idOfContragent == %s, idOfSync == %s",
                    idOfContragent, idOfSync), e);
        }
    }

    public void registerPaymentResponse(Document responseDocument, long idOfContragent, String idOfSync) {
        try {
            File file = createFile(paymentResponseLogPath, idOfContragent, idOfSync, "out");
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                writeDocument(responseDocument, outputStream);
            } finally {
                close(outputStream);
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Failed to save sync response, idOfContragent == %s, idOfSync == %s", idOfContragent,
                            idOfSync), e);
        }
    }

    private static File createFile(String path, Long idOfOrg, String idOfSync, String suffix) throws Exception {
        File file = null;
        long attempt = 0;
        boolean done = false;
        while (!done) {
            String filename = buildFileName(idOfOrg, idOfSync, suffix, attempt);
            file = new File(FilenameUtils.concat(path, filename));
            done = file.createNewFile();
            attempt++;
        }
        return file;
    }

    private static String buildFileName(Long idOfOrg, String idOfSync, String suffix, long attempt) throws Exception {
        StringBuilder filenameBuilder = new StringBuilder();
        filenameBuilder.append(preparePartOfFilename(idOfOrg.toString())).append('-')
                .append(preparePartOfFilename(idOfSync)).append('-').append(preparePartOfFilename(suffix));
        if (0 != attempt) {
            filenameBuilder.append('-').append(attempt);
        }
        filenameBuilder.append(".xml");
        return filenameBuilder.toString();
    }

    private static String preparePartOfFilename(String value) {
        return value.replace(':', '.').replace('\\', '.').replace('/', '.').replace('+', '.');
    }

    private static void writeDocument(Document document, OutputStream outputStream) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    private static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.error("Failed to close", e);
            }
        }
    }

}