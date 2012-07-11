/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
        /* Метод нужно переписать так, чтобы он использовал distributedObjectsListMap. CurrentMaxVersions - если без ее исользования обойтись нельзя
        (а так ли это?) нужно заполнять в методе Process.
         */
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

        DistributedObjectsEnumComparator distributedObjectsEnumComparator = new DistributedObjectsEnumComparator();
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

    @Transactional
    private void createConflict(DistributedObject distributedObject, Long idOfOrg) throws Exception {
        DOConflict conflict = new DOConflict();
        conflict.setValueInc(createStringElement(getSimpleDocument(), distributedObject));
        conflict.setgVersionInc(distributedObject.getGlobalVersion());
        conflict.setIdOfOrg(idOfOrg);
        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
        TypedQuery<DistributedObject> query = entityManager.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        DistributedObject currDistributedObject = query.getSingleResult();
        conflict.setgVersionCur(currDistributedObject.getGlobalVersion());
        conflict.setValueCur(createStringElement(getSimpleDocument(), currDistributedObject));
        conflict.setCreateConflictDate(new Date());
        entityManager.persist(conflict);
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
            DistributionManager dm = RuntimeContext.getAppContext().getBean(DistributionManager.class);
            /*
            DOProcessorFactory doProcessorFactory = DOProcessorFactory.getInstance();
            DOProcessor doProcessor = doProcessorFactory.getProcessor(objectClass);
             */
            for (DistributedObject distributedObject : distributedObjects) {
                dm.processOneDistributedObject(distributedObject, currentMaxVersion, idOfOrg);
                /*
                doProcessor.process(distributionObject, currentMaxVersion, idOfOrg);
                 */
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        //distributedObjects = distributedObjectList; Может быть имеет смысле не менять сам список объектов? Либо удалять из него помеченные для удаления

    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOneDistributedObject(DistributedObject distributedObject, Long idOfOrg, long currentMaxVersion) {
        try {
            if (distributedObject.getDeletedState()) {
                if (updateDeleteState(distributedObject)) {
                    throw new Exception(
                            "Error by set Delete State by " + distributedObject.getClass().getSimpleName() + " guid="
                                    + distributedObject.getGuid());
                }
            } else {
                if (distributedObject.getTagName().equals("C")) {
                    distributedObject = createDistributedObject(distributedObject, currentMaxVersion);
                    distributedObject.setTagName("C");
                }
                if (distributedObject.getTagName().equals("M")) {
                    long objectVersion = distributedObject.getGlobalVersion();
                    long currentVersion = DAOService.getInstance().getDistributedObjectVersion(distributedObject);
                    if (objectVersion != currentVersion) {
                        createConflict(distributedObject, idOfOrg);
                    }
                    distributedObject = DAOService.getInstance()
                            .mergeDistributedObject(distributedObject, objectVersion);
                    distributedObject.setTagName("M");
                }
                //distributedObjectList.add(distributedObject);
                // Почему нужно добавлять в distributedObjectList только те объекты, которые не помечены для удаления?
            }
        } catch (Exception e) {
            // Произошла ошибка при обрабоке одного объекта - нужна как то сообщить об этом пользователю
            ErrorObject errorObject = new ErrorObject();
            errorObject.setClazz(distributedObject.getClass());
            errorObject.setGuid(distributedObject.getGuid());
            errorObject.setMessage(e.getMessage());
            if (e instanceof DistributedObjectException) {
                errorObject.setType(((DistributedObjectException) e).getType());
            }
            DistributedObjectsEnumComparator.getErrorObjectList().add(errorObject);
            logger.error(errorObject.toString(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            // При обработке в
            currentMaxVersions.put(currentObject.getValue(), Long.parseLong(getAttributeValue(node, "V")));
            // Здесь не стоит лезть в БД. Все доступы к бд должны быть внутри транзакции.
            /*if(node.getFirstChild()!=null){
                DAOService.getInstance().updateVersionByDistributedObjects(currentObject.name());
            }*/
            node = node.getFirstChild();
            while (node != null) {
                DistributedObject distributedObject = createDistributedObject(currentObject);
                distributedObject = distributedObject.build(node);
                distributedObject.setOrgOwner(idOfOrg);

                if (!distributedObjectsListMap.containsKey(currentObject)) {
                    distributedObjectsListMap.put(currentObject, new ArrayList<DistributedObject>());
                }
                distributedObjectsListMap.get(currentObject).add(distributedObject);

                node = node.getNextSibling();
            }
        }
    }

    /* парсинг блока <Confirm/> не обработанных объектов*/
    public void buildConfirm(Node node, Long idOfOrg) throws Exception {

    }

    private Document getSimpleDocument() throws Exception {
        if (document == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            document = factory.newDocumentBuilder().newDocument();
        }
        return document;
    }

    @Transactional
    private long getGlobalIDByGUID(DistributedObject distributedObject) {
        //   TypedQuery<Long> query=entityManager.createQuery("select id from "+distributedObject.getClass().getSimpleName()+" where this.quid='"+distributedObject.getGuid()+"'",Long.class);
        TypedQuery<Long> query = null;
        try {
            query = entityManager.createQuery(
                    "select id from " + distributedObject.getClass().getSimpleName() + " where guid='"
                            + distributedObject.getGuid() + "'", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            return -1;
        }
        //   return query.getSingleResult();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private DistributedObject createDistributedObject(DistributedObject distributedObject, long currentVersion)
            throws Exception {
        try {
            long id = getGlobalIDByGUID(distributedObject);
            if (id > 0) {
                throw new DistributedObjectException(1);
            }
            distributedObject.setCreatedDate(new Date());
            // Версия должна быть одна на всех, и получена заранее.
            /*TypedQuery<DOVersion> query = em.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName",DOVersion.class);
            query.setParameter("distributedObjectClassName",distributedObject.getClass().getSimpleName().toUpperCase());
            List<DOVersion> doVersionList = query.getResultList();
            if(doVersionList.size()==0) {
                distributedObject.setGlobalVersion(0L);
            } else {
                distributedObject.setGlobalVersion(doVersionList.get(0).getCurrentVersion()-1);
            }*/
            distributedObject.setGlobalVersion(currentVersion);
            return entityManager.merge(distributedObject);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }


    @Transactional
    private boolean updateDeleteState(DistributedObject distributedObject) {
        StringBuilder stringQuery = new StringBuilder("update ");
        stringQuery.append(distributedObject.getClass().getSimpleName());
        stringQuery.append(" set deletedState=:deletedState where guid='");
        stringQuery.append(distributedObject.getGuid());
        stringQuery.append("'");
        Query q = entityManager.createQuery(stringQuery.toString());
        q.setParameter("deletedState", true);
        return (q.executeUpdate() != 0);
        //if (q.executeUpdate()!=0) throw new Exception("Error by set Delete State by "+distributedObject.getClass().getSimpleName()+" guid="+distributedObject.getGuid());
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
    /* public List<ErrorObject> getErrorObjectList() {
        return errorObjectList;
    }*/

    public Map<DistributedObjectsEnum, List<DistributedObject>> getDistributedObjectsListMap() {
        return distributedObjectsListMap;
    }

}

