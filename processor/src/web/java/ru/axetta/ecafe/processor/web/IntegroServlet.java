/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerData;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwnerProcessor;
import ru.axetta.ecafe.processor.core.sync.manager.IntegroLogger;
import ru.axetta.ecafe.processor.core.sync.manager.Manager;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

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
import javax.servlet.annotation.WebServlet;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
@WebServlet(
        name = "IntegroServlet",
        description = "IntegroServlet",
        urlPatterns = {"/integro"}
)
public class IntegroServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/xml", CONTENT_TYPE_GZIPPED = "application/octet-stream";
    private static final Logger logger = LoggerFactory.getLogger(IntegroServlet.class);

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
                requestData = readRequest(request);
                //requestData = readRequestFromFile();  // For tests only!!!
            } catch (Exception e) {
                logger.error("Failed to parse request", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            logger.info(String.format("Starting synchronization with %s", request.getRemoteAddr()));

            IntegroLogger integroLogger = runtimeContext.getIntegroLogger();
            Org org = null;
            String  idOfSync = null;
            Node dataNode = null;
            // Partial XML parsing to extract IdOfOrg & IdOfSync & type
            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateOnlyFormat.setTimeZone(utcTimeZone);
            try{
                TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);//TimeZone.getTimeZone("Europe/Moscow");
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                dateFormat.setTimeZone(localTimeZone);
                timeFormat.setTimeZone(localTimeZone);
                Document requestDocument = requestData.document;
                dataNode = requestDocument.getFirstChild();
                NamedNodeMap namedNodeMap=dataNode.getAttributes();
                org = findOrg(runtimeContext, getIdOfOrg(namedNodeMap));
                idOfSync = getIdOfSync(namedNodeMap);
                integroLogger.registerIntegroRequest(requestData.document, org.getIdOfOrg(), idOfSync);
            } catch (Exception e){
                logger.error("",e);
                return;
            }

            //PublicKey publicKey;
            //try {
            //    idOfOrg = findOrg(runtimeContext, idOfOrg);
            //    publicKey = DigitalSignatureUtils.convertToPublicKey(org.getPublicKey());
            //} catch (Exception e) {
            //    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            //    return;
            //}
            /* Must be commented for testing!!!  */
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
            /* Секция RO можент быть и пустой но идентификатор организации для подготовки ответа возмем */

            Document responseDocument = null;

            if(org.getCommodityAccounting()){
                //String[]  doGroupNames = new String[]{"ProductsGroup", "DocumentGroup"};
                List<String> doGroupNames = Arrays.asList("ProductsGroup", "DocumentGroup");

                Manager manager = new Manager(org.getIdOfOrg(), doGroupNames);
                try {
                    // Save requestDocument by means of SyncLogger as IdOfOrg-in.xml

                    Node roNode = dataNode.getFirstChild();
                    roNode=roNode.getNextSibling();
                    if (roNode != null) {
                        manager.buildRO(roNode);
                    }
                    //manager.build(roNode);
                } catch (Exception e) {
                    logger.error("Failed to parse XML request", e);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                try {
                    SyncHistory syncHistory = runtimeContext.getProcessor().createSyncHistoryIntegro(org.getIdOfOrg(),
                            new Date(), "1C", request.getRemoteAddr());
                    manager.setSyncHistory(syncHistory);
                    manager.process(runtimeContext.createPersistenceSession().getSessionFactory());
                } catch (Exception e) {
                    logger.error("Failed to process request", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                // Sign XML response

                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.newDocument();
                    Element dataElement = document.createElement("Data");
                    document.appendChild(dataElement);
                    dataElement.appendChild(manager.toElement(document));
                    AbstractProcessor processor = new OrgOwnerProcessor(runtimeContext.createPersistenceSession(), org.getIdOfOrg());
                    OrgOwnerData orgOwnerData = (OrgOwnerData) processor.process();
                    dataElement.appendChild(orgOwnerData.toElement(document));
                    responseDocument = document;
                } catch (Exception e) {
                    logger.error("Failed to serialize response", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }

            // Send XML response
            try {
                integroLogger.registerIntegroResponse(responseDocument, org.getIdOfOrg(), idOfSync);
                writeResponse(response, true, responseDocument);
            } catch (Exception e) {
                logger.error("Failed to write response", e);
                throw new ServletException(e);
            }

            logger.info(String.format("End of synchronization with %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        }
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
        } else if (!StringUtils.equals(CONTENT_TYPE, httpRequest.getContentType())) {
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

    private static void writeResponse(HttpServletResponse httpResponse, boolean useCompression,
            Document responseDocument) throws Exception {
        httpResponse.setContentType(useCompression ? CONTENT_TYPE_GZIPPED : CONTENT_TYPE);
        httpResponse.setCharacterEncoding(responseDocument.getXmlEncoding());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        OutputStream outputStream =
                useCompression ? new GZIPOutputStream(httpResponse.getOutputStream()) : httpResponse.getOutputStream();
        transformer.transform(new DOMSource(responseDocument), new StreamResult(outputStream));
        if (outputStream instanceof GZIPOutputStream) {
            ((GZIPOutputStream) outputStream).finish();
        }
    }

    public static String getIdOfSync(NamedNodeMap namedNodeMap) throws Exception {
        return namedNodeMap.getNamedItem("Date").getTextContent();
    }

    public static long getIdOfOrg(NamedNodeMap namedNodeMap) throws Exception {
        return getLongValue(namedNodeMap, "IdOfOrg");
    }

    private static long getLongValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node n = namedNodeMap.getNamedItem(name);
        return Long.parseLong(n.getTextContent());
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
            //Long ifOfOrg = DAOUtils.getIdOfOrg(persistenceSession, idOfOrg);
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
