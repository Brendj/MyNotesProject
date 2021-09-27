/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.payment.PaymentLogger;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.SyncLogInfo;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.manager.IntegroLogger;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private final SimpleDateFormat dateFormat;
    private final List<SyncLogInfo> logInfoList;
    private final Object syncObject;

    private final int SYNC_LOG_COUNT = 100;

    public ProcessLogger(String syncRequestLogPath, String syncResponseLogPath, String paymentRequestLogPath,
            String paymentResponseLogPath, String intgeroRequestLogPath, String intgeroResponseLogPath) {
        this.syncRequestLogPath = syncRequestLogPath;
        this.syncResponseLogPath = syncResponseLogPath;
        this.paymentRequestLogPath = paymentRequestLogPath;
        this.paymentResponseLogPath = paymentResponseLogPath;
        this.intgeroRequestLogPath = intgeroRequestLogPath;
        this.intgeroResponseLogPath = intgeroResponseLogPath;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.logInfoList = new ArrayList<>();
        this.syncObject = new Object();
    }

    @Override
    public void registerIntegroRequest(Document requestDocument, long idOfOrg, String idOfSync) {
        if (intgeroRequestLogPath==null) return;
        try {
            String datePath = String.format("%s/%s/", intgeroRequestLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfOrg, idOfSync, "in");
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
            String datePath = String.format("%s/%s/", intgeroResponseLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfOrg, idOfSync, "out");
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
            String datePath = String.format("%s/%s/", syncRequestLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfOrg, idOfSync, "in");
            //File file = createFile(syncRequestLogPath, idOfOrg, idOfSync, "in");
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

    public void registerSyncRequestInDb(long idOfOrg, String idOfSync) {
        DAOService.getInstance().registerSyncRequest(idOfOrg, idOfSync);
    }

    @Async
    public void queueSyncRequestAsync(long idOfOrg, String idOfSync) {
        synchronized (syncObject) {
            logInfoList.add(new SyncLogInfo(idOfOrg, idOfSync));
        }
    }

    public void runRegisterSyncRequestInDb() {
        String query = "INSERT INTO cf_synchistory_daily (idofsync, idoforg, syncdate) VALUES ";
        synchronized (syncObject) {
            if (logInfoList.size() == 0) return;
            logger.info(String.format("Start runRegisterSyncRequestInDb. Current count = %s", logInfoList.size()));
            int counter = 0;
            Iterator<SyncLogInfo> iterator = logInfoList.iterator();
            while (iterator.hasNext()) {
                SyncLogInfo info = iterator.next();
                query += String.format("('%s', %s, %s), ", info.getIdOfSync(), info.getIdOfOrg(), info.getSyncTime());
                iterator.remove();
                counter++;
                if (counter > SYNC_LOG_COUNT) break;
            }
            query = query.substring(0, query.length()-2);
        }
        try {
            DAOService.getInstance().saveSyncRequestInDb(query);
        } catch (Exception e) {
            logger.error("Error in runRegisterSyncRequestInDb: ", e);
        }
    }

    public void registerSyncResponse(Document responseDocument, long idOfOrg, String idOfSync) {
        try {
            String datePath = String.format("%s/%s/", syncResponseLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfOrg, idOfSync, "out");
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
            String datePath = String.format("%s/%s/", paymentRequestLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfContragent, idOfSync, "in");
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
            String datePath = String.format("%s/%s/", paymentResponseLogPath, dateFormat.format(new Date()));
            File syncResponseLogPathDay = new File(datePath);
            if(!syncResponseLogPathDay.exists()){
                boolean result = syncResponseLogPathDay.mkdir();
            }
            File file = createFile(datePath, idOfContragent, idOfSync, "out");
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