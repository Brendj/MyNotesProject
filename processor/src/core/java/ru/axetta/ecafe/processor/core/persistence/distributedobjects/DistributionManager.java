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
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
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
    private Long idOfOrg;
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String,Long>currentMaxVersions=new HashMap<String, Long>();

    private List<DistributedObject> distributedObjects = new LinkedList<DistributedObject>();

    public DistributionManager(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }


    /**
     * Создает  элемент <RO> выходного xml документа
     * @param document   выходной xml документ
     * @return  элемент <RO> выходного xml документа
     */
    public Element toElement(Document document) throws Exception {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        String tagName;
        for(int i=0; i<distributedObjects.size(); i++){
            DistributedObject distributedObject = distributedObjects.get(i);
            if(distributedObject.getTagName().equals("C")){
                Long lid = distributedObject.getLocalID();
                distributedObject = DAOService.getInstance().createDistributedObject(distributedObject);
                distributedObject.setTagName("C");
                distributedObject.setLocalID(lid);
                distributedObjects.set(i,distributedObject);
            }
            if(distributedObjects.get(i).getTagName().equals("M")){
                long objectVersion = distributedObject.getGlobalVersion();
                Long currentMaxVersion = DAOService.getInstance().getDistributedObjectVersion(distributedObject);
                if(objectVersion == currentMaxVersion){
                    distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, currentMaxVersion+1);
                } else {
                    Element element =  document.createElement("O");
                    element = distributedObject.toElement(element);

                    TransformerFactory transFactory = TransformerFactory.newInstance();
                    Transformer transformer = transFactory.newTransformer();
                    StringWriter buffer = new StringWriter();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource(element),new StreamResult(buffer));
                    String stringElement = buffer.toString();

                    distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, objectVersion);
                    DOConflict conflict = new DOConflict();
                    conflict.setgVersionCur(currentMaxVersion);
                    conflict.setIdOfOrg(idOfOrg);
                    conflict.setgVersionInc(objectVersion);
                    conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());

                    element =  document.createElement("O");
                    element = distributedObject.toElement(element);
                    transFactory = TransformerFactory.newInstance();
                    transformer = transFactory.newTransformer();
                    buffer = new StringWriter();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource(element),new StreamResult(buffer));
                    conflict.setValueCur(buffer.toString());

                    conflict.setValueInc(stringElement);
                    conflict.setCreateConflictDate(new Date());
                    DAOService.getInstance().createConflict(conflict);
                }
                distributedObject.setTagName("M");
                distributedObjects.set(i,distributedObject);
            }
            tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).getValue();
            if(!elementMap.containsKey(tagName)){
                Element distributedObjectElement = document.createElement(tagName);
                confirmElement.appendChild(distributedObjectElement);
                elementMap.put(tagName,distributedObjectElement);
            }
            Element element =  document.createElement(distributedObject.getTagName());
            elementMap.get(tagName).appendChild(distributedObject.toConfirmElement(element));
        }
        elementRO.appendChild(confirmElement);
        elementMap.clear();
        for (DistributedObject distributedObject: distributedObjects){
            tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).getValue();
            if(!elementMap.containsKey(tagName)){
                Element distributedObjectElement = document.createElement(tagName);
                elementRO.appendChild(distributedObjectElement);
                elementMap.put(tagName,distributedObjectElement);
            }
            Element element =  document.createElement("O");
            elementMap.get(tagName).appendChild(distributedObject.toElement(element));
        }
        return  elementRO;
    }

    /**
     * Берет информацию из элемента <Pr> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     * @param node  Элемент <Pr>
     * @throws Exception
     */
    public void build(Node node) throws Exception{
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            currentMaxVersions.put(currentObject.getValue(), Long.parseLong(getAttributeValue(node,"V")));
            node = node.getFirstChild();
            node = node.getNextSibling();
            while (node!=null){
                DistributedObject distributedObject = createDistributedObject(currentObject);
                distributedObject = distributedObject.build(node);
                distributedObject.setOrgOwner(idOfOrg);
                distributedObjects.add(distributedObject);
                node = node.getNextSibling();
                if(node !=null) node = node.getNextSibling();
            }
        }
    }

    private DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum){
        return DistributionFactory.createDistributedObject(distributedObjectsEnum);
    }

    private String getAttributeValue(Node node, String attributeName){
        return (node.getAttributes().getNamedItem(attributeName)!=null?node.getAttributes().getNamedItem(
                attributeName).getTextContent():null);
    }


    /* public void parseXML(Node node) throws Exception {
      if (Node.ELEMENT_NODE == node.getNodeType()) {
          String objectName = node.getNodeName();
          currentMaxVersions.put(objectName,Long.parseLong(getAttributeValue(node,"V")));
          node = node.getFirstChild();
          node = node.getNextSibling();
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
  }  */

}

