/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 10.11.2010
 * Time: 0:01:58
 * To change this template use File | Settings | File Templates.
 */
public class XMLUtils {

    public static String nodeToString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }

    public static Node importXmlFragmentToDocument(Document doc, String xml)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document d = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        return doc.importNode(d.getDocumentElement(), true);
    }

    public static String getStringAttributeValue(Node node, String attributeName,Integer length) throws Exception{
        if(getAttributeValue(node, attributeName)==null) return null;
        String result = getAttributeValue(node, attributeName);
        if(result.length()>length) return result.substring(0, length);
        return result;
    }

    public static String getAttributeValue(Node node, String attributeName){
        if(node.getAttributes().getNamedItem(attributeName)==null) return null;
        return node.getAttributes().getNamedItem(attributeName).getTextContent().trim();
    }

    public static Node findFirstChildElement(Node node, String name) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    public static Node findFirstChildTextNode(Node node) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.TEXT_NODE == currNode.getNodeType()) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    public static int getIntValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        return Integer.parseInt(namedNodeMap.getNamedItem(name).getTextContent());
    }

    public static long getLongValue(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node n = namedNodeMap.getNamedItem(name);
        return Long.parseLong(n.getTextContent());
    }

    public static Long getLongValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            node = namedNodeMap.getNamedItem(name.toUpperCase());
            if (node == null) {
                return null;
            }
        }
        return Long.parseLong(node.getTextContent());
    }

    public static String getStringValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            return null;
        }
        return node.getTextContent();
    }

    public static String getStringValueNullSafe(NamedNodeMap namedNodeMap, String name, int length) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            return null;
        }
        String result = node.getTextContent();
        if(result.length()>length) return result.substring(0, length);
        return result;
    }


}
