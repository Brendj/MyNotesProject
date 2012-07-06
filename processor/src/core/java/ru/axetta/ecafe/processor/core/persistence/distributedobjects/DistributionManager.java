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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
@Scope("prototype")
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
    //private List<DistributedObject> distributedObjects = new LinkedList<DistributedObject>();
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private Map<String, List<DistributedObject>> distributedObjectsListMap = new HashMap<String, List<DistributedObject>>();

    /* Обязательно public метод спринг не может инициализировать */
    public DistributionManager() {
    }

    @PostConstruct
    public void init() {
    }

    /**
     * Создает  элемент <RO> выходного xml документа
     *
     * @param document выходной xml документ
     * @return элемент <RO> выходного xml документа
     */
    public Element toElement(Document document) throws Exception {
        /* Метод нужно переписать так, чтобы он использовал distributedObjectsListMap. CurrentMaxVersions - если без ее исользования обойтись нельзя
        (а так ли это?) нужно заполнять в методе Process.
         */
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        String tagName;
        for(String key: distributedObjectsListMap.keySet()){
            List<DistributedObject> distributedObjects = distributedObjectsListMap.get(key);
            for (int i = 0; i < distributedObjects.size(); i++) {
                DistributedObject distributedObject = distributedObjects.get(i);
                tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).getValue();
                if (!elementMap.containsKey(tagName)) {
                    Element distributedObjectElement = document.createElement(tagName);
                    confirmElement.appendChild(distributedObjectElement);
                    elementMap.put(tagName, distributedObjectElement);
                }
                Element element = document.createElement(distributedObject.getTagName());
                elementMap.get(tagName).appendChild(distributedObject.toConfirmElement(element));
            }
            elementRO.appendChild(confirmElement);
            elementMap.clear();
            distributedObjects.clear();
            for (DistributedObjectsEnum distributedObjectsEnum : DistributedObjectsEnum.values()) {
                String name = distributedObjectsEnum.name();
                List<DistributedObject> distributedObjectList = DAOService.getInstance()
                        .getDistributedObjects(name, currentMaxVersions.get(name), idOfOrg);
                if (!distributedObjectList.isEmpty()) {
                    distributedObjects.addAll(distributedObjectList);
                }
            }

            for (DistributedObject distributedObject : distributedObjects) {
                tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).getValue();
                if (!elementMap.containsKey(tagName)) {
                    Element distributedObjectElement = document.createElement(tagName);
                    elementRO.appendChild(distributedObjectElement);
                    elementMap.put(tagName, distributedObjectElement);
                }
                Element element = document.createElement("O");
                elementMap.get(tagName).appendChild(distributedObject.toElement(element));
            }
        }
        return elementRO;
    }

    @PersistenceContext
    EntityManager entityManager;


    /* метод работы с бд */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void process(List<DistributedObject> distributedObjects, String objectClass) throws Exception {
        /* В методе нужно обрабатывать объекты одного типа - проще передавать список однотипных аргументов через параметр*/
        //List<DistributedObject> distributedObjectList = new LinkedList<DistributedObject>();
        entityManager.find(TechnologicalMap.class,1L);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().newDocument();
            DOVersion doVersion = DAOService.getInstance().updateVersionByDistributedObjects(objectClass);
            long currentMaxVersion = doVersion.getCurrentVersion();
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            //for (DistributedObject distributedObject: distributedObjects){
            for (DistributedObject distributedObject : distributedObjects) {
                try {
                    if (distributedObject.getDeletedState()) {
                        DAOService.getInstance().setDeletedState(distributedObject);
                    } else {
                        if (distributedObject.getTagName().equals("C")) {
                            Long lid = distributedObject.getLocalID();
                            distributedObject = DAOService.getInstance()
                                    .createDistributedObject(distributedObject, currentMaxVersion);
                            distributedObject.setTagName("C");
                            distributedObject.setLocalID(lid);
                        }
                        if (distributedObject.getTagName().equals("M")) {
                            long objectVersion = distributedObject.getGlobalVersion();
                            Long currentVersion = DAOService.getInstance()
                                    .getDistributedObjectVersion(distributedObject);
                            if (objectVersion == currentVersion) {
                                distributedObject = DAOService.getInstance()
                                        .mergeDistributedObject(distributedObject, currentMaxVersion);
                            } else {

                                String stringElement = createStringElement(document, distributedObject);

                                distributedObject = DAOService.getInstance()
                                        .mergeDistributedObject(distributedObject, objectVersion);
                                DOConflict conflict = new DOConflict();
                                conflict.setgVersionCur(currentVersion);
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
                        //distributedObjectList.add(distributedObject);
                        // Почему нужно добавлять в distributedObjectList только те объекты, которые не помечены для удаления?
                    }
                } catch (Exception e) {
                    // Произошла ошибка при обрабоке одного объекта - нужна как то сообщить об этом пользователю
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        //distributedObjects = distributedObjectList; Может быть имеет смысле не менять сам список объектов? Либо удалять из него помеченные для удаления

    }

    /**
     * Берет информацию из элемента <Pr> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     *
     * @param node Элемент <Pr>
     * @throws Exception
     */
    public void build(Node node) throws Exception {
        //distributedObjects.clear();
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            // При обработке в
            currentMaxVersions.put(currentObject.getValue(), Long.parseLong(getAttributeValue(node,"V")));
            // Здесь не стоит лезть в БД. Все доступы к бд должны быть внутри транзакции.
            /*if(node.getFirstChild()!=null){
                DAOService.getInstance().updateVersionByDistributedObjects(currentObject.name());
            }*/
            node = node.getFirstChild();
            while (node != null) {
                DistributedObject distributedObject = createDistributedObject(currentObject);
                distributedObject = distributedObject.build(node);
                distributedObject.setOrgOwner(idOfOrg);
                //distributedObjects.add(distributedObject);

                if (!distributedObjectsListMap.containsKey(currentObject.name())) {
                    distributedObjectsListMap.put(currentObject.name(), new LinkedList<DistributedObject>());
                }
                distributedObjectsListMap.get(currentObject.name()).add(distributedObject);

                node = node.getNextSibling();
            }
        }
    }

    private String createStringElement(Document document, DistributedObject distributedObject)
            throws TransformerException {
        Element element = document.createElement("O");
        element = distributedObject.toElement(element);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(element), new StreamResult(buffer));
        return buffer.toString();
    }

    private DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum) throws Exception {
        /*DistributedObject distributedObject=null;
        switch (distributedObjectsEnum){
            case Product: distributedObject = new Product(); break;
            case TechnologicalMap: distributedObject = new TechnologicalMap(); break;
            case TechnologicalMapProduct: distributedObject = new TechnologicalMapProduct(); break;
        }*/
        String name = "ru.axetta.ecafe.processor.core.persistence.distributedobjects." + distributedObjectsEnum.name();
        Class cl = Class.forName(name);
        return (DistributedObject) cl.newInstance();
    }


    private String getAttributeValue(Node node, String attributeName) {
        return (node.getAttributes().getNamedItem(attributeName) != null ? node.getAttributes()
                .getNamedItem(attributeName).getTextContent() : null);
    }


    /* Getter and Setters */
    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Map<String, List<DistributedObject>> getDistributedObjectsListMap() {
        return distributedObjectsListMap;
    }

}

