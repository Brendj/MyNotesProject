/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
     * Список глобальных объектов на базе процессинга
     */
    private List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();
    private List<ConfirmObject> confirmObjectList = new LinkedList<ConfirmObject>();
    private Long idOfOrg;

    /**
     * Создает  элемент <RO> выходного xml документа
     * @param document   выходной xml документ
     * @return  элемент <RO> выходного xml документа
     */
    public Element toElement(Document document) {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        for (ConfirmObject confirmObject: confirmObjectList){
            if(!elementMap.containsKey(confirmObject.getNodeName())){
                Element distributedObjectElement = document.createElement(confirmObject.getNodeName());
                confirmElement.appendChild(distributedObjectElement);
                elementMap.put(confirmObject.getNodeName(),distributedObjectElement);
            }
            Element element =  document.createElement(confirmObject.getAction());
            elementMap.get(confirmObject.getNodeName()).appendChild(confirmObject.toElement(element));
        }
        elementRO.appendChild(confirmElement);
        List<ProductGuide> productGuideList = DAOService.getInstance().getProductGuide();
        distributedObjectList.addAll(productGuideList);
        elementMap.clear();
        for (DistributedObject distributedObject: distributedObjectList){
            if(!elementMap.containsKey(distributedObject.getNodeName())){
                Element distributedObjectElement = document.createElement(distributedObject.getNodeName());
                elementRO.appendChild(distributedObjectElement);
                elementMap.put(distributedObject.getNodeName(),distributedObjectElement);
            }
            Element element =  document.createElement("O");
            elementMap.get(distributedObject.getNodeName()).appendChild(distributedObject.toElement(element));
        }
        return elementRO;
    }

    private DistributedObject createDistributedObject(String nodeName){
        if(nodeName.equals("Pr")) return new ProductGuide();
        return null;
    }



    private String getAttributeValue(Node node, String attributeName){
        return (node.getAttributes().getNamedItem(attributeName)!=null?node.getAttributes().getNamedItem(
                attributeName).getTextContent():null);
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
            String objectName = node.getNodeName();
            node = node.getFirstChild();
            node = node.getNextSibling();
            /**/
            while (node != null){
                DistributedObject distributedObject = createDistributedObject(objectName);
                distributedObject = distributedObject.build(node);
                distributedObject.setIdOfOrg(idOfOrg);
                ConfirmObject confirmObject = new ConfirmObject();
                if(getAttributeValue(node,"D")==null){
                    if(node.getNodeName().equals("C")){
                        distributedObject = DAOService.getInstance().createDistributedObject(distributedObject);
                        confirmObject.setNodeName(objectName);
                        confirmObject.setAction(node.getNodeName());
                        confirmObject.setLocalID(Long.parseLong(getAttributeValue(node,"LID")));
                        confirmObject.setGlobalId(distributedObject.getGlobalId());
                        confirmObject.setGlobalVersion(distributedObject.getGlobalVersion());
                        confirmObjectList.add(confirmObject);
                    }
                    if(node.getNodeName().equals("M")){
                        Long version =Long.parseLong(getAttributeValue(node,"V"));
                        String toStringDistributedObject = distributedObject.toString();
                        if( DAOService.getInstance().getDistributedObjectVersion(distributedObject).equals(version)) {
                            distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject,null);
                        } else {
                            distributedObject.setGlobalVersion(version);
                            distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, version);
                            DOConflict conflict = new DOConflict();
                            conflict.setgVersionCur(distributedObject.getGlobalVersion());
                            conflict.setIdOfOrg(idOfOrg);
                            conflict.setgVersionInc(version);
                            conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
                            conflict.setValueCur(distributedObject.toString());
                            conflict.setValueInc(toStringDistributedObject);
                            conflict.setCreateConflictDate(new Date());
                            DAOService.getInstance().createConflict(conflict);
                        }
                        confirmObject.setNodeName(objectName);
                        confirmObject.setAction(node.getNodeName());
                        confirmObject.setGlobalId(Long.parseLong(getAttributeValue(node,"GID")));
                        confirmObject.setGlobalVersion(distributedObject.getGlobalVersion());
                        confirmObjectList.add(confirmObject);
                    }
                } else {
                    distributedObject = DAOService.getInstance().setStatusDistributedObject(distributedObject,getAttributeValue(node,"D").equals("1"));
                    confirmObject.setNodeName(objectName);
                    confirmObject.setAction(node.getNodeName());
                    confirmObject.setGlobalId(Long.parseLong(getAttributeValue(node,"GID")));
                    confirmObject.setGlobalVersion(distributedObject.getGlobalVersion());
                    confirmObject.setStatus(getAttributeValue(node,"D").equals("1"));
                }
                node = node.getNextSibling();
                if(node !=null) node = node.getNextSibling();
            }
        }
    }

    /* Getter and Setters */
    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

}

