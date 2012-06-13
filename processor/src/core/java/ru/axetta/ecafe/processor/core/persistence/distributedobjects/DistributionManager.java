/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.06.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class DistributionManager {

    private List<DistributedObject> distributedObjects = new LinkedList<DistributedObject>();

    public void addDistributedObject(DistributedObject distributedObject){
        distributedObjects.add(distributedObject);
    }

    public void sortByType(){
        Collections.sort(distributedObjects);
    }

    private Element getProductElement(Document document){
        return document.createElement("RO");
    }

    public Element getElements(Document document){
        Element element = getProductElement(document);
        Element productElements = null;
        for (DistributedObject distributedObject: distributedObjects){
            if(distributedObject instanceof ProductGuide){
                if(productElements == null){
                    productElements = document.createElement("Pr");
                    element.appendChild(productElements);
                }
                ProductGuide productGuide = (ProductGuide) distributedObject;
                productElements.appendChild(productGuide.toElement(document));
            }
        }
        return element;
    }

    public void parseXML(Node node) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            if(node.getNodeName().equals("Pr")){
                ProductGuide productGuide = new ProductGuide();
                /* в случае успеха при парсинге добавляем в список */
                node = node.getFirstChild();
                node = node.getNextSibling();
                while (node != null){
                   if(productGuide.parseXML(node)){
                       addDistributedObject(productGuide);
                   }
                   node = node.getNextSibling();
                }
            }
        }
    }

    private Node findFirstChildTextNode(Node node) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            if (Node.TEXT_NODE == currNode.getNodeType()) {
                return currNode;
            }
            currNode = currNode.getNextSibling();
        }
        return null;
    }
}
