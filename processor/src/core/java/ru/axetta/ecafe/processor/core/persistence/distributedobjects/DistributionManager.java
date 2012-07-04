/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

@Component
@Scope("singleton")
public class DistributionManager {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(DistributionManager.class);

    private Long idOfOrg;
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String,Long> currentMaxVersions=new HashMap<String, Long>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    private List<DistributedObject> distributedObjects = new LinkedList<DistributedObject>();

    public DistributionManager() {}
    @PostConstruct
    public void init(){}
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
            /*if(distributedObject.getDeletedState()){
                DAOService.getInstance().setDeletedState(distributedObject);
            } else {
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
                        String stringElement = createStringElement(document, distributedObject);

                        distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, objectVersion);
                        DOConflict conflict = new DOConflict();
                        conflict.setgVersionCur(currentMaxVersion);
                        conflict.setIdOfOrg(idOfOrg);
                        conflict.setgVersionInc(objectVersion);
                        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());

                        conflict.setValueCur(createStringElement(document, distributedObject));

                        conflict.setValueInc(stringElement);
                        conflict.setCreateConflictDate(new Date());
                        DAOService.getInstance().createConflict(conflict);
                    }
                    distributedObject.setTagName("M");
                    distributedObjects.set(i,distributedObject);
                }
            }*/
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
        distributedObjects.clear();
        for (DistributedObjectsEnum distributedObjectsEnum: DistributedObjectsEnum.values()){
            String name = distributedObjectsEnum.name();
            List<DistributedObject> distributedObjectList = DAOService.getInstance().getDistributedObjects(name,
                    currentMaxVersions.get(name), idOfOrg);
            if(!distributedObjectList.isEmpty()) distributedObjects.addAll(distributedObjectList);
        }

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

    @PersistenceContext
    EntityManager entityManager;
    /* метод работы с бд */
    @Transactional
    public void process() throws Exception{
        List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().newDocument();
        for (DistributedObject distributedObject: distributedObjects){
            if(distributedObject.getDeletedState()){
                DAOService.getInstance().setDeletedState(distributedObject);
            } else {
                if(distributedObject.getTagName().equals("C")){
                    Long lid = distributedObject.getLocalID();
                    distributedObject = DAOService.getInstance().createDistributedObject(distributedObject);
                    distributedObject.setTagName("C");
                    distributedObject.setLocalID(lid);
                }
                if(distributedObject.getTagName().equals("M")){
                    long objectVersion = distributedObject.getGlobalVersion();
                    Long currentMaxVersion = DAOService.getInstance().getDistributedObjectVersion(distributedObject);
                    if(objectVersion == currentMaxVersion){
                        distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, currentMaxVersion+1);
                    } else {

                        String stringElement = createStringElement(document, distributedObject);

                        distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, objectVersion);
                        DOConflict conflict = new DOConflict();
                        conflict.setgVersionCur(currentMaxVersion);
                        conflict.setIdOfOrg(idOfOrg);
                        conflict.setgVersionInc(objectVersion);
                        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());

                        conflict.setValueCur(createStringElement(document, distributedObject));

                        conflict.setValueInc(stringElement);
                        conflict.setCreateConflictDate(new Date());
                        DAOService.getInstance().createConflict(conflict);
                    }
                    distributedObject.setTagName("M");
                }
                distributedObjectList.add(distributedObject);
            }
        }
        distributedObjects = distributedObjectList;
    }

    /**
     * Берет информацию из элемента <Pr> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     * @param node  Элемент <Pr>
     * @throws Exception
     */
    public void build(Node node) throws Exception{
        distributedObjects.clear();
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            currentMaxVersions.put(currentObject.getValue(), Long.parseLong(getAttributeValue(node,"V")));
            if(node.getFirstChild()!=null){
                DAOService.getInstance().updateVersionByDistributedObjects(currentObject.name());
            }
            node = node.getFirstChild();
            while (node!=null){
                DistributedObject distributedObject = createDistributedObject(currentObject);
                distributedObject = distributedObject.build(node);
                distributedObject.setOrgOwner(idOfOrg);
                distributedObjects.add(distributedObject);
                node = node.getNextSibling();
            }
        }
    }

    private String createStringElement(Document document, DistributedObject distributedObject)
            throws TransformerException {
        Element element =  document.createElement("O");
        element = distributedObject.toElement(element);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(element),new StreamResult(buffer));
        return buffer.toString();
    }


    private DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum) throws Exception{
        /*DistributedObject distributedObject=null;
        switch (distributedObjectsEnum){
            case Product: distributedObject = new Product(); break;
            case TechnologicalMap: distributedObject = new TechnologicalMap(); break;
            case TechnologicalMapProduct: distributedObject = new TechnologicalMapProduct(); break;
        }*/
        String name = "ru.axetta.ecafe.processor.core.persistence.distributedobjects."+distributedObjectsEnum.name();
        Class cl =  Class.forName(name);
        return (DistributedObject) cl.newInstance();
    }

    private String getAttributeValue(Node node, String attributeName){
        return (node.getAttributes().getNamedItem(attributeName)!=null?node.getAttributes().getNamedItem(
                attributeName).getTextContent():null);
    }

    /* Getter and Setters */
    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

}

