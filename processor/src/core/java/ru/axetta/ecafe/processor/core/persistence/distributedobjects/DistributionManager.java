/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.DateType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.06.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */

public class DistributionManager {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(DistributionManager.class);
    /**
     * Список успешно выполненных действий с распределенными объектами в виде  пар(действие, объект)
     */
    private List<DistributedObjectItem> distributedObjectItems = new LinkedList<DistributedObjectItem>();
    //private HashMap<String, Element> stringElementHashMap = new HashMap<String, Element>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    private List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();
    private Document confirmDocument = null;
   // private Element confirmNode;


    public void addDistributedObjectItem(DistributedObjectItem distributedObjectItem){
        distributedObjectItems.add(distributedObjectItem);
    }

    /**
     *  Оперирует с одним из двух видов списков: либо список выполненных действий либо список глобальных
     *  объектов. В первом случае appendElement=<Confirm>. Во втором случае appendElement=<RO>
     *   В обоих случаях appendElement заполняется элементами, сгенерированными по спискам
     * @param list  либо список выполненных действий либо список глобальных
     *  объектов
     * @param appendElement   элемент, заполняемый другими элементами (Элемент <Confirm> либо элемент  <RO>)
     * @param document  выходной xml документ
     */
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

    /**
     * Создает  элемент <RO> выходного xml документа
     * @param document   выходной xml документ
     * @return  элемент <RO> выходного xml документа
     * @throws JAXBException
     */
    public Element getElements(Document document) throws JAXBException {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        generateNodesByList(distributedObjectItems, confirmElement,document);
        elementRO.appendChild(confirmElement);
        //elementRO.appendChild(confirmNode);
        //elementRO.appendChild(confirmDocument.importNode(confirmNode, true));
        List<ProductGuide> productGuideList = DAOService.getInstance().getProductGuide();
        distributedObjectList.addAll(productGuideList);
        generateNodesByList(distributedObjectList, elementRO,document);
        return elementRO;
    }

    private Document getConfirmDocument() throws ParserConfigurationException {
        if(confirmDocument == null){
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //confirmDocument = documentBuilder.newDocument();
            confirmDocument = new DocumentImpl();
        }
        return confirmDocument;
    }

    /**
     * Берет информацию из элемента <Pr> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     * @param node  Элемент <Pr>
     * @throws Exception
     */
    public void parseXML(Node node) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            if(node.getNodeName().equals("Pr")){
                // в случае успеха при парсинге добавляем в список
                node = node.getFirstChild();
                node = node.getNextSibling();
                while (node != null){
                    ProductGuide productGuide = new ProductGuide();
                    String action = productGuide.parseXML(node);
                    //productGuide.setDeleteTime(new Date());
                    //productGuide.setCreateTime(new Date());
                    //productGuide.setEditTime(new Date());
                    if(action != null){
                       if(productGuide.getAttributeValue(node,"D") == null){
                           if(node.getNodeName().equals("C")){
                               //productGuide = DAOService.getInstance().createProductGuide(productGuide);
                               productGuide = DAOService.getInstance().mergeProductGuide(productGuide);
                           }
                           if(node.getNodeName().equals("M")){
                               Long version = DAOService.getInstance().getProductGuideVersion(productGuide.getGlobalId());
                               if(version != productGuide.getVersion()){
                                   ProductGuide newProductGuide = DAOService.getInstance().mergeProductGuide(productGuide);
                                   DOConflict conflict = new DOConflict();
                                   conflict.setgVersionCur(productGuide.getVersion());
                                   conflict.setgVersionInc(version);
                                   conflict.setDistributedObjectClassName("ProductGuide");
                                   conflict.setValueCur(newProductGuide.toString());
                                   conflict.setValueInc(productGuide.toString());
                                   conflict.setCreateConflictDate(new Date());
                                   DAOService.getInstance().createConflict(conflict);
                                   //DAOService.getInstance().setProductGuideVersion(productGuide.getGlobalId(),version);
                               } else {
                                   productGuide = DAOService.getInstance().mergeProductGuide(productGuide);
                               }
                               //productGuide = DAOService.getInstance().mergeProductGuide(productGuide);
                           }

                       } else {
                           DAOService.getInstance().setDeleteStatusProductGuide(productGuide.getGlobalId(),productGuide.getAttributeValue(node,"D").equals("1"));
                       }
                       addDistributedObjectItem(new DistributedObjectItem(action, productGuide));
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

    /**
     * Пара {(Распределенный объект), (действие, которое необходимо выполнить с объектом) }
     */
    public static class DistributedObjectItem{

        /**
         *   действие, которое необходимо выполнить(или выполнено) с объектом
         */
        private String action;
        /**
         *   Распределенный объект
         */
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

