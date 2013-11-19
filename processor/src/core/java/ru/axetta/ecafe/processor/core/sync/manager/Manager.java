/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOGroupsFactory;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOSyncClass;
import ru.axetta.ecafe.processor.core.sync.doGroups.IDOGroup;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.12
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */

public class Manager {
    /**
     * Логгер
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String, Long> currentMaxVersions = new HashMap<String, Long>();
    private HashMap<String, Integer> currentLimits = new HashMap<String, Integer>();
    private HashMap<String, String> currentLastGuids = new HashMap<String, String>();

    private SortedMap<DOSyncClass, List<DistributedObject>> resultDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private SortedMap<DOSyncClass, List<DistributedObject>> incomeDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    private List<DOConfirm> doConfirms = new ArrayList<DOConfirm>();
    private Long idOfOrg;
    private SyncHistory syncHistory;
    private final String[] doGroupNames;

    private Document conflictDocument;

    public void setSyncHistory(SyncHistory syncHistory) {
        this.syncHistory = syncHistory;
    }

    private DOSyncService doService;

    public Manager(Long idOfOrg, String[] doGroupNames) {
        this.idOfOrg = idOfOrg;
        this.doGroupNames = doGroupNames;
        //this.syncHistory = syncHistory;
        this.doService = (DOSyncService) RuntimeContext.getAppContext().getBean("doSyncService");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.conflictDocument = factory.newDocumentBuilder().newDocument();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Берет информацию из элемента <RO> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     *
     * @param roNode Элемент <RO>
     * @throws Exception
     */
    public void buildRO(Node roNode) throws Exception {
        // Получаем имена групп РО, которые будем обрабатывать.
        //String[] doGroupNames = StringUtils.split(XMLUtils.getAttributeValue(roNode, "DO_GROUPS"), ';');

        // Если группы явно не указаны, то пока берем все.
        Node confirmNode = XMLUtils.findFirstChildElement(roNode, "Confirm");
        if (confirmNode != null)
            // Обработка секции <Confirm>
            buildConfirmNode(confirmNode);
        // Получаем секции РО, которые будем обрабатывать.
        List<Node> doNodeList = XMLUtils.findNodesWithNameNotEqualsTo(roNode, "Confirm");
        for (String groupName : doGroupNames) {
            IDOGroup doGroup = DOGroupsFactory.getGroup(groupName);
            buildOneGroup(doGroup, doNodeList);
        }
    }

    private void buildOneGroup(IDOGroup doGroup, List<Node> doNodeList) {
        Iterator<Node> iter = doNodeList.iterator();
        while (iter.hasNext()) {
            Node doNode = iter.next();
            DOSyncClass doSyncClass = doGroup.getDOSyncClass(doNode.getNodeName().trim());
            if (doSyncClass != null) {
                // Обработка перечня экземпляров РО одного определенного класса.
                buildDONode(doNode, doSyncClass);
                iter.remove();
            }
        }
    }

    private void buildDONode(Node doNode, DOSyncClass doSyncClass) {
        String name = doNode.getNodeName().trim();
        // http://www.slf4j.org/faq.html#logging_performance
        LOGGER.debug("RO parse '{}' node XML section", name);
        currentMaxVersions.put(doSyncClass.getDoClass().getSimpleName(), XMLUtils.getLongAttributeValue(doNode, "V"));
        currentLimits.put(doSyncClass.getDoClass().getSimpleName(), XMLUtils.getIntegerValueZeroSafe(doNode, "Limit"));
        currentLastGuids.put(doSyncClass.getDoClass().getSimpleName(), XMLUtils.getAttributeValue(doNode, "LastGuid"));
        incomeDOMap.put(doSyncClass, new ArrayList<DistributedObject>());
        doNode = doNode.getFirstChild();
        while (doNode != null) {
            if (Node.ELEMENT_NODE == doNode.getNodeType()) {
                DistributedObject distributedObject = null;
                try {
                    distributedObject = doSyncClass.getDoClass().newInstance();
                    distributedObject.setIdOfSyncOrg(idOfOrg);
                    distributedObject = distributedObject.build(doNode);
                } catch (Exception e) {
                    if (distributedObject != null) {
                        if (e instanceof DistributedObjectException){
                            distributedObject.setDistributedObjectException((DistributedObjectException) e);
                            LOGGER.error(distributedObject.toString(), e);
                        }
                        else{
                            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
                            LOGGER.error(distributedObject.toString(), e);
                        }
                    }
                }
                List<DistributedObject> doList = incomeDOMap.get(doSyncClass);
                doList.add(distributedObject);
            }
            doNode = doNode.getNextSibling();
        }
        LOGGER.debug("RO end parse '{}' node XML section", name);
    }

