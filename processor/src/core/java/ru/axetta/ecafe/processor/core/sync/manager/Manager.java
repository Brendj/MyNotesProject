/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
import ru.axetta.ecafe.processor.core.persistence.SyncHistoryException;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOGroupsFactory;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOSyncClass;
import ru.axetta.ecafe.processor.core.sync.doGroups.IDOGroup;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
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
     * Ключи = имена элементов (Пример элемента: <Product>),
     * значения = текущие максимальые версии объектов
     * (Пример версии: атрибут V тега <Product V="20">)
     */
    private HashMap<String, Long> currentMaxVersions = new HashMap<String, Long>();

    /**
     * Ключи = имена элементов (Пример элемента: <Product>),
     * значения = максимальное количество запрашиваемое клиентом
     * (Пример версии: атрибут Limit тега <Product Limit="20">)
     */
    private HashMap<String, Integer> currentLimits = new HashMap<String, Integer>();

    /**
     * Ключи = имена элементов (Пример элемента: <Product>),
     * значения = UUID последнего объекта успешно обработаноого клиентом
     * (Пример версии: атрибут Limit тега <Product LastGuid="00a7d120-6d6e-41cb-ba0b-2068d3308f68">)
     */
    private HashMap<String, String> currentLastGuids = new HashMap<String, String>();

    private SortedMap<DOSyncClass, List<DistributedObject>> resultDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private SortedMap<DOSyncClass, List<DistributedObject>> incomeDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    private SortedMap<String, List<String>> confirmDOMap = new TreeMap<String, List<String>>();
    private Long idOfOrg;
    private SyncHistory syncHistory;
    //private final String[] doGroupNames;
    private final List<String> doGroupNames;
    private final DOGroupsFactory doGroupsFactory = new DOGroupsFactory();

    /* Максимальное количество объектов используемых в запросах конструкции IN */
    private static final int maxCount = 1000;

    private Document conflictDocument;

    public void setSyncHistory(SyncHistory syncHistory) {
        this.syncHistory = syncHistory;
    }

    //public Manager(Long idOfOrg, String[] doGroupNames) {
    //    this.idOfOrg = idOfOrg;
    //    this.doGroupNames = doGroupNames;
    //    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //    try {
    //        this.conflictDocument = factory.newDocumentBuilder().newDocument();
    //    } catch (Exception ex) {
    //        throw new RuntimeException(ex.getMessage());
    //    }
    //}

    public Manager(Long idOfOrg, List<String> doGroupNames) {
        this.idOfOrg = idOfOrg;
        this.doGroupNames = doGroupNames;
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

        // Если группы явно не указаны, то пока берем все.
        Node confirmNode = XMLUtils.findFirstChildElement(roNode, "Confirm");
        if (confirmNode != null)
        // Обработка секции <Confirm>
        {
            buildConfirmNode(confirmNode);
        }
        // Получаем секции РО, которые будем обрабатывать.
        List<Node> doNodeList = XMLUtils.findNodesWithNameNotEqualsTo(roNode, "Confirm");
        for (String groupName : doGroupNames) {
            IDOGroup doGroup = doGroupsFactory.createGroup(groupName);
            buildOneGroup(doGroup, doNodeList.iterator());
        }
    }

    private void buildOneGroup(IDOGroup doGroup, Iterator<Node> iter) {
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
                        if (e instanceof DistributedObjectException) {
                            distributedObject.setDistributedObjectException((DistributedObjectException) e);
                            LOGGER.error(distributedObject.toString(), e);
                        } else {
                            distributedObject
                                    .setDistributedObjectException(new DistributedObjectException("Internal Error"));
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
            if (distributedObjects != null && !distributedObjects.isEmpty()) {
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

    public void clearConfirm(SessionFactory sessionFactory, String classSimpleName) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        List<String> guids = confirmDOMap.get(classSimpleName);
        if (guids != null && !guids.isEmpty()) {
            try {
                persistenceSession = sessionFactory.openSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                int size = guids.size();
                if (size > maxCount) {
                    int position = 0;
                    while (position < size) {
                        int end = (position + maxCount < size ? position + maxCount : size);
                        List<String> subListGuids = guids.subList(position, end);
                        final String sql = "delete from DOConfirm where guid in (:guid) and distributedObjectClassName=:className and orgOwner=:idOfOrg";
                        Query deleteQuery = persistenceSession.createQuery(sql);
                        deleteQuery.setParameterList("guid", subListGuids);
                        deleteQuery.setParameter("className", classSimpleName);
                        deleteQuery.setParameter("idOfOrg", idOfOrg);
                        deleteQuery.executeUpdate();
                        position = end;
                    }
                } else {
                    final String sql = "delete from DOConfirm where guid in (:guid) and distributedObjectClassName=:className and orgOwner=:idOfOrg";
                    Query deleteQuery = persistenceSession.createQuery(sql);
                    deleteQuery.setParameterList("guid", guids);
                    deleteQuery.setParameter("className", classSimpleName);
                    deleteQuery.setParameter("idOfOrg", idOfOrg);
                    deleteQuery.executeUpdate();
                }

                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                // TODO: записать в журнал ошибок
                //saveException(sessionFactory, e);
                errorMessage = e.getMessage();
                LOGGER.error("Error clear Confirms: " + e.getMessage());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, LOGGER);
                HibernateUtils.close(persistenceSession, LOGGER);
                if (StringUtils.isNotEmpty(errorMessage)) {
                    saveException(sessionFactory, errorMessage);
                }
            }
        }

    }

    public void process(SessionFactory sessionFactory) {
        LOGGER.debug("RO begin process section");
        //sessionFactory = RuntimeContext.reportsSessionFactory;
        SortedMap<DOSyncClass, List<DistributedObject>> currentDOListMap = new TreeMap<DOSyncClass, List<DistributedObject>>();

        for (DOSyncClass doSyncClass : incomeDOMap.keySet()) {
            final Class<? extends DistributedObject> doClass = doSyncClass.getDoClass();
            final String classSimpleName = doClass.getSimpleName();

            LOGGER.debug("init clearConfirm");
            clearConfirm(sessionFactory, classSimpleName);
            LOGGER.debug("end clearConfirm");

            final Integer currentLimit = currentLimits.get(classSimpleName);
            LOGGER.debug("init findResponseResult");
            if (currentLimit == null || currentLimit <= 0) {
                List<DistributedObject> currentResultDOList = findResponseResult(sessionFactory, doClass, currentLimit);
                LOGGER.debug("end findResponseResult");
                resultDOMap.put(doSyncClass, currentResultDOList);
                LOGGER.debug("init processDistributedObjectsList");
                List<DistributedObject> distributedObjectsList = processDistributedObjectsList(sessionFactory,
                        doSyncClass);
                LOGGER.debug("end processDistributedObjectsList");
                currentDOListMap.put(doSyncClass, distributedObjectsList);
                LOGGER.debug("init addConfirms");
                addConfirms(sessionFactory, classSimpleName, currentResultDOList);
                LOGGER.debug("end addConfirms");
                List<DistributedObject> currentConfirmDOList = null;
                LOGGER.debug("init findConfirmedDO");
                currentConfirmDOList = findConfirmedDO(sessionFactory, doClass);
                LOGGER.debug("end findConfirmedDO");
                if (!currentConfirmDOList.isEmpty()) {
                    for (DistributedObject distributedObject : currentConfirmDOList) {
                        if (!currentResultDOList.contains(distributedObject)) {
                            currentResultDOList.add(distributedObject);
                        }
                    }
                }

            } else {
                LOGGER.debug("init findConfirmedDO");
                List<DistributedObject> currentResultDOList = findConfirmedDO(sessionFactory, doClass);
                LOGGER.debug("end findConfirmedDO");
                final int newLimit = currentLimit - currentResultDOList.size();
                if (newLimit > 0) {
                    List<DistributedObject> newResultDOList = findResponseResult(sessionFactory, doClass, newLimit);
                    currentResultDOList.addAll(newResultDOList);
                    LOGGER.debug("end findResponseResult");
                    LOGGER.debug("init addConfirms");
                    addConfirms(sessionFactory, classSimpleName, currentResultDOList);
                    LOGGER.debug("end addConfirms");
                }
                resultDOMap.put(doSyncClass, currentResultDOList);

            }
        }
        incomeDOMap = currentDOListMap;
        LOGGER.debug("RO end process section");
    }

    private List<DistributedObject> findResponseResult(SessionFactory sessionFactory,
            Class<? extends DistributedObject> doClass, final Integer currentLimit) {
        List<DistributedObject> currentResultDOList = new ArrayList<DistributedObject>();
        //sessionFactory = RuntimeContext.reportsSessionFactory;
        Session persistenceSession = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            DistributedObject refDistributedObject = doClass.newInstance();
            final String classSimpleName = doClass.getSimpleName();
            Long currentMaxVersion = currentMaxVersions.get(classSimpleName);
            if (currentLimit == null || currentLimit <= 0) {
                currentResultDOList = refDistributedObject.process(persistenceSession, idOfOrg, currentMaxVersion);
            } else {
                Criteria criteria = persistenceSession.createCriteria(doClass);
                final String currentLastGuid = currentLastGuids.get(classSimpleName);
                refDistributedObject.createProjections(criteria);

                if (StringUtils.isNotEmpty(currentLastGuid)) {
                    Disjunction mainRestriction = Restrictions.disjunction();
                    mainRestriction.add(Restrictions.gt("globalVersion", currentMaxVersion));
                    Conjunction andRestr = Restrictions.conjunction();
                    andRestr.add(Restrictions.gt("guid", currentLastGuid));
                    andRestr.add(Restrictions.ge("globalVersion", currentMaxVersion));
                    mainRestriction.add(andRestr);
                    criteria.add(mainRestriction);
                } else {
                    criteria.add(Restrictions.ge("globalVersion", currentMaxVersion));
                }

                criteria.addOrder(Order.asc("globalVersion"));
                criteria.addOrder(Order.asc("guid"));
                criteria.setMaxResults(currentLimit);
                criteria.setResultTransformer(Transformers.aliasToBean(doClass));
                currentResultDOList = (List<DistributedObject>) criteria.list();
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            LOGGER.error("Error findResponseResult: " + e.getMessage(), e);
        } finally {
            HibernateUtils.close(persistenceSession, LOGGER);
            if (StringUtils.isNotEmpty(errorMessage)) {
                saveException(sessionFactory, errorMessage);
            }
        }
        return currentResultDOList;
    }

    private List<DistributedObject> findConfirmedDO(SessionFactory sessionFactory,
            Class<? extends DistributedObject> doClass) {
        Session persistenceSession = null;
        String errorMessage = null;
        List<DistributedObject> currentConfirmDOList = new ArrayList<DistributedObject>();
        try {
            persistenceSession = sessionFactory.openSession();
            DistributedObject refDistributedObject = doClass.newInstance();

            final String classSimpleName = doClass.getSimpleName();
            final Integer currentLimit = currentLimits.get(classSimpleName);
            if (currentLimit == null || currentLimit <= 0) {
                DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DOConfirm.class);
                detachedCriteria.add(Restrictions.eq("distributedObjectClassName", classSimpleName));
                detachedCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
                detachedCriteria.setProjection(Property.forName("guid"));

                Criteria distributedObjectCriteria = persistenceSession.createCriteria(doClass);
                distributedObjectCriteria.add(Property.forName("guid").in(detachedCriteria));
                refDistributedObject.createProjections(distributedObjectCriteria);
                distributedObjectCriteria.setResultTransformer(Transformers.aliasToBean(doClass));
                currentConfirmDOList = (List<DistributedObject>) distributedObjectCriteria.list();
            } else {
                Criteria criteria = persistenceSession.createCriteria(doClass);
                final String currentLastGuid = currentLastGuids.get(classSimpleName);
                Long currentMaxVersion = currentMaxVersions.get(classSimpleName);
                refDistributedObject.createProjections(criteria);

                if (StringUtils.isNotEmpty(currentLastGuid)) {
                    Disjunction mainRestriction = Restrictions.disjunction();
                    mainRestriction.add(Restrictions.gt("globalVersion", currentMaxVersion));
                    Conjunction andRestr = Restrictions.conjunction();
                    andRestr.add(Restrictions.gt("guid", currentLastGuid));
                    andRestr.add(Restrictions.ge("globalVersion", currentMaxVersion));
                    mainRestriction.add(andRestr);
                    criteria.add(mainRestriction);
                } else {
                    criteria.add(Restrictions.ge("globalVersion", currentMaxVersion));
                }
                criteria.addOrder(Order.asc("globalVersion"));
                criteria.addOrder(Order.asc("guid"));
                criteria.setMaxResults(currentLimit);
                criteria.setResultTransformer(Transformers.aliasToBean(doClass));
                currentConfirmDOList = (List<DistributedObject>) criteria.list();
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
            LOGGER.error("Error findConfirms: " + e.getMessage());
        } finally {
            HibernateUtils.close(persistenceSession, LOGGER);
            if (StringUtils.isNotEmpty(errorMessage)) {
                saveException(sessionFactory, errorMessage);
            }
        }
        return currentConfirmDOList;
    }

    private void buildConfirmNode(Node node) throws Exception {
        LOGGER.debug("RO parse Confirm XML section");
        Node childNode = node.getFirstChild();
        while (childNode != null) {
            if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                final String className = childNode.getNodeName().trim();
                final String guid = childNode.getAttributes().getNamedItem("Guid").getTextContent();
                if (StringUtils.isNotEmpty(guid)) {
                    if (!confirmDOMap.containsKey(className)) {
                        confirmDOMap.put(className, new ArrayList<String>());
                    }
                    confirmDOMap.get(className).add(guid);
                }
            }
            childNode = childNode.getNextSibling();
        }
        LOGGER.debug("RO end parse Confirm XML section");
    }

    private void addConfirms(SessionFactory sessionFactory, String simpleName,
            List<DistributedObject> confirmDistributedObjectList) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            //persistenceSession.setCacheMode(CacheMode.IGNORE);
            if (confirmDistributedObjectList != null && !confirmDistributedObjectList.isEmpty()) {
                final int size = confirmDistributedObjectList.size();
                List<String> currentGUIDs = new ArrayList<String>(size);
                List<String> dbGUIDs = new ArrayList<String>();
                LOGGER.debug("addConfirms: currentGUIDs create list");
                long duration = System.currentTimeMillis();
                for (DistributedObject distributedObject : confirmDistributedObjectList) {
                    currentGUIDs.add(distributedObject.getGuid());
                }
                duration = System.currentTimeMillis() - duration;
                LOGGER.debug("addConfirms: currentGUIDs end duration: " + duration);
                duration = System.currentTimeMillis();
                LOGGER.debug("addConfirms: find exist UUID ");
                if (size > maxCount) {
                    int position = 0;
                    while (position < size) {
                        int end = (position + maxCount < size ? position + maxCount : size);
                        List<String> subListGuids = currentGUIDs.subList(position, end);
                        Criteria uuidCriteria = persistenceSession.createCriteria(DOConfirm.class);
                        uuidCriteria.add(Restrictions.eq("distributedObjectClassName", simpleName));
                        uuidCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
                        uuidCriteria.add(Restrictions.in("guid", subListGuids));
                        uuidCriteria.setProjection(Projections.property("guid"));
                        List<String> dbGUIDs1 = (List<String>) uuidCriteria.list();
                        dbGUIDs.addAll(dbGUIDs1);
                        position = end;
                    }
                } else {
                    Criteria uuidCriteria = persistenceSession.createCriteria(DOConfirm.class);
                    uuidCriteria.add(Restrictions.eq("distributedObjectClassName", simpleName));
                    uuidCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
                    uuidCriteria.add(Restrictions.in("guid", currentGUIDs));
                    uuidCriteria.setProjection(Projections.property("guid"));
                    dbGUIDs = (List<String>) uuidCriteria.list();
                }
                duration = System.currentTimeMillis() - duration;
                LOGGER.debug("addConfirms: end find exist UUID duration: " + duration);
                final boolean b = (dbGUIDs!=null && currentGUIDs==null) || (dbGUIDs==null && currentGUIDs!=null) ||
                        (dbGUIDs!=null && currentGUIDs!=null && dbGUIDs.size() != currentGUIDs.size());
                LOGGER.debug("addConfirms: current and DB count not equals:" + b);
                if (b) {
                    LOGGER.debug("addConfirms: persist not exists confirm: " + duration);
                    duration = System.currentTimeMillis();
                    for (DistributedObject distributedObject : confirmDistributedObjectList) {
                        String uuid = distributedObject.getGuid();
                        if (!dbGUIDs.contains(uuid)) {
                            createConfirm(persistenceSession, simpleName, uuid);
                        }
                    }
                    duration = System.currentTimeMillis() - duration;
                    LOGGER.debug("addConfirms: end persist not exists confirm: " + duration);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            errorMessage = "Error addConfirms: " + e.getMessage();
            LOGGER.error(errorMessage);
        } finally {
            HibernateUtils.close(persistenceSession, LOGGER);
            if (StringUtils.isNotEmpty(errorMessage)) {
                saveException(sessionFactory, errorMessage);
            }
        }


    }

    private void createConfirm(Session session, String simpleName, String uuid) {
        DOConfirm confirm = new DOConfirm(simpleName, uuid, idOfOrg);
        session.save(confirm);
    }

    private List<DistributedObject> processDistributedObjectsList(SessionFactory sessionFactory,
            DOSyncClass doSyncClass) {
        LOGGER.debug("processDistributedObjectsList: init");
        List<DistributedObject> distributedObjects = incomeDOMap.get(doSyncClass);
        if (doSyncClass.getDoClass() == CycleDiagram.class) {
            Collections.sort(distributedObjects, new Comparator<DistributedObject>() {
                @Override
                public int compare(DistributedObject o1, DistributedObject o2) {
                    int res = o1.getDeletedState().equals(o2.getDeletedState()) ? 0
                            : (o1.getDeletedState() == Boolean.TRUE ? -1 : 1);
                    if (res != 0) {
                        return res;
                    }
                    return o1.getGuid().compareTo(o2.getGuid());
                }
            });
        }
        List<DistributedObject> distributedObjectList = new ArrayList<DistributedObject>();
        LOGGER.debug("processDistributedObjectsList: init data");
        if (!distributedObjects.isEmpty()) {
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            Long currentMaxVersion = updateDOVersion(sessionFactory, doSyncClass.getDoClass().getSimpleName());
            for (DistributedObject distributedObject : distributedObjects) {
                LOGGER.debug("Process: {}", distributedObject.toString());
                DistributedObject currentDistributedObject = processCurrentObject(sessionFactory, distributedObject,
                        currentMaxVersion);
                distributedObjectList.add(currentDistributedObject);
            }
            /* generate result list */
            List<DistributedObject> currentResultDOList = resultDOMap.get(doSyncClass);
            /* уберем все объекты которые есть в конфирме */
            if (!(currentResultDOList == null || currentResultDOList.isEmpty())) {
                currentResultDOList.removeAll(distributedObjectList);
            }
        }
        LOGGER.debug("processDistributedObjectsList: end");
        return distributedObjectList;
    }

    private Long updateDOVersion(SessionFactory sessionFactory, String doClass){
        Long version = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            version = DOVersionRepository.updateClassVersion(doClass, persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
            if (StringUtils.isNotEmpty(errorMessage)) {
                saveException(sessionFactory, errorMessage);
            }
        }
        return version;
    }



    private DistributedObject processCurrentObject(SessionFactory sessionFactory, DistributedObject distributedObject,
            Long currentMaxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (distributedObject.getDistributedObjectException() == null) {
                String tagName = distributedObject.getTagName();
                if (!(distributedObject.getDeletedState() == null || distributedObject.getDeletedState())) {
                    distributedObject.preProcess(persistenceSession, idOfOrg);
                    distributedObject = processDistributedObject(persistenceSession, distributedObject,
                            currentMaxVersion);
                } else {
                    distributedObject = updateDeleteState(persistenceSession, distributedObject, currentMaxVersion);
                }
                distributedObject.setTagName(tagName);
            } else {
                Org org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
                DistributedObjectException de = distributedObject.getDistributedObjectException();
                SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory,
                        "Error processCurrentObject: " + de.getMessage());
                persistenceSession.save(syncHistoryException);
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (DistributedObjectException e) {
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            // TODO: записать в журнал ошибок
            //saveException(sessionFactory, e);
            distributedObject.setDistributedObjectException(e);
            errorMessage = "Error processCurrentObject: " + e.getMessage();
            LOGGER.error(errorMessage);
        } catch (Exception e) {
            // TODO: записать в журнал ошибок
            //saveException(sessionFactory, e);
            //errorMessage = e.getMessage();
            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
            LOGGER.error(distributedObject.toString(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
            if (StringUtils.isNotEmpty(errorMessage)) {
                saveException(sessionFactory, errorMessage);
            }
        }
        return distributedObject;
    }

    private void saveException(SessionFactory sessionFactory, String message) {
        Session persistenceSession = sessionFactory.openSession();
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
            SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory, message);
            persistenceSession.save(syncHistoryException);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            LOGGER.error("createSyncHistory exception: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
    }

    private DistributedObject updateDeleteState(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion) throws Exception {
        Criteria currentDOCriteria = persistenceSession.createCriteria(distributedObject.getClass());
        currentDOCriteria.add(Restrictions.eq("guid", distributedObject.getGuid()));
        distributedObject.createProjections(currentDOCriteria);
        currentDOCriteria.setResultTransformer(Transformers.aliasToBean(distributedObject.getClass()));
        currentDOCriteria.setMaxResults(1);
        DistributedObject currentDO = (DistributedObject) currentDOCriteria.uniqueResult();
        if (currentDO == null) {
            throw new DistributedObjectException(
                    distributedObject.getClass().getSimpleName() + " NOT_FOUND_VALUE : " + distributedObject.getGuid());
        }
        currentDO.setLastUpdate(new Date());
        currentDO.setDeleteDate(new Date());
        currentDO.setGlobalVersion(currentMaxVersion);
        currentDO.setDeletedState(true);
        currentDO.setTagName("M");
        currentDO.preProcess(persistenceSession, idOfOrg);
        persistenceSession.update(currentDO);
        return currentDO;
        //return doService.update(currentDO);
    }

    private DistributedObject processDistributedObject(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion) throws Exception {
        final Class<? extends DistributedObject> aClass = distributedObject.getClass();
        Criteria currentDOCriteria = persistenceSession.createCriteria(aClass);
        currentDOCriteria.add(Restrictions.eq("guid", distributedObject.getGuid()));
        distributedObject.createProjections(currentDOCriteria);
        currentDOCriteria.setResultTransformer(Transformers.aliasToBean(aClass));
        currentDOCriteria.setMaxResults(1);
        DistributedObject currentDO = (DistributedObject) currentDOCriteria.uniqueResult();
        // Создание в БД нового экземпляра РО.
        final String simpleClassName = aClass.getSimpleName();
        if (distributedObject.getTagName().equals("C")) {
            if (currentDO != null) {
                final String message = simpleClassName + " DUPLICATE_GUID : " + distributedObject.getGuid();
                throw new DistributedObjectException(message);
            }
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setGlobalVersionOnCreate(currentMaxVersion);
            distributedObject.setCreatedDate(new Date());
            //distributedObject.beforePersist(persistenceSession, idOfOrg, distributedObject.getGuid());
            persistenceSession.persist(distributedObject);
            distributedObject.setTagName("C");
        }
        // Изменение существующего в БД экземпляра РО.
        if (distributedObject.getTagName().equals("M")) {
            if (currentDO == null) {
                final String message = simpleClassName + " NOT_FOUND_VALUE : " + distributedObject.getGuid();
                throw new DistributedObjectException(message);
            }
            Long currentVersion = currentDO.getGlobalVersion();
            Long objectVersion = distributedObject.getGlobalVersion();
            currentDO.fill(distributedObject);
            currentDO.setDeletedState(distributedObject.getDeletedState());
            currentDO.setLastUpdate(new Date());
            currentDO.setGlobalVersion(currentMaxVersion);
            DOConflict doConflict = null;
            // Проверка на наличие конфликта версионности.
            if (objectVersion != null && currentVersion != null && !objectVersion.equals(currentVersion)) {
                doConflict = createConflict(distributedObject, currentDO);
                persistenceSession.persist(doConflict);
            }
            currentDO.setTagName("M");
            //currentDO.beforePersist(persistenceSession, idOfOrg, distributedObject.getGuid());
            currentDO.preProcess(persistenceSession, idOfOrg);
            currentDO.updateVersionFromParent(persistenceSession);
            persistenceSession.update(currentDO);
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setTagName("M");
        }
        return distributedObject;
    }

    private DOConflict createConflict(DistributedObject distributedObject, DistributedObject currentDO)
            throws Exception {
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
    private String createStringElement(Document document, DistributedObject distributedObject)
            throws TransformerException {
        Element element = document.createElement("O");
        element = distributedObject.toElement(element);
        return XMLUtils.nodeToString(element);
    }

}
