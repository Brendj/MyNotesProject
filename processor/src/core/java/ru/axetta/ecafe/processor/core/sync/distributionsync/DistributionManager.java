/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.distributionsync;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String, Long> currentMaxVersions = new HashMap<String, Long>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private Map<DistributedObjectsEnum, List<DistributedObject>> distributedObjectsListMap = new HashMap<DistributedObjectsEnum, List<DistributedObject>>();

    @Autowired
    private ErrorObjectData errorObjectData;

    public ErrorObjectData getErrorObjectData() {
        return errorObjectData;
    }

    public void setErrorObjectData(ErrorObjectData errorObjectData) {
        this.errorObjectData = errorObjectData;
    }

    private Document document;

    @PersistenceContext
    private EntityManager entityManager;

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
    public Element toElement(Document document, Long idOfOrg, DateFormat dateFormat,DateFormat timeFormat) throws Exception {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        String tagName;
        List<DistributedObject> distributedObjects = null;
        for (DistributedObjectsEnum key : distributedObjectsListMap.keySet()) {
            distributedObjects = distributedObjectsListMap.get(key);
            for (DistributedObject distributedObject : distributedObjects) {
                tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).name();
                if (!elementMap.containsKey(tagName)) {
                    Element distributedObjectElement = document.createElement(tagName);
                    confirmElement.appendChild(distributedObjectElement);
                    elementMap.put(tagName, distributedObjectElement);
                }
                Element element = document.createElement(distributedObject.getTagName());
                //if (!(DistributedObjectsEnumComparator.isEmptyOrNull())) {
                if (!(errorObjectData.isEmptyOrNull())) {
                    ErrorObject errorObject = new ErrorObject(distributedObject.getClass(),
                            distributedObject.getGuid());
                    //int index = DistributedObjectsEnumComparator.getErrorObject(errorObject);
                    int index = errorObjectData.getErrorObject(errorObject);
                    if (index != -1) {
                        element.setAttribute("errorType", errorObjectData.getTypeByIndex(index));
                        //element.setAttribute("errorType", DistributedObjectsEnumComparator.getTypeByIndex(index));
                    }
                }
                Long version = DAOService.getInstance().getDOVersionByGUID(distributedObject);
                distributedObject.setGlobalVersion(version);
                elementMap.get(tagName).appendChild(distributedObject.toConfirmElement(element));
            }
            elementRO.appendChild(confirmElement);
            elementMap.clear();
            distributedObjects.clear();
        }

        // Раскоментировать при не обходимости отсортированного вывода к клиенту
        //DistributedObjectsEnumComparator distributedObjectsEnumComparator = new DistributedObjectsEnumComparator();
        DistributedObjectsEnum[] array = DistributedObjectsEnum.values();
        // Arrays.sort(array,distributedObjectsEnumComparator);

        distributedObjects = new ArrayList<DistributedObject>();
        for (DistributedObjectsEnum anArray : array) {
            String name = anArray.name();
            List<DistributedObject> distributedObjectList = new ArrayList<DistributedObject>(0);
            try {
                distributedObjectList = DAOService.getInstance()
                        .getDistributedObjects(anArray.getValue(), currentMaxVersions.get(name), idOfOrg);
            } catch (Exception e) {
                if (e instanceof DistributedObjectException) {
                    Element element = document.createElement(anArray.name());
                    element.setAttribute("errorType", String.valueOf(((DistributedObjectException) e).getType()));
                    elementRO.appendChild(element);
                }
                continue;
            }
            if (!(distributedObjectList == null || distributedObjectList.isEmpty())) {
                distributedObjects.addAll(distributedObjectList);
                for (DistributedObject distributedObject : distributedObjectList) {
                    DOConfirm confirm = new DOConfirm();
                    confirm.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
                    confirm.setGuid(distributedObject.getGuid());
                    confirm.setOrgOwner(idOfOrg);
                    DAOService.getInstance().persistEntity(confirm);
                }
            }

            List<String> guidList = DAOService.getInstance().getGUIDsInConfirms(name, idOfOrg);
            List<DistributedObject> distributedObjectsConfirm = null;
            if (guidList != null && !guidList.isEmpty()) {
                distributedObjectsConfirm = DAOService.getInstance().findDistributedObjectByInGUID(name, guidList);
            }
            if (distributedObjectsConfirm != null && !distributedObjectsConfirm.isEmpty()) {
                for (DistributedObject distributedObject : distributedObjectsConfirm) {
                    if (!distributedObjectList.contains(distributedObject)) {
                        distributedObjects.add(distributedObject);
                    }
                }
            }
        }

        for (DistributedObject distributedObject : distributedObjects) {
            tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).name();
            if (!elementMap.containsKey(tagName)) {
                Element distributedObjectElement = document.createElement(tagName);
                elementRO.appendChild(distributedObjectElement);
                elementMap.put(tagName, distributedObjectElement);
            }
            Element element = document.createElement("O");
            distributedObject.setDateFormat(dateFormat);
            distributedObject.setTimeFormat(timeFormat);
            elementMap.get(tagName).appendChild(distributedObject.toElement(element));
        }

        /* очистим список ошибок */
        //DistributedObjectsEnumComparator.setErrorObjectList(new ArrayList<ErrorObject>(0));

        return elementRO;
    }


    /* метод работы с бд */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //, isolation = Isolation.READ_COMMITTED)  дает ошибку http://stackoverflow.com/questions/5234240/hibernatespringjpaisolation-does-not-work
    public void process(List<DistributedObject> distributedObjects, DistributedObjectsEnum objectClass, Long idOfOrg)
            throws Exception {
        /* В методе нужно обрабатывать объекты одного типа - проще передавать список однотипных аргументов через параметр*/
        try {
            Long currentMaxVersion = DAOService.getInstance().updateVersionByDistributedObjects(objectClass.name());
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            DistributedObjectProcessor distributedObjectProcessor = DistributedObjectProcessor.getInstance();
            distributedObjectProcessor.setErrorObjectData(errorObjectData);
            for (DistributedObject distributedObject : distributedObjects) {
                distributedObjectProcessor.process(distributedObject, currentMaxVersion, idOfOrg, getSimpleDocument());
            }
            errorObjectData = distributedObjectProcessor.getErrorObjectData();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Transactional
    public void clearConfirmTable(List<DOConfirm> confirmList){
        for (DOConfirm confirm: confirmList){
            TypedQuery<DOConfirm> query = entityManager.createQuery("from DOConfirm where distributedObjectClassName=:distributedObjectClassName and guid=:guid and orgOwner=:orgOwner",DOConfirm.class);
            query.setParameter("distributedObjectClassName",confirm.getDistributedObjectClassName());
            query.setParameter("guid",confirm.getGuid());
            query.setParameter("orgOwner",confirm.getOrgOwner());
            List<DOConfirm> confirms = query.getResultList();
            if(confirms !=null && !confirms.isEmpty()){
                for (DOConfirm c: confirms){
                    DAOService.getInstance().deleteEntity(c);
                }
            }
        }
    }

    /**
     * Берет информацию из элемента <Pr> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     *
     * @param node Элемент <Pr>
     * @throws Exception
     */
    public void build(Node node, Long idOfOrg, DateFormat dateFormat,DateFormat timeFormat) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            if(node.getNodeName().equals("Confirm")){
                Node childNode = node.getFirstChild();
                while (childNode != null) {
                    buildConfirm(childNode, idOfOrg);
                    childNode = childNode.getNextSibling();
                }
            } else {
                DistributedObjectsEnum currentObject = null;
                currentObject = DistributedObjectsEnum.parse(node.getNodeName());
                currentMaxVersions.put(currentObject.name(), Long.parseLong(getAttributeValue(node, "V")));
                node = node.getFirstChild();
                while (node != null) {
                    if (Node.ELEMENT_NODE == node.getNodeType()) {
                        DistributedObject distributedObject = createDistributedObject(currentObject);
                        distributedObject = distributedObject.build(node);
                        distributedObject.setDateFormat(dateFormat);
                        distributedObject.setTimeFormat(timeFormat);
                        if (!distributedObjectsListMap.containsKey(currentObject)) {
                            distributedObjectsListMap.put(currentObject, new ArrayList<DistributedObject>());
                        }
                        distributedObjectsListMap.get(currentObject).add(distributedObject);
                    }
                    node = node.getNextSibling();
                }
            }
        }
    }

    private List<DOConfirm> confirmDistributedObject = new ArrayList<DOConfirm>();

    public List<DOConfirm> getConfirmDistributedObject() {
        return confirmDistributedObject;
    }

    public void buildConfirm(Node node, Long idOfOrg) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            DOConfirm confirm = new DOConfirm();
            confirm.setDistributedObjectClassName(currentObject.name());
            confirm.setGuid(node.getAttributes().getNamedItem("Guid").getTextContent());
            confirm.setOrgOwner(idOfOrg);
            confirmDistributedObject.add(confirm);
        }
    }

    private Document getSimpleDocument() throws Exception {
        if (document == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            document = factory.newDocumentBuilder().newDocument();
        }
        return document;
    }


    private DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum) throws Exception {
        Class cl = distributedObjectsEnum.getValue();
        return (DistributedObject) cl.newInstance();
    }

    private String getAttributeValue(Node node, String attributeName) {
        return (node.getAttributes().getNamedItem(attributeName) != null ? node.getAttributes()
                .getNamedItem(attributeName).getTextContent() : null);
    }

    public Map<DistributedObjectsEnum, List<DistributedObject>> getDistributedObjectsListMap() {
        return distributedObjectsListMap;
    }

}

