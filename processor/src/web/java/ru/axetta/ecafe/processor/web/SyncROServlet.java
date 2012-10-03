/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.sync.SyncLogger;
import ru.axetta.ecafe.processor.core.sync.SyncProcessor;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.SyncResponse;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 03.10.12
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class SyncROServlet extends HttpServlet  {
    private static final String CONTENT_TYPE = "text/xml", CONTENT_TYPE_GZIPPED= "application/octet-stream";
    private static final Logger logger = LoggerFactory.getLogger(SyncROServlet.class);

    static class RequestData {
        public boolean isCompressed;
        public Document document;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            RequestData requestData = new RequestData();
            // Read XML request
            try {
               // requestData = readRequest(request);
                requestData = readRequestFromFile();  // For tests only!!!
            } catch (Exception e) {
                logger.error("Failed to parse request", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            logger.info(String.format("Starting synchronization with %s", request.getRemoteAddr()));
            // Partial XML parsing to extract IdOfOrg & IdOfSync & type
            long idOfOrg;
            String idOfSync;
            int syncType;
            Node envelopeNode;
            NamedNodeMap namedNodeMap;
            try {
                //envelopeNode = SyncRequest.Builder.findEnvelopeNode(requestData.document);
                //namedNodeMap = envelopeNode.getAttributes();
                //idOfOrg = SyncRequest.Builder.getIdOfOrg(namedNodeMap);
                //idOfSync = SyncRequest.Builder.getIdOfSync(namedNodeMap);
                //syncType = SyncRequest.Builder.getSyncType(namedNodeMap);
            } catch (Exception e) {
                logger.error("Failed to extract required packet attribute", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

           // boolean bLogPackets = (syncType==SyncRequest.TYPE_FULL);

            // Save requestDocument by means of SyncLogger as IdOfOrg-IdOfSync-in.xml
           /* SyncLogger syncLogger = runtimeContext.getSyncLogger();
            if (bLogPackets) syncLogger.registerSyncRequest(requestData.document, idOfOrg, idOfSync);
            else {
                logger.info(String.format("Synchronization with %s - type: %d - packets not logged", request.getRemoteAddr(), syncType));
            }*/

            // Verify XML signature
           /* Org org;
            PublicKey publicKey;
            try {
                org = findOrg(runtimeContext, idOfOrg);
                publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }*/



            // Must be commented for testing!!!
           /* try {
                if (!DigitalSignatureUtils.verify(publicKey, requestData.document)) {
                    logger.error(String.format("Invalid digital signature, IdOfOrg == %s", idOfOrg));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to verify digital signature, IdOfOrg == %s", idOfOrg), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }*/

            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateOnlyFormat.setTimeZone(utcTimeZone);

            TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            dateFormat.setTimeZone(localTimeZone);
            timeFormat.setTimeZone(localTimeZone);

            //Manager.new(datonly,datetime)


            Manager manager=new Manager(dateFormat,timeFormat);


            // Parse XML request
            //TODO: Manager.build(Node)
           try{
            Document requestDocument=requestData.document;

               Node dataNode=requestDocument.getFirstChild();

             Node roNode=dataNode.getFirstChild();
               /* Секция RO можент быть и пустой но идентификатор организации для подготовки ответа возмем */
               manager.setIdOfOrg(0L);
               if(roNode != null){
                   Node itemNode = roNode.getFirstChild();
                   while (null != itemNode) {
                       if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                           manager.build(itemNode);
                       }
                       itemNode = itemNode.getNextSibling();
                   }
               }

               manager.build(roNode);


           }catch(Exception e){
               logger.error("Failed to parse XML request", e);
               response.sendError(HttpServletResponse.SC_BAD_REQUEST);
               return;

           }
           try{
                    manager.process(runtimeContext.createPersistenceSession().getSessionFactory());
           }catch(Exception e){
                logger.error("Failed to process request", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;

           }

            // Sign XML response
            Document responseDocument=null;
             try{
                 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                 DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                 Document document = documentBuilder.newDocument();
                 Element dataElement = document.createElement("Data");
                 document.appendChild(dataElement);
                 dataElement.appendChild(manager.toElement(document)) ;
                 responseDocument = document;
             } catch(Exception e ){
                 logger.error("Failed to serialize response", e);
                     response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                     return;
             }
            // Send XML response

            try {
                writeResponse(response, false , responseDocument);
            } catch (Exception e) {
                logger.error("Failed to write response", e);
                throw new ServletException(e);
            }

            logger.info(String.format("End of synchronization with %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        }
    }

    private static RequestData readRequestFromFile() throws Exception {
        RequestData requestData = new RequestData();
        FileInputStream fileInputStream = new FileInputStream("D:/Projects/request.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        requestData.document = documentBuilder.parse(fileInputStream);
        return requestData;
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
                logger.error(String.format("Unknown org with IdOfOrg == %s", idOfOrg));
                throw new NullPointerException(String.format("Unknown org with IdOfOrg == %s", idOfOrg));
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return org;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
