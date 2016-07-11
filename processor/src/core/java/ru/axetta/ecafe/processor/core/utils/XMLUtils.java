/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static Document inputStreamToXML(InputStream is, DocumentBuilderFactory dbf) {
        DocumentBuilder documentBuilder;
        Document document;
        try {
            documentBuilder = dbf.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(is));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return document;
    }

    public static String getAttributeValue(Node node, String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        return attribute == null ? null : attribute.getTextContent().trim();
    }

    public static String getStringAttributeValue(Node node, String attributeName, int length) {
        String result = getAttributeValue(node, attributeName);
        if (result == null)
            return null;
        if (result.length() > length)
            return result.substring(0, length);
        return result;
    }

    public static Long getLongAttributeValue(Node node, String attributeName) {
        String value = getAttributeValue(node, attributeName);
        return value == null || value.isEmpty() ? null : Long.valueOf(value);
    }

    public static Integer getIntegerAttributeValue(Node node, String attributeName) {
        String value = getAttributeValue(node, attributeName);
        return value == null || value.isEmpty() ? null : Integer.valueOf(value);
    }

    public static int getIntegerValueZeroSafe(Node node, String attributeName) {
        String value = getAttributeValue(node, attributeName);
        return value == null || value.isEmpty() ? 0 : Integer.valueOf(value);
    }

    public static String getStringValueNullSafe(Node node, String name) throws Exception {
        String value = getAttributeValue(node, name);
        if (null == node) {
            return null;
        }
        return node.getTextContent();
    }

    public static Date getDateAttributeValue(Node node, String attributeName) throws Exception {
        String attributeValue = getAttributeValue(node, attributeName);
        try {
            return CalendarUtils.parseDate(attributeValue);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Date getDateTimeAttributeValue(Node node, String attributeName)  throws Exception{
        String attributeValue = getAttributeValue(node, attributeName);
        try {
            return CalendarUtils.parseFullDateTimeWithLocalTimeZone(attributeValue);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Float getFloatAttributeValue(Node node, String attributeName) throws Exception {
        String attributeValue = getAttributeValue(node, attributeName);
        if (attributeValue == null || attributeValue.isEmpty())
            return null;
        String replacedString = attributeValue.replaceAll(",", ".");
        try {
            return Float.parseFloat(replacedString);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Boolean getBooleanAttributeValue(Node node, String attributeName) throws Exception {
        String attributeValue = getAttributeValue(node, attributeName);
        if (attributeValue == null || attributeValue.isEmpty())
            return null;
        return attributeValue.equals("1");
    }

    public static Character getCharacterAttributeValue(Node node, String attributeName) throws Exception {
        String attributeValue = getAttributeValue(node, attributeName);
        if (attributeValue == null || attributeValue.isEmpty())
            return null;
        try {
            return attributeValue.charAt(0);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    public static List<Node> findNodesWithNameNotEqualsTo(Node parentNode, String name) {
        List<Node> result = new ArrayList<Node>();
        Node child = parentNode.getFirstChild();
        while (child != null) {
            if (Node.ELEMENT_NODE == child.getNodeType() && !name.equals(child.getNodeName()))
                result.add(child);
            child = child.getNextSibling();
        }
        return result;
    }

    public static List<Node> findNodesWithNameEqualsTo(Node parentNode, String name) {
        List<Node> result = new ArrayList<Node>();
        Node child = parentNode.getFirstChild();
        while (child != null) {
            if (Node.ELEMENT_NODE == child.getNodeType() && name.equalsIgnoreCase(child.getNodeName()))
                result.add(child);
            child = child.getNextSibling();
        }
        return result;
    }

    public static Node findFirstChildElement(Node node, String name) {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.ELEMENT_NODE == currNode.getNodeType() && currNode.getNodeName().equals(name)) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }

    public static Node findFirstChildTextNode(Node node) {
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

    public static Integer getIntegerValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        Node node = namedNodeMap.getNamedItem(name);
        if (null == node) {
            node = namedNodeMap.getNamedItem(name.toUpperCase());
            if (node == null) {
                return null;
            }
        }
        return Integer.parseInt(node.getTextContent());
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

    public static Date getDateValueNullSafe(NamedNodeMap namedNodeMap, String name) throws Exception {
        String stringValue = getStringValueNullSafe(namedNodeMap, name);
        if (stringValue != null) {
            try {
                return CalendarUtils.parseDate(stringValue);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return null;
    }

    public static void setAttributeIfNotNull(Element element, String attrName, Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                element.setAttribute(attrName, (Boolean) value ? "1" : "0");
            } else {
                element.setAttribute(attrName, value.toString());
            }
        }
    }
}