    public Element toElement(Document document) throws Exception {
        LOGGER.debug("RO section begin generate XML node");
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        List<DistributedObject> distributedObjects;
        /* generate confirm element */
        LOGGER.debug("Generate confirm element");
        for (SortedMap.Entry<DOSyncClass, List<DistributedObject>> entry : incomeDOMap.entrySet()) {
            distributedObjects = entry.getValue();
            String classTagName = entry.getKey().getDoClass().getSimpleName();
            Element doClassElement = document.createElement(classTagName);
            confirmElement.appendChild(doClassElement);
            for (DistributedObject distributedObject : distributedObjects) {
                Element element = document.createElement(distributedObject.getTagName());
                doClassElement.appendChild(distributedObject.toConfirmElement(element));
            }
            distributedObjects.clear();
        }
        elementRO.appendChild(confirmElement);
        /* generate result objects */
        LOGGER.debug("Generate result objects.");
        for (Map.Entry<DOSyncClass, List<DistributedObject>> entry : resultDOMap.entrySet()) {
            distributedObjects = entry.getValue();
            String classTagName = entry.getKey().getDoClass().getSimpleName();
            Element doClassElement = document.createElement(classTagName);
            elementRO.appendChild(doClassElement);
            if(distributedObjects!=null && !distributedObjects.isEmpty()){
                for (DistributedObject distributedObject : distributedObjects) {
                    Element element = document.createElement("O");
                    doClassElement.appendChild(distributedObject.toElement(element));
                }
                distributedObjects.clear();
            }
        }
        LOGGER.debug("Complete manager generate XML node.");
        return elementRO;
    }

