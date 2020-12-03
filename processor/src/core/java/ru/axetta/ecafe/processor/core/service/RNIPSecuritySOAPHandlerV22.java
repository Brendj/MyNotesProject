/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.AttributeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RNIPSecuritySOAPHandlerV22 extends RNIPSecuritySOAPHandlerV21 {
    private static final Logger logger = LoggerFactory.getLogger(RNIPSecuritySOAPHandlerV22.class);

    public RNIPSecuritySOAPHandlerV22(String containerAlias, String containerPassword, IRNIPMessageToLog messageLogger) {
        super(containerAlias, containerPassword, messageLogger);
    }

    @Override
    protected void additionalTaskOnOutboundMessage(Document doc) {
        try {
            Element requestElement = (Element)doc.getElementsByTagName("soap:Body").item(0).getFirstChild();
            replaceNamespaceVersion(requestElement);
            NodeList nodeList = requestElement.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    node = replaceNamespaceVersion(doc, node);
                }
            }
        } catch (Exception e) {
            logger.error("Error in replace RNIP namespaces in outbound message: ", e);
        }
    }

    @Override
    protected void additionalTaskOnInboundMessage(Document doc) {
        try {
            Element requestElement = (Element)doc.getElementsByTagName("soap:Body").item(0).getFirstChild();
            replaceNamespaceVersionInverse(requestElement);
        } catch (Exception e) {
            logger.error("Error in replace RNIP namespaces in inbound message: ", e);
        }
    }

    /*
    Замена namespace для нового формата
     */
    private void replaceNamespaceVersion(Element nodeItem) {
        AttributeMap map = (AttributeMap) nodeItem.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            AttrNSImpl node = (AttrNSImpl) map.item(i);
            if (node.getLocalName().startsWith("ns")) {
                node.setValue(node.getValue().replace("2.1.1", "2.2.0"));
            }
        }
    }

    private Node replaceNamespaceVersion(Document doc, Node nodeItem) {
        return doc.renameNode(nodeItem, nodeItem.getNamespaceURI().replace("2.1.1", "2.2.0"), nodeItem.getNodeName());
    }

    private void replaceNamespaceVersionInverse(Element nodeItem) {
        AttributeMap map = (AttributeMap) nodeItem.getAttributes();
        for (int i = 0; i < map.getLength(); i++) {
            AttrNSImpl node = (AttrNSImpl) map.item(i);
            if (node.getLocalName().startsWith("ns")) {
                node.setValue(node.getValue().replace("2.2.0", "2.1.1"));
            }
        }
    }
}
