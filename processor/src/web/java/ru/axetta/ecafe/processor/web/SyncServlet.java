/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.SyncCollector;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 16:02:24
 */
public class SyncServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/xml", CONTENT_TYPE_GZIPPED= "application/octet-stream";
    private static final Logger logger = LoggerFactory.getLogger(SyncServlet.class);
    private static final SyncCollector SYNC_COLLECTOR = SyncCollector.getInstance();
    private static final HashSet<Long> syncsInProgress = new HashSet<Long>();
    private static final HashSet<Long> fullSyncsInProgress = new HashSet<Long>();
    private static final HashSet<Long> accIncSyncsInProgress = new HashSet<Long>();
    private static final List<String[]> restrictedFullSyncPeriods =
            getRestrictPeriods(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_RESTRICT_FULL_SYNC_PERIODS));
    private static final AtomicLong threadCounter = new AtomicLong();
    public static final int MAX_SYNCS = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SIMULTANEOUS_SYNC_THREADS);
    public static final int permitsTimeout = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SIMULTANEOUS_SYNC_TIMEOUT);
    public static final int permitsAccIncTimeout = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SIMULTANEOUS_ACCINC_SYNC_TIMEOUT);
    private static final Semaphore permitsForSync = new Semaphore(MAX_SYNCS, true);
    private final static ThreadLocal<Boolean> currentSyncWasGranted = new ThreadLocal<Boolean>(){
        @Override protected Boolean initialValue() { return false; }
    };

    static class RequestData {
        public boolean isCompressed;
        public Document document;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        Long syncTime = new Date().getTime();
        SyncCollector.registerSyncStart(syncTime);
        long idOfOrg = -1;
        try {
            currentSyncWasGranted.set(false);
            runtimeContext = RuntimeContext.getInstance();

            RequestData requestData = new RequestData();
            // Read XML request
            try {
                requestData = readRequest(request);
            } catch (Exception e) {
                logger.error("Failed to parse request", e);
                String message = String.format("Failed to parse request: %s", e.getMessage());
                sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Partial XML parsing to extract IdOfOrg & IdOfSync & type
            Node envelopeNode;
            NamedNodeMap namedNodeMap;

            String idOfSync;
            SyncType syncType;
            try {
                envelopeNode = SyncRequest.Builder.findEnvelopeNode(requestData.document);
                namedNodeMap = envelopeNode.getAttributes();
                idOfOrg = SyncRequest.Builder.getIdOfOrg(namedNodeMap);
                idOfSync = SyncRequest.Builder.getIdOfSync(namedNodeMap);
                syncType = SyncRequest.Builder.getSyncType(namedNodeMap);
                SyncCollector.setIdData(syncTime, idOfOrg, idOfSync, syncType);
                if(syncType==null) throw new Exception("Unknown sync type");
                Thread.currentThread().setName("SyncServlet-" + syncType + "-" + idOfOrg + "-n" + threadCounter.addAndGet(1));
            } catch (Exception e) {
                String message = String.format("Failed to extract required packet attribute [remote address: %s]",
                        request.getRemoteAddr());
                logger.error(message, e);
                sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            //Время запрета полной синхронизации
            if (syncType==SyncType.TYPE_FULL && isRestrictedFullSyncPeriod()) {
                String message = String.format("Full sync not allowed in this time, idOfOrg=%d", idOfOrg);
                logger.error(message);
                response.addHeader("Retry-After", String.valueOf(LimitFilter.RETRY_AFTER));
                sendError(response, syncTime, message, LimitFilter.SC_TOO_MANY_REQUESTS);
                return;
            }

            /////// Недопущение двух и более одновременных синхронизаций от одной организации
            boolean success, tooManyRequests = false;
            int allSyncsCount, fullSyncsCount, accIncSyncsCount = 0;
            synchronized(syncsInProgress) {
                success = syncsInProgress.add(idOfOrg);
                if (success && syncType==SyncType.TYPE_FULL) {
                    fullSyncsInProgress.add(idOfOrg);
                }
                //Тип синхры AccInc - разрешаем выполнение одновременно с другими типами
                if (syncType==SyncType.TYPE_GET_ACC_INC) {
                    success = accIncSyncsInProgress.add(idOfOrg);
                }
                // ограничение количества одновременных синхр - срабатывает только для полных синхр
                if (success && (syncType==SyncType.TYPE_FULL &&
                        fullSyncsInProgress.size()>runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_LIMITS))) {
                    tooManyRequests = true;
                }
                allSyncsCount = syncsInProgress.size();
                fullSyncsCount = fullSyncsInProgress.size();
                accIncSyncsCount = accIncSyncsInProgress.size();
            }
            if (!success) {
                String message = String.format("Failed to perform this sync from idOfOrg=%s. This IdOfOrg is currently in sync", idOfOrg);
                logger.error(message);
                sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (tooManyRequests) {
                String message = String.format("Failed to perform this sync from idOfOrg=%s. Too many active requests. Current count syncs: %s, full syncs: %s, accInc syncs: %s",
                        idOfOrg, allSyncsCount, fullSyncsCount, accIncSyncsCount);
                logger.error(message);
                sendError(response, syncTime, message, LimitFilter.SC_TOO_MANY_REQUESTS);
                return;
            }
            ///////
            long currentTime = System.currentTimeMillis();
            boolean useQueueForAllSyncs = runtimeContext.isUseQueueForAllSyncs();
            if (useQueueForAllSyncs || syncType != SyncType.TYPE_GET_ACC_INC) {
                long timeout = (syncType == SyncType.TYPE_GET_ACC_INC) ? permitsAccIncTimeout : permitsTimeout;
                if (permitsForSync.tryAcquire(timeout, TimeUnit.MINUTES)) {
                    currentSyncWasGranted.set(true);
                } else {
                    String message = "Failed to perform this sync. Wait timeout is expired. Available permits is " + permitsForSync.availablePermits();
                    logger.error(message);
                    sendError(response, syncTime, message, LimitFilter.SC_TOO_MANY_REQUESTS);
                    return;
                }
            }


            long begin_sync = System.currentTimeMillis();
            logger.info(String.format("-Starting synchronization with %s: id: %s, current count syncs: %s, full syncs: %s, accInc syncs: %s, available permits: %s, permit acquired in: %s ms",
                    request.getRemoteAddr(), idOfOrg, allSyncsCount, fullSyncsCount, accIncSyncsCount, permitsForSync.availablePermits(), System.currentTimeMillis() - currentTime));

            boolean bLogPackets = (syncType==SyncType.TYPE_FULL);

            // Save requestDocument by means of SyncLogger as IdOfOrg-IdOfSync-in.xml
            SyncLogger syncLogger = runtimeContext.getSyncLogger();
            /* Must be FALSE for testing!!!  */
            boolean verifySignature = true;
            if (RuntimeContext.getInstance().isTestMode()){
                verifySignature = false;
                //todo delete
            }

            if (!verifySignature || bLogPackets) {
                syncLogger.registerSyncRequest(requestData.document, idOfOrg, idOfSync);
            }

            // Verify XML signature
            Org org;
            PublicKey publicKey;
            try {
                //org = findOrg(runtimeContext, idOfOrg);
                org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
                publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
            } catch (Exception e) {
                logger.error("Error convert org public key: ", e);
                String message = ((Integer) HttpServletResponse.SC_BAD_REQUEST).toString() + ": " + e.getMessage();
                sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                if (verifySignature && !DigitalSignatureUtils.verify(publicKey, requestData.document)) {
                    String message = String.format("Invalid digital signature, IdOfOrg == %s", idOfOrg);
                    logger.error(message);
                    sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to verify digital signature, IdOfOrg == %s", idOfOrg), e);
                String message = String.format("Failed to verify digital signature, IdOfOrg == %s", idOfOrg);
                sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }


            //  Daily logging for sync request
            syncLogger.registerSyncRequestInDb(idOfOrg, idOfSync);


            // Parse XML request
            SyncRequest syncRequest;
            try {
                SyncRequest.Builder syncRequestBuilder = new SyncRequest.Builder();
                syncRequest = syncRequestBuilder.build(envelopeNode, namedNodeMap, org, idOfSync, request.getRemoteAddr());
            } catch (Exception e) {
                logger.error(String.format("Failed to parse XML request. IdOfOrg=%s", idOfOrg), e);
                String msg = String.format("Failed to parse XML request: %s", e.getMessage());
                sendError(response, syncTime, msg, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Process request
            SyncResponse syncResponse;
            try {
                SyncProcessor processor = runtimeContext.getSyncProcessor();
                syncResponse = processor.processSyncRequest(syncRequest);
                syncRequest = null;
            } catch (Exception e) {
                logger.error(String.format("Failed to process request. IdOfOrg=%s", idOfOrg), e);
                String message = String.format("Failed to serialize response: %s", e.getMessage());
                sendError(response, syncTime, message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Sign XML response
            Document responseDocument;
            try {
                responseDocument = syncResponse.toDocument();
                syncResponse = null;
                DigitalSignatureUtils.sign(runtimeContext.getSyncPrivateKey(), responseDocument);
            } catch (Exception e) {
                logger.error(String.format("Failed to serialize response. IdOfOrg=%s", idOfOrg), e);
                String format = String.format("Failed to serialize response: %s", e.getMessage());
                sendError(response, syncTime, format, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            if (bLogPackets) {
                // Save responseDocument by means of SyncLogger as IdOfOrg-IdOfSync-out.xml
                syncLogger.registerSyncResponse(responseDocument, idOfOrg, idOfSync);
            }

            // Send XML response
            try {
                writeResponse(response, requestData.isCompressed, responseDocument);
            } catch (Exception e) {
                logger.error(String.format("Failed to write response. IdOfOrg=%s", idOfOrg), e);
                throw new ServletException(e);
            }

            final String message = String.format("End of synchronization with %s: id: %s, time taken: %s ms",
                    request.getRemoteAddr(), idOfOrg, System.currentTimeMillis() - begin_sync);
            logger.info(message);
        } catch (RuntimeContext.NotInitializedException e) {
            SyncCollector.setErrMessage(syncTime, e.getMessage());
            SyncCollector.registerSyncEnd(syncTime);
            throw new UnavailableException(e.getMessage());
        } catch (InterruptedException ie) {
            String message = "Failed to perform this sync after interrupted exception.";
            logger.error(message);
            sendError(response, syncTime, message, HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            SyncCollector.registerSyncEnd(syncTime);
            removeSyncInProgress(idOfOrg);
            if (currentSyncWasGranted.get()) permitsForSync.release();
        }
    }

    private void removeSyncInProgress(long idOfOrg) {
        synchronized (syncsInProgress) {
            syncsInProgress.remove(idOfOrg);
            fullSyncsInProgress.remove(idOfOrg);
            accIncSyncsInProgress.remove(idOfOrg);
        }
    }

    private void sendError(HttpServletResponse response, Long syncTime, String msgString, int responseResultCode) throws IOException {
        String errorMsg = ((Integer) responseResultCode).toString() + ": " + msgString;
        SyncCollector.setErrMessage(syncTime, errorMsg);
        SyncCollector.registerSyncEnd(syncTime);
        response.sendError(responseResultCode, msgString);
    }

    private static RequestData readRequest(HttpServletRequest httpRequest) throws Exception {
        RequestData requestData = new RequestData();
        if (StringUtils.isEmpty(httpRequest.getCharacterEncoding())) {
            httpRequest.setCharacterEncoding(CharEncoding.UTF_8);
        }

        InputStream inputStream = null;
        if (StringUtils.equals(CONTENT_TYPE_GZIPPED, httpRequest.getContentType())) {
            inputStream = new GZIPInputStream(httpRequest.getInputStream());
            requestData.isCompressed = true;
        }
        else if (!StringUtils.equals(CONTENT_TYPE, httpRequest.getContentType())) {
            throw new RuntimeException("Invalid content type");
        } else {
            inputStream = httpRequest.getInputStream();
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestData.document = documentBuilder.parse(inputStream);
        return requestData;
    }

    private static void writeResponse(HttpServletResponse httpResponse, boolean useCompression, Document responseDocument) throws Exception {
        httpResponse.setContentType(useCompression?CONTENT_TYPE_GZIPPED:CONTENT_TYPE);
        httpResponse.setCharacterEncoding(responseDocument.getXmlEncoding());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        OutputStream outputStream = useCompression?new GZIPOutputStream(httpResponse.getOutputStream()):httpResponse.getOutputStream();
        transformer.transform(new DOMSource(responseDocument), new StreamResult(outputStream));
        if (outputStream instanceof GZIPOutputStream) ((GZIPOutputStream)outputStream).finish();
    }

    private Org findOrg(RuntimeContext runtimeContext, Long idOfOrg) throws Exception {
        PublicKey publicKey;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            // Start data model transaction
            persistenceTransaction = persistenceSession.beginTransaction();
            // Find given org
            Org org = (Org) persistenceSession.get(Org.class, idOfOrg);
            if (null == org) {
                final String message = String.format("Unknown org with IdOfOrg == %s", idOfOrg);
                logger.error(message);
                throw new NullPointerException(message);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return org;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static List<String[]> getRestrictPeriods(String option) {
        List<String[]> result = new ArrayList<String[]>();
        if (option.equals("")) {
            logger.info("Option OPTION_REQUEST_SYNC_LIMITFILTER is empty");
            return result;
        }
        try {
            String[] arr = option.trim().split(";");
            for (String period : arr) {
                String[] time = period.trim().split("-");
                result.add(time);
            }
        }
        catch(Exception ex) {
            logger.error("Invalid option OPTION_REQUEST_SYNC_LIMITFILTER is set in config");
        }
        return result;
    }

    private boolean isRestrictedFullSyncPeriod() {
        try {
            for (String[] period : restrictedFullSyncPeriods) {
                String[] bPeriod = period[0].trim().split(":");
                Calendar c1 = new GregorianCalendar();
                c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bPeriod[0].trim()));
                c1.set(Calendar.MINUTE, Integer.parseInt(bPeriod[1].trim()));
                c1.set(Calendar.SECOND,0);

                String[] ePeriod = period[1].trim().split(":");
                Calendar c2 = new GregorianCalendar();
                c2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ePeriod[0].trim()));
                c2.set(Calendar.MINUTE, Integer.parseInt(ePeriod[1].trim()));
                c2.set(Calendar.SECOND,0);

                if (CalendarUtils.betweenDate(new Date(), c1.getTime(), c2.getTime())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            logger.error("Error in isRestrictedFullSyncPeriod: ", e);
        }
        return false;
    }

}