    public void process(SessionFactory sessionFactory) {
        LOGGER.debug("RO begin process section");
        SortedMap<DOSyncClass, List<DistributedObject>> currentDOListMap = new TreeMap<DOSyncClass, List<DistributedObject>>();

        doService.deleteDOConfirms(doConfirms, 0, null);

        for (DOSyncClass doSyncClass : incomeDOMap.keySet()) {
            // TODO: что делать если список пуст
            LOGGER.debug("init generateResponseResult");
            final Class<? extends DistributedObject> doClass = doSyncClass.getDoClass();
            final Integer currentLimit = currentLimits.get(doClass.getSimpleName());
            final String currentLastGuid = currentLastGuids.get(doClass.getSimpleName());
            List<DistributedObject> currentResultDOList = generateResponseResult(sessionFactory, doSyncClass);
            LOGGER.debug("end generateResponseResult");
            resultDOMap.put(doSyncClass, currentResultDOList);
            LOGGER.debug("init processDistributedObjectsList");
            List<DistributedObject> distributedObjectsList = processDistributedObjectsList(sessionFactory, doSyncClass);
            LOGGER.debug("end processDistributedObjectsList");
            currentDOListMap.put(doSyncClass, distributedObjectsList);
            addConfirms(currentResultDOList, sessionFactory);
            List<DistributedObject> currentConfirmDOList = null;
            try {
                LOGGER.debug("init findConfirmedDO");
                //int limit = 0;
                //if(currentLimit!=null && currentLimit>0 && currentLimit>currentResultDOList.size()){
                //    limit = currentLimit-currentResultDOList.size();
                //}
                //Session persistenceSession = null;
                //Transaction persistenceTransaction = null;
                //try {
                //    persistenceSession = sessionFactory.openSession();
                //    persistenceTransaction = persistenceSession.beginTransaction();
                //
                //    DistributedObject refDistributedObject = doSyncClass.getDoClass().newInstance();
                //
                //    DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DOConfirm.class);
                //    detachedCriteria.add(Restrictions.eq("distributedObjectClassName",doClass.getSimpleName()));
                //    detachedCriteria.add(Restrictions.eq("orgOwner",idOfOrg));
                //    detachedCriteria.setProjection(Property.forName("guid"));
                //
                //    Criteria distributedObjectCriteria = persistenceSession.createCriteria(doClass);
                //    distributedObjectCriteria.add(Property.forName("guid").in(detachedCriteria));
                //    refDistributedObject.createProjections(distributedObjectCriteria, limit, currentLastGuid);
                //    currentConfirmDOList = distributedObjectCriteria.list();
                //    persistenceTransaction.commit();
                //    persistenceTransaction = null;
                //} catch (Exception e){
                //    // TODO: записать в журнал ошибок
                //    persistenceSession = getSession(sessionFactory, e);
                //    LOGGER.error("Error addConfirms: "+ e.getMessage());
                //} finally {
                //    HibernateUtils.rollback(persistenceTransaction, LOGGER);
                //    HibernateUtils.close(persistenceSession, LOGGER);
                //}

                currentConfirmDOList = doService.findConfirmedDO(doClass, idOfOrg, currentLimit, currentLastGuid);
                LOGGER.debug("end findConfirmedDO");
                if (!currentConfirmDOList.isEmpty()) {
                    for (DistributedObject distributedObject : currentConfirmDOList) {
                        if (!currentResultDOList.contains(distributedObject)) {
                            currentResultDOList.add(distributedObject);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Generate Confirm object Exception", e);
            }

        }
        incomeDOMap = currentDOListMap;
        LOGGER.debug("RO end process section");
    }



    private void buildConfirmNode(Node node) throws Exception {
        LOGGER.debug("RO parse Confirm XML section");
        Node childNode = node.getFirstChild();
        while (childNode != null) {
            if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                DOConfirm confirm = new DOConfirm();
                confirm.setDistributedObjectClassName(node.getNodeName().trim());
                confirm.setGuid(childNode.getAttributes().getNamedItem("Guid").getTextContent());
                confirm.setOrgOwner(idOfOrg);
                doConfirms.add(confirm);
            }
            childNode = childNode.getNextSibling();
        }
        LOGGER.debug("RO end parse Confirm XML section");
    }

    // TODO: неплохо отправить его в отдельный поток
    private void addConfirms(List<DistributedObject> confirmDistributedObjectList, SessionFactory sessionFactory) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            persistenceSession.setCacheMode(CacheMode.IGNORE);
            if(confirmDistributedObjectList!=null && !confirmDistributedObjectList.isEmpty()){
                for (DistributedObject distributedObject : confirmDistributedObjectList) {
                    final DOConfirm confirm = new DOConfirm(distributedObject.getClass().getSimpleName(), distributedObject.getGuid(), idOfOrg);
                    Example example = Example.create(confirm);
                    Criteria criteria = persistenceSession.createCriteria(DOConfirm.class);
                    criteria.add(example);
                    criteria.setCacheable(false);
                    criteria.setReadOnly(true);
                    if(criteria.uniqueResult()==null){
                        persistenceSession.save(confirm);
                    }
                    if(confirmDistributedObjectList.indexOf(distributedObject) % 100 ==0){
                        persistenceSession.flush();
                        persistenceSession.clear();
                    }
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e){
            // TODO: записать в журнал ошибок
            persistenceSession = getSession(sessionFactory, e);
            LOGGER.error("Error addConfirms: "+ e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }

    }

    private List<DistributedObject> processDistributedObjectsList(SessionFactory sessionFactory, DOSyncClass doSyncClass) {
        List<DistributedObject> distributedObjects = incomeDOMap.get(doSyncClass);
        List<DistributedObject> distributedObjectList = new ArrayList<DistributedObject>();
        if (!distributedObjects.isEmpty()) {
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            Long currentMaxVersion = doService.updateDOVersion(doSyncClass.getDoClass());
            for (DistributedObject distributedObject : distributedObjects) {
                LOGGER.debug("Process: {}", distributedObject.toString());
                DistributedObject currentDistributedObject = processCurrentObject(sessionFactory, distributedObject, currentMaxVersion);
                distributedObjectList.add(currentDistributedObject);
            }
            /* generate result list */
            List<DistributedObject> currentResultDOList = resultDOMap.get(doSyncClass);
            /* уберем все объекты которые есть в конфирме */
            if (!(currentResultDOList == null || currentResultDOList.isEmpty()))
                currentResultDOList.removeAll(distributedObjectList);
        }
        return distributedObjectList;
    }

    private DistributedObject processCurrentObject(SessionFactory sessionFactory, DistributedObject distributedObject, Long currentMaxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            if (distributedObject.getDistributedObjectException() == null) {
                persistenceSession = sessionFactory.openSession();
                if (!(distributedObject.getDeletedState() == null || distributedObject.getDeletedState())) {
                    distributedObject.preProcess(persistenceSession, idOfOrg);
                    distributedObject = processDistributedObject(persistenceSession, distributedObject, currentMaxVersion);
                } else {
                    String tagName = distributedObject.getTagName();
                    distributedObject = updateDeleteState(persistenceSession, distributedObject, currentMaxVersion);
                    distributedObject.setTagName(tagName);
                }
            } else {
                persistenceTransaction = persistenceSession.beginTransaction();
                Org org = null;
                org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
                DistributedObjectException de = distributedObject.getDistributedObjectException();
                SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory, "Error processCurrentObject: " + de.getMessage());
                persistenceSession.save(syncHistoryException);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            }
        } catch (DistributedObjectException e) {
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            // TODO: записать в журнал ошибок
            getSession(sessionFactory, e);
            distributedObject.setDistributedObjectException(e);
            LOGGER.error(e.getMessage()+":"+distributedObject.toString());
        } catch (Exception e) {
            // TODO: записать в журнал ошибок
            getSession(sessionFactory, e);
            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
            LOGGER.error(distributedObject.toString(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
        return distributedObject;
    }

    private List<DistributedObject> generateResponseResult(SessionFactory sessionFactory, DOSyncClass doSyncClass){
        List<DistributedObject> result = null;
        Session persistenceSession = null;
        //Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            //persistenceTransaction = persistenceSession.beginTransaction();

            DistributedObject refDistributedObject = doSyncClass.getDoClass().newInstance();
            final String simpleName = doSyncClass.getDoClass().getSimpleName();
            Long currentMaxVersion = currentMaxVersions.get(simpleName);
            int currentLimit = currentLimits.get(simpleName);
            String currentLastGuid = currentLastGuids.get(simpleName);
            result = refDistributedObject.process(persistenceSession, idOfOrg, currentMaxVersion, currentLimit, currentLastGuid);
            //persistenceTransaction.commit();
            //persistenceTransaction = null;
        } catch (Exception e){
            // TODO: записать в журнал ошибок
            persistenceSession = getSession(sessionFactory, e);
            LOGGER.error("Error generateResponseResult: ", e);
        } finally {
            //HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
        if(result==null){
            return new ArrayList<DistributedObject>();
        } else {
            return result;
        }
    }

    private Session getSession(SessionFactory sessionFactory, Exception e) {
        Session persistenceSession = sessionFactory.openSession();
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory, "Error generateResponseResult: " + e.getMessage());
            persistenceSession.save(syncHistoryException);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e1) {
            LOGGER.error("createSyncHistory exception: ",e1);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
        return persistenceSession;
    }

    private DistributedObject updateDeleteState(Session persistenceSession, DistributedObject distributedObject, Long currentMaxVersion) throws Exception {
        //DistributedObject currentDO = doService.findByGuid(distributedObject.getClass(), distributedObject.getGuid());
        Criteria currentDOCriteria = persistenceSession.createCriteria(distributedObject.getClass());
        currentDOCriteria.add(Restrictions.eq("guid", distributedObject.getGuid()));
        distributedObject.createProjections(currentDOCriteria, 0, "");
        currentDOCriteria.setMaxResults(1);
        DistributedObject currentDO = (DistributedObject) currentDOCriteria.uniqueResult();
        if (currentDO == null) {
            throw new DistributedObjectException(
                    distributedObject.getClass().getSimpleName() + " NOT_FOUND_VALUE : " + distributedObject.getGuid());
        }
        return doService.update(currentDO);
    }

    private DistributedObject processDistributedObject(Session persistenceSession, DistributedObject distributedObject, Long currentMaxVersion) throws Exception {
        DistributedObject currentDO = doService.findByGuid(distributedObject.getClass(), distributedObject.getGuid());
        //Criteria currentDOCriteria = persistenceSession.createCriteria(distributedObject.getClass());
        //currentDOCriteria.add(Restrictions.eq("guid", distributedObject.getGuid()));
        //distributedObject.createProjections(currentDOCriteria, 0, "");
        //currentDOCriteria.setMaxResults(1);
        //DistributedObject currentDO = (DistributedObject) currentDOCriteria.uniqueResult();
        // Создание в БД нового экземпляра РО.
        final String simpleClassName = distributedObject.getClass().getSimpleName();
        if (distributedObject.getTagName().equals("C")) {
            if (currentDO != null) {
                final String message = simpleClassName + " DUPLICATE_GUID : " + distributedObject.getGuid();
                throw new DistributedObjectException(message);
            }
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setGlobalVersionOnCreate(currentMaxVersion);
            distributedObject = doService.createDO(distributedObject);
            persistenceSession.save(distributedObject);
            distributedObject.setTagName("C");
        }
        // Изменение существующего в БД экземпляра РО.
        if (distributedObject.getTagName().equals("M")) {
            if (currentDO == null) {
                final String message =simpleClassName + " NOT_FOUND_VALUE : " + distributedObject.getGuid();
                throw new DistributedObjectException(message);
            }
            Long currentVersion = currentDO.getGlobalVersion();
            Long objectVersion = distributedObject.getGlobalVersion();
            DOConflict doConflict = null;
            // Проверка на наличие конфликта версионности.
            if (objectVersion != null && currentVersion != null && !objectVersion.equals(currentVersion)) {
                doConflict = createConflict(distributedObject, currentDO);
            }
            currentDO.fill(distributedObject);
            currentDO.setDeletedState(distributedObject.getDeletedState());
            currentDO.setLastUpdate(new Date());
            currentDO.setGlobalVersion(currentMaxVersion);
            distributedObject = doService.mergeDO(currentDO, doConflict);
            //persistenceSession.saveOrUpdate(doConflict);
            //persistenceSession.saveOrUpdate(distributedObject);
            distributedObject.setTagName("M");
        }
        return distributedObject;
    }

    private DOConflict createConflict(DistributedObject distributedObject, DistributedObject currentDO) throws Exception {
        DOConflict conflict = new DOConflict();
        conflict.setValueInc(createStringElement(conflictDocument, distributedObject));
        conflict.setgVersionInc(distributedObject.getGlobalVersion());
        conflict.setIdOfOrg(idOfOrg);
        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
        conflict.setgVersionCur(currentDO.getGlobalVersion());
        conflict.setgVersionResult(currentDO.getGlobalVersion());
        conflict.setValueCur(createStringElement(conflictDocument, currentDO));
        return conflict;
    }

    /* взять из XML Utills */
    private String createStringElement(Document document, DistributedObject distributedObject) throws TransformerException {
        Element element = document.createElement("O");
        element = distributedObject.toElement(element);
        return XMLUtils.nodeToString(element);
    }

}
