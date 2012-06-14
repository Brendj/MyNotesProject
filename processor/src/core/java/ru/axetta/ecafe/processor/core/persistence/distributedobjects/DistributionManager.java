/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.06.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */

public class DistributionManager {

    private Logger logger = LoggerFactory.getLogger(DistributionManager.class);

    private List<DistributedObjectItem> distributedObjectItems = new LinkedList<DistributedObjectItem>();
    private HashMap<String, Element> stringElementHashMap = new HashMap<String, Element>();
    private List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();

    private Element confirmNode;

    public void addDistributedObjectItem(DistributedObjectItem distributedObjectItem){
        distributedObjectItems.add(distributedObjectItem);
    }

    private void generateNodesByList(List list, Element appendElement, Document document){
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        for (Object object: list){
            String action = "";
            DistributedObject distributedObject = null;
            if(object instanceof DistributedObject){
                distributedObject = (DistributedObject) object;
                action = "O";
            }
            if(object instanceof DistributedObjectItem) {
                distributedObject = ((DistributedObjectItem) object).getDistributedObject();
                action = ((DistributedObjectItem) object).getAction();
            }
            if(distributedObject == null) continue;
            if(!elementMap.containsKey(distributedObject.getNodeName())){
                Element distributedObjectElement = document.createElement(distributedObject.getNodeName());
                appendElement.appendChild(distributedObjectElement);
                elementMap.put(distributedObject.getNodeName(),distributedObjectElement);
            }
            elementMap.get(distributedObject.getNodeName()).appendChild(distributedObject.toElement(document,action));
        }

    }

    public Element getElements(Document document) throws JAXBException {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        generateNodesByList(distributedObjectItems, confirmElement,document);
        elementRO.appendChild(confirmElement);
        List<ProductGuide> productGuideList = DAOService.getInstance().getProductGuide();
        distributedObjectList.addAll(productGuideList);
        generateNodesByList(distributedObjectList, elementRO,document);
        return elementRO;
    }

    public void parseXML(Node node) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            if(node.getNodeName().equals("Pr")){
                /* в случае успеха при парсинге добавляем в список */
                node = node.getFirstChild();
                node = node.getNextSibling();
                while (node != null){
                    ProductGuide productGuide = new ProductGuide();
                    String action = productGuide.parseXML(node);
                    if(action != null){
                       addDistributedObjectItem(new DistributedObjectItem(action, productGuide));
                        /* занесение в бд */
                    }
                    node = node.getNextSibling();
                    if(node !=null) node = node.getNextSibling();
                }
            }
        }
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

    private static Node findFirstChildTextNode(Node node) throws Exception {
        Node currNode = node.getFirstChild();
        while (null != currNode) {
            currNode = currNode.getNextSibling();
            if (Node.TEXT_NODE == currNode.getNodeType()) {
                return currNode;
            }
        }
        return null;
    }

    public static class DistributedObjectItem{

        private String action;
        private DistributedObject distributedObject;

        private DistributedObjectItem(String action, DistributedObject distributedObject) {
            this.action = action;
            this.distributedObject = distributedObject;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public DistributedObject getDistributedObject() {
            return distributedObject;
        }

        public void setDistributedObject(DistributedObject distributedObject) {
            this.distributedObject = distributedObject;
        }
    }
}

