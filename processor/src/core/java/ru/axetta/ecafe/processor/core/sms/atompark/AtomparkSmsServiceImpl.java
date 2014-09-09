/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.atompark;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.MessageIdGenerator;
import ru.axetta.ecafe.processor.core.sms.SendResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AtomparkSmsServiceImpl extends ISmsService {

    public AtomparkSmsServiceImpl(Config config) {
        super(config);
    }

    private String sendServiceRequest(String request) throws Exception {
        PostMethod httpMethod = new PostMethod(config.getServiceUrl());
        httpMethod.addParameter("XML", request);
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Sending SMS service request: %s", request));
            }
            HttpClient httpClient = new HttpClient();
            httpClient.getParams().setContentCharset("UTF-8");
            int statusCode = httpClient.executeMethod(httpMethod);
            if (HttpStatus.SC_OK != statusCode) {
                throw new HttpException(String.format("HTTP status is: %d", statusCode));
            }
            String response = httpMethod.getResponseBodyAsString();
            logger.info(String.format("Retrived response from SMS service: %s", response));
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Retrived response from SMS service: %s", response));
            }
            return response;
        } finally {
            httpMethod.releaseConnection();
        }
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param messageId
     * @param sender
     * @param phoneNumber
     * @param text
     * @return
     * @throws Exception
     */
    public SendResponse sendTextMessage(String sender, String phoneNumber, Object textObject)
            throws Exception {
        if(!(textObject instanceof String)) {
            throw new Exception("Text argument must be a string");
        }
        String text = (String) textObject;
        if (StringUtils.isEmpty(sender)) {
            sender = config.getDefaultSender();
        }
        MessageIdGenerator messageIdGenerator = RuntimeContext.getInstance().getMessageIdGenerator();
        String messageId = messageIdGenerator.generate();

        String serviceRequest = buildSendXml(messageId, sender, phoneNumber, text);
        String serviceResponse = sendServiceRequest(serviceRequest);
        return new SendResponse(extractStatusCodeFromSendResponse(serviceResponse), null, messageId);
    }

    /**
     * Warning: has to be threadsafe
     *
     * @param messageId
     * @return
     * @throws Exception
     */
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        String serviceRequest = buildStatusXml(messageId);
        String serviceResponse = sendServiceRequest(serviceRequest);
        Node messageNode = extractMessageNodeFromDeliveryResponse(serviceResponse, messageId);
        if (null == messageNode) {
            throw new IllegalArgumentException(
                    String.format("Message node with given messageId (%s) not found", messageId));
        }
        return buildDeliveryResponse(messageNode);
    }

    private String buildSendXml(String messageId, String sender, String phoneNumber, String text) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element smsElement = document.createElement("SMS");
        document.appendChild(smsElement);

        Element operationsElement = document.createElement("operations");
        smsElement.appendChild(operationsElement);
        Element operationElement = document.createElement("operation");
        operationsElement.appendChild(operationElement);
        operationElement.setTextContent("SEND");

        Element authentificationElement = document.createElement("authentification");
        smsElement.appendChild(authentificationElement);
        Element usernameElement = document.createElement("username");
        authentificationElement.appendChild(usernameElement);
        usernameElement.setTextContent(config.getUserName());
        Element passwordElement = document.createElement("password");
        authentificationElement.appendChild(passwordElement);
        passwordElement.setTextContent(config.getPassword());

        Element messageElement = document.createElement("message");
        smsElement.appendChild(messageElement);
        Element senderElement = document.createElement("sender");
        messageElement.appendChild(senderElement);
        senderElement.setTextContent(sender);
        Element textElement = document.createElement("text");
        messageElement.appendChild(textElement);
        CDATASection cdataSection = document.createCDATASection(text);
        textElement.appendChild(cdataSection);

        Element numbersElement = document.createElement("numbers");
        smsElement.appendChild(numbersElement);
        Element numberElement = document.createElement("number");
        numbersElement.appendChild(numberElement);
        numberElement.setAttribute("messageID", messageId);
        numberElement.setTextContent(phoneNumber);

        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

    private int extractStatusCodeFromSendResponse(String response) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(response)));
        Node responseNode = findFirstChildElement(document, "RESPONSE");
        Node statusNode = findFirstChildElement(responseNode, "status");
        return Integer.parseInt(statusNode.getTextContent());
    }

    private Node extractMessageNodeFromDeliveryResponse(String response, String messageId) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(response)));
        Node responseNode = findFirstChildElement(document, "deliveryreport");
        Node messageNode = findFirstChildElement(responseNode, "message");
        while (null != messageNode) {
            NamedNodeMap namedNodeMap = messageNode.getAttributes();
            Node messageIdAttribute = namedNodeMap.getNamedItem("id");
            if (null != messageIdAttribute) {
                if (StringUtils.equals(messageIdAttribute.getTextContent(), messageId)) {
                    break;
                }
            }
            messageNode = findNextElement(messageNode, "message");
        }
        return messageNode;
    }

    private DeliveryResponse buildDeliveryResponse(Node messageNode) throws Exception {
        Integer statusCode = null;
        Date sentDate = null;
        Date doneDate = null;
        NamedNodeMap namedNodeMap = messageNode.getAttributes();
        String statusText = namedNodeMap.getNamedItem("status").getTextContent();
        for (int i = 0; i != DeliveryResponse.STATUS_TEXT.length; ++i) {
            if (StringUtils.equals(DeliveryResponse.STATUS_TEXT[i], statusText)) {
                statusCode = DeliveryResponse.STATUS_CODE[i];
                break;
            }
        }
        if (null == statusCode) {
            throw new IllegalArgumentException("Unknown delivery status: " + statusText);
        } else if (0 != statusCode) {
            TimeZone utcTimeZone = TimeZone.getTimeZone(config.getServiceTimeZone());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(utcTimeZone);
            Node sentDateNode = namedNodeMap.getNamedItem("sentdate");
            if (null != sentDateNode) {
                sentDate = convertToDate(sentDateNode.getTextContent(), dateFormat);
            }
            Node doneDateNode = namedNodeMap.getNamedItem("donedate");
            if (null != doneDateNode) {
                doneDate = convertToDate(doneDateNode.getTextContent(), dateFormat);
            }
        }
        return new DeliveryResponse(statusCode, sentDate, doneDate);
    }

    private static Date convertToDate(String dateText, DateFormat dateFormat) throws Exception {
        if (StringUtils.startsWith(dateText, "0000")) {
            return null;
        }
        Date date = dateFormat.parse(dateText);
        if (date.before(new Date(0))) {
            return null;
        }
        return date;
    }

    private static Node findFirstChildElement(Node node, String name) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    private static Node findNextElement(Node node, String name) throws Exception {
        Node currNode = node.getNextSibling();
        while (null != currNode) {
            if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    private String buildStatusXml(String messageId) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element smsElement = document.createElement("SMS");
        document.appendChild(smsElement);

        Element operationsElement = document.createElement("operations");
        smsElement.appendChild(operationsElement);
        Element operationElement = document.createElement("operation");
        operationsElement.appendChild(operationElement);
        operationElement.setTextContent("GETSTATUS");

        Element authentificationElement = document.createElement("authentification");
        smsElement.appendChild(authentificationElement);
        Element usernameElement = document.createElement("username");
        authentificationElement.appendChild(usernameElement);
        usernameElement.setTextContent(config.getUserName());
        Element passwordElement = document.createElement("password");
        authentificationElement.appendChild(passwordElement);
        passwordElement.setTextContent(config.getPassword());

        Element statisticsElement = document.createElement("statistics");
        smsElement.appendChild(statisticsElement);
        Element messageidElement = document.createElement("messageid");
        statisticsElement.appendChild(messageidElement);
        messageidElement.setTextContent(messageId);

        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

}
