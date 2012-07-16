/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    //private Long idOfOrg;
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String, Long> currentMaxVersions = new HashMap<String, Long>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    //private List<DistributedObject> distributedObjects = new ArrayList<DistributedObject>();
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private Map<DistributedObjectsEnum, List<DistributedObject>> distributedObjectsListMap = new HashMap<DistributedObjectsEnum, List<DistributedObject>>();

    //private Map<String,String> errorMap = new HashMap<String, String>();
    //private List<ErrorObject> errorObjectList = new ArrayList<ErrorObject>();

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
    public Element toElement(Document document, Long idOfOrg) throws Exception {
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        String tagName;
        List<DistributedObject> distributedObjects = null;
        for (DistributedObjectsEnum key : distributedObjectsListMap.keySet()) {
            distributedObjects = distributedObjectsListMap.get(key);
            for (int i = 0; i < distributedObjects.size(); i++) {
                DistributedObject distributedObject = distributedObjects.get(i);
                tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).getValue();
                if (!elementMap.containsKey(tagName)) {
                    Element distributedObjectElement = document.createElement(tagName);
                    confirmElement.appendChild(distributedObjectElement);
                    elementMap.put(tagName, distributedObjectElement);
                }
                Element element = document.createElement(distributedObject.getTagName());
                if (!(DistributedObjectsEnumComparator.isEmptyOrNull())) {
                    /*int index = DistributedObjectsEnumComparator.getErrorObjectList().indexOf(
                            new ErrorObject(distributedObject.getClass(), distributedObject.getGuid()));*/
                    ErrorObject errorObject = new ErrorObject(distributedObject.getClass(),
                            distributedObject.getGuid());
                    int index = DistributedObjectsEnumComparator.getErrorObject(errorObject);
                    if (index != -1) {
                        element.setAttribute("errorType", DistributedObjectsEnumComparator.getTypeByIndex(index));
                    }
                }
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
        for (int i = 0; i < array.length; i++) {
            String name = array[i].name();
            List<DistributedObject> distributedObjectList = DAOService.getInstance()
                    .getDistributedObjects(name, currentMaxVersions.get(name), idOfOrg);
            if (!distributedObjectList.isEmpty()) {
                distributedObjects.addAll(distributedObjectList);
            }
            List<String> guidList = DAOService.getInstance().getGUIDsInConfirms(name, idOfOrg);
            List<DistributedObject> distributedObjectsConfirm = null;
            if(guidList!=null && !guidList.isEmpty()){
                distributedObjectsConfirm = DAOService.getInstance().findDistributedObjectByInGUID(name,guidList);
            }
            if (distributedObjectsConfirm!=null && !distributedObjectsConfirm.isEmpty()) {
                distributedObjects.addAll(distributedObjectsConfirm);
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

        return elementRO;
    }


    /* метод работы с бд */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //, isolation = Isolation.READ_COMMITTED)  дает ошибку http://stackoverflow.com/questions/5234240/hibernatespringjpaisolation-does-not-work
    public void process(List<DistributedObject> distributedObjects, DistributedObjectsEnum objectClass, Long idOfOrg)
            throws Exception {
        /* В методе нужно обрабатывать объекты одного типа - проще передавать список однотипных аргументов через параметр*/
        try {
            DOVersion doVersion = DAOService.getInstance().updateVersionByDistributedObjects(objectClass.name());
            long currentMaxVersion = doVersion.getCurrentVersion();
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.

            DistributedObjectProcessor distributedObjectProcessor = DistributedObjectProcessor.getInstance();
            for (DistributedObject distributedObject : distributedObjects) {
                distributedObjectProcessor.process(distributedObject, currentMaxVersion, idOfOrg, getSimpleDocument());

            }
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
    public void build(Node node, Long idOfOrg) throws Exception {
        //distributedObjects.clear();
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
                // При обработке в
                String attrValue = getAttributeValue(node, "V");
                currentMaxVersions.put(currentObject.getValue(), Long.parseLong(getAttributeValue(node, "V")));
                // Здесь не стоит лезть в БД. Все доступы к бд должны быть внутри транзакции.
                /*if(node.getFirstChild()!=null){
                    DAOService.getInstance().updateVersionByDistributedObjects(currentObject.name());
                }*/
                node = node.getFirstChild();
                while (node != null) {
                    if (Node.ELEMENT_NODE == node.getNodeType()) {
                        DistributedObject distributedObject = createDistributedObject(currentObject);
                        distributedObject = distributedObject.build(node);
                        distributedObject.setOrgOwner(idOfOrg);

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

    public Map<DistributedObjectsEnum, List<DistributedObject>> getDistributedObjectsListMap() {
        return distributedObjectsListMap;
    }

}

