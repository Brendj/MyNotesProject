/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.AccessDiniedException;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.payment.PaymentLogger;
import ru.axetta.ecafe.processor.core.payment.PaymentProcessor;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.payment.PaymentResponse;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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
import java.security.PublicKey;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.07.2009
 * Time: 16:02:24
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "PaymentServlet",
        description = "PaymentServlet",
        urlPatterns = {"/pay"}
)
public class PaymentServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/xml";
    private static final Logger logger = LoggerFactory.getLogger(PaymentServlet.class);

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            String userName = StringUtils.defaultString(request.getParameter("username"));
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Processing payments from user: %s", userName));
            }
            Long idOfUser;
            try {
                idOfUser = findUser(runtimeContext, userName,
                        StringUtils.defaultString(request.getParameter("password")));
            } catch (Exception e) {
                logger.error(String.format("Failed to check user, no such user: %s", userName), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (null == idOfUser) {
                logger.error("Failed to check user: invalid password");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Read XML request
            Document requestDocument;
            try {
                requestDocument = readRequest(request);
            } catch (Exception e) {
                logger.error("Failed to parse request", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            logger.info(String.format("Starting synchronization with %s", request.getRemoteAddr()));

            // Partial XML parsing to extract IdOfOrg & IdOfSync
            long idOfContragent;
            String idOfSync;
            Node envelopeNode;
            NamedNodeMap namedNodeMap;
            try {
                envelopeNode = PaymentRequest.Builder.findEnvelopeNode(requestDocument);
                namedNodeMap = envelopeNode.getAttributes();
                idOfContragent = PaymentRequest.Builder.getIdOfContragent(namedNodeMap);
                idOfSync = PaymentRequest.Builder.getIdOfSync(namedNodeMap);
            } catch (Exception e) {
                logger.error("Failed to extract IdOfOrg", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Save requestDocument by means of SyncLogger as IdOfOrg-IdOfSync-in.xml
            PaymentLogger paymentLogger = runtimeContext.getPaymentLogger();
            paymentLogger.registerPaymentRequest(requestDocument, idOfContragent, idOfSync);

            // Verify XML signature
            PublicKey publicKey;
            try {
                //publicKey = extractPublicKey(runtimeContext, idOfContragent);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                if (false && !DigitalSignatureUtils.verify(publicKey, requestDocument)) {
                    logger.error(String.format("Invalid digital signature, IdOfOrg == %s", idOfContragent));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to verify digital signature, IdOfOrg == %s", idOfContragent), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Parse XML request
            PaymentRequest paymentRequest;
            try {
                PaymentRequest.Builder paymentRequestBuilder = new PaymentRequest.Builder();
                paymentRequest = paymentRequestBuilder
                        .build(envelopeNode, namedNodeMap, idOfUser, idOfContragent, idOfSync);
                requestDocument = null;
            } catch (Exception e) {
                logger.error("Failed to parse XML request", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            //add username to addidofpayment


            for (PaymentRequest.PaymentRegistry.Payment payment :  paymentRequest.getPaymentRegistry().getPaymentsList()) {
                payment.setAddIdOfPayment(userName + "_" + payment.getAddIdOfPayment());
            }

            // Process request
            PaymentResponse paymentResponse;
            try {
                PaymentProcessor processor = runtimeContext.getPaymentProcessor();
                paymentResponse = processor.processPayRequest(paymentRequest);
                paymentRequest = null;
            } catch (AccessDiniedException e) {
                logger.error("Failed to process request: no rights to process payments", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                logger.error("Failed to process request", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Sign in XML response
            Document responseDocument;
            try {
                responseDocument = paymentResponse.toDocument();
                paymentResponse = null;
                DigitalSignatureUtils.sign(runtimeContext.getPaymentPrivateKey(), responseDocument);
            } catch (Exception e) {
                logger.error("Failed to serialize response", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Save responseDocument by means of SyncLogger as IdOfOrg-IdOfSync-out.xml
            paymentLogger.registerPaymentResponse(responseDocument, idOfContragent, idOfSync);

            // Send XML response
            try {
                writeResponse(response, responseDocument);
            } catch (Exception e) {
                logger.error("Failed to write response", e);
                throw new ServletException(e);
            }
            logger.info(String.format("End of synchronization with %s", request.getRemoteAddr()));
        } catch (RuntimeContext.NotInitializedException e) {
            throw new UnavailableException(e.getMessage());
        }
    }

    private static Document readRequest(HttpServletRequest httpRequest) throws Exception {
        if (StringUtils.isEmpty(httpRequest.getCharacterEncoding())) {
            httpRequest.setCharacterEncoding(CharEncoding.UTF_8);
        }
        if (!StringUtils.equals(CONTENT_TYPE, httpRequest.getContentType())) {
            throw new RuntimeException("Invalid content type");
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(httpRequest.getInputStream());
    }

    private static void writeResponse(HttpServletResponse httpResponse, Document responseDocument) throws Exception {
        httpResponse.setContentType(CONTENT_TYPE);
        httpResponse.setCharacterEncoding(responseDocument.getXmlEncoding());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(responseDocument), new StreamResult(httpResponse.getOutputStream()));
    }

    private static PublicKey extractPublicKey(RuntimeContext runtimeContext, Long idOfContragent) throws Exception {
        PublicKey publicKey;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            // Start data model transaction
            persistenceTransaction = persistenceSession.beginTransaction();

            // Find given org
            Contragent contragent = (Contragent) persistenceSession.get(Contragent.class, idOfContragent);
            if (null == contragent) {
                logger.error(String.format("Unknown contragent with IdOfContragent == %s", idOfContragent));
                throw new NullPointerException(
                        String.format("Unknown contragent with IdOfContragent == %s", idOfContragent));
            }

            // Extract PK for org with given IdOfOrg
            publicKey = DigitalSignatureUtils.convertToPublicKey(contragent.getPublicKey());
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return publicKey;
    }

    private static Long findUser(RuntimeContext runtimeContext, String userName, String password) throws Exception {
        Long idOfUser = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            // Start data model transaction
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria userCriteria = persistenceSession.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("userName", userName));
            User user = (User) userCriteria.uniqueResult();
            if (user.hasPassword(password)) {
                idOfUser = user.getIdOfUser();
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idOfUser;
    }

}