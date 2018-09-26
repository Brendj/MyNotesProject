/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/*
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
public class MealServiceHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger logger = LoggerFactory.getLogger(MealServiceHandler.class);
    private final String fileName;
    private final String LOG_PATH_PREFIX;
    private final String DEFAULT_LOG_PATH_PREFIX = "/home/jbosser/processor/MEAL_logs/";
    private final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("_dd_MM_yyyy");
    private final String endPointAddress;

    public MealServiceHandler(String endPointAddress){
        super();
        String endPointAddressFolder = parseAddress(endPointAddress);
        this.endPointAddress = endPointAddress;
        this.LOG_PATH_PREFIX = getLogPath() + endPointAddressFolder + "/";
        String today = DATA_FORMAT.format(new Date());
        this.fileName = LOG_PATH_PREFIX + endPointAddressFolder + today + ".log";
    }


    private String parseAddress(String endPointAddress) {
        return endPointAddress
                .replaceAll("http://|https://", "")
                .replaceAll("\\.|/", "_");
    }

    private String getLogPath() {
        return RuntimeContext.getInstance()
                .getConfigProperties().getProperty("ecafe.processor.mealmanager.logpath", DEFAULT_LOG_PATH_PREFIX);
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        try {
            Boolean outboundProperty = (Boolean)
                    smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outboundProperty.booleanValue()) {
                final SOAPPart soapPart = smc.getMessage().getSOAPPart();
                final Document source_doc = soapPart.getEnvelope().getOwnerDocument();
                String sss = toString(source_doc)
                        .replaceAll(" xmlns=\"http://service.petersburgedu.ru/webservice/meal\" xmlns:ns2=\"http://service.petersburgedu.ru/webservice/meal/wsdl\" xmlns:ns3=\"service.petersburgedu.ru/webservice/meal/wsdl\"", " xmlns=\"http://service.petersburgedu.ru/webservice/meal/wsdl\"")
                        .replaceAll("ns3:", "")
                        .replaceAll("ns2:", "");
                InputStream in = new ByteArrayInputStream(sss.getBytes("UTF-8"));
                Document signed_doc = newDocumentFromInputStream(in);
                DOMSource domSource = new DOMSource(signed_doc);
                soapPart.setContent(domSource);
            }

        } catch (Exception e) {
            logger.error("Error in filter chain", e);
        }
        if (!MealManager.isOn()) {
            logToFile(smc);
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToFile(smc);
        return true;
    }

    private Document newDocumentFromInputStream(InputStream in) {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            ret = builder.parse(new InputSource(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    /*
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context
     * to see if this is an outgoing or incoming message.
     * Write a brief message to the print stream and
     * output the message. The writeTo() method can throw
     * SOAPException or IOException
     */
    private void logToFile(SOAPMessageContext smc) {
        try {
            Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            StringBuilder message = new StringBuilder();
            SimpleDateFormat eventTimeFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");

            final SOAPPart soapPart = smc.getMessage().getSOAPPart();
            final Document doc = soapPart.getEnvelope().getOwnerDocument();

            message.append(eventTimeFormat.format(new Date()));
            message.append(" Endpoint address is: ");
            message.append(endPointAddress);

            if (outboundProperty) {
                message.append("\nOutbound message:\n");
            } else {
                message.append("\nInbound message:\n");
            }

            message.append(toString(doc));
            message.append("\n");

            synchronized (this) {
                checkAndCreateFolder(LOG_PATH_PREFIX);
                FileWriter fw = new FileWriter(fileName, true);
                fw.write(message.toString());
                fw.flush();
                fw.close();
            }
        } catch (Exception e) {
            logger.error("Exception in handler: " + e);
        }
    }

    private void checkAndCreateFolder(String pathToFolder) throws Exception{
        try {
            File folder = new File(pathToFolder);
            if (!folder.exists()) {
                if(!folder.mkdirs()){
                    throw new RuntimeException();
                }
            }
        }catch (Exception e){
            logger.error(String.format("Can't create directory %s :", pathToFolder), e);
            throw e;
        }
    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}