/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    private static class DistributedObjectItem{

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

    private List<DistributedObjectItem> distributedObjectItems = new LinkedList<DistributedObjectItem>();
    private HashMap<String, Element> stringElementHashMap = new HashMap<String, Element>();

    private Element getProductElement(Document document){
        return document.createElement("RO");
    }

    public void addDistributedObjectItem(DistributedObjectItem distributedObjectItem){
        distributedObjectItems.add(distributedObjectItem);
    }

    public Element getElements(Document document) throws JAXBException {
        Element elementRO = document.createElement("RO");
        elementRO.appendChild(getConfirmElement(document));
        /* Запрос БД вернуть все distributedObject и зположить в список тега <0/>*/
        return elementRO;
    }

    private Element getConfirmElement(Document document) {
        Element confirmElement = document.createElement("Confirm");
        for (DistributedObjectItem distributedObjectItem: distributedObjectItems){
            DistributedObject distributedObject = distributedObjectItem.getDistributedObject();
            if(!stringElementHashMap.containsKey(distributedObject.getNodeName())){
                Element distributedObjectElement = document.createElement(distributedObject.getNodeName());
                confirmElement.appendChild(distributedObjectElement);
                stringElementHashMap.put(distributedObject.getNodeName(),distributedObjectElement);
            }
            stringElementHashMap.get(distributedObject.getNodeName()).appendChild(distributedObject.toElement(document,
                    distributedObjectItem.getAction()));
        }
        return confirmElement;
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
                       addDistributedObjectItem(new DistributedObjectItem(action,productGuide));
                        /* занесение в бд */
                    }
                    node = node.getNextSibling();
                    if(node !=null) node = node.getNextSibling();
                }
            }
        }
    }
}
