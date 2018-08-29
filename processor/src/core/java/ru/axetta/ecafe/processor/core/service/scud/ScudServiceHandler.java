/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Set;

public class ScudServiceHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger logger = LoggerFactory.getLogger(ScudServiceHandler.class);
    private static PrintStream out = System.out;

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
                        .replaceAll(" xmlns:ns2=\"http://service.petersburgedu.ru/webservice/scud\" xmlns:ns3=\"http://service.petersburgedu.ru/webservice/scud/wsdl\"", "")
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
        //if (!MealManager.isOn()) {
            logToSystemOut(smc);
        //}
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToSystemOut(smc);
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

    public void close(MessageContext messageContext) {
    }

    private void logToSystemOut(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean)
                smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);


        if (outboundProperty.booleanValue()) {
            out.println("\nOutbound message:");
        } else {
            out.println("\nInbound message:");
        }

        try {
            final SOAPPart soapPart = smc.getMessage().getSOAPPart();
            final Document doc = soapPart.getEnvelope().getOwnerDocument();
            System.out.println(toString(doc));
            out.println("");
        } catch (Exception e) {
            out.println("Exception in handler: " + e);
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
