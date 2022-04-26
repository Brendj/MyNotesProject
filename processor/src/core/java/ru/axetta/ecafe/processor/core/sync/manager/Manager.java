/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.daoservices.commodity.accounting.GoodRequestRepository;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;
import ru.axetta.ecafe.processor.core.persistence.SyncHistoryException;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodAgeGroupType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingValueParser;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOGroupsFactory;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOSyncClass;
import ru.axetta.ecafe.processor.core.sync.doGroups.IDOGroup;
import ru.axetta.ecafe.processor.core.sync.manager.modifier.DistributedObjectModifier;
import ru.axetta.ecafe.processor.core.sync.manager.modifier.ModifierTypeFactory;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.08.12
 * Time: 17:27
 */

public class Manager implements AbstractToElement {

    /**
     * Логгер
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    /* Максимальное количество объектов используемых в запросах конструкции IN */
    private static final int maxCount = 1000;
    private final List<String> doGroupNames;
    private final DOGroupsFactory doGroupsFactory = new DOGroupsFactory();
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


    /**
     * Ключи = имена элементов (Пример элемента: <Product>),
     * значения = объем информации, который запрашивает клиент
     * (Пример версии: атрибут Limit тега <Product LastGuid="00a7d120-6d6e-41cb-ba0b-2068d3308f68">)
     */
    private Map<String, DistributedObject.InformationContents> currentInformationContents = new HashMap<String, DistributedObject.InformationContents>();

    private SortedMap<DOSyncClass, List<DistributedObject>> resultDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private SortedMap<DOSyncClass, List<DistributedObject>> incomeDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    private SortedMap<String, List<String>> confirmDOMap = new TreeMap<String, List<String>>();
    private Long idOfOrg;
    private SyncHistory syncHistory;
    private Document conflictDocument;

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

    public void setSyncHistory(SyncHistory syncHistory) {
        this.syncHistory = syncHistory;
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
        //Node confirmNode = XMLUtils.findFirstChildElement(roNode, "Confirm");
        // Обработка секции <Confirm>
        List<Node> confirmNodes = XMLUtils.findNodesWithNameEqualsTo(roNode, "Confirm");
        for (Node confirmNode : confirmNodes) {
            if (confirmNode != null) {
                buildConfirmNode(confirmNode);
            }
        }
        // Получаем секции РО, которые будем обрабатывать.
        List<Node> doNodeList = XMLUtils.findNodesWithNameNotEqualsTo(roNode, "Confirm");
        for (String groupName : doGroupNames) {
            IDOGroup doGroup = doGroupsFactory.createGroup(groupName);
            buildOneGroup(doGroup, doNodeList.iterator());
        }
    }

    private void buildOneGroup(IDOGroup doGroup, Iterator<Node> nodeList) {
        while (nodeList.hasNext()) {
            Node doNode = nodeList.next();
            DOSyncClass doSyncClass = doGroup.getDOSyncClass(doNode.getNodeName().trim());
            if (doSyncClass != null) {
                // Обработка перечня экземпляров РО одного определенного класса.
                buildDONode(doNode, doSyncClass);
                nodeList.remove();
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
        if (XMLUtils.getIntegerAttributeValue(doNode, "Contents") != null) {
            currentInformationContents.put(doSyncClass.getDoClass().getSimpleName(),
                    DistributedObject.InformationContents
                            .getByCode(XMLUtils.getIntegerAttributeValue(doNode, "Contents").intValue()));
        }
        incomeDOMap.put(doSyncClass, new ArrayList<DistributedObject>());
        doNode = doNode.getFirstChild();
        while (doNode != null) {
            if (Node.ELEMENT_NODE == doNode.getNodeType()) {
                DistributedObject distributedObject = null;
                try {
                    // TODO: реализовать паттерн билдер
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
                    distributedObject.setIdOfSyncOrg(idOfOrg);
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
                if (doSyncClass.getDoClass() == ECafeSettings.class) {
                    for (DistributedObject dob : distributedObjectsList) {
                        if (!currentResultDOList.contains(dob) && ((ECafeSettings) dob).isPreOrderFeeding()) {
                            currentResultDOList.add(dob);
                        }
                    }
                }
                LOGGER.debug("end processDistributedObjectsList");
                currentDOListMap.put(doSyncClass, distributedObjectsList);
                LOGGER.debug("init addConfirms");
                addConfirms(sessionFactory, classSimpleName, currentResultDOList);
                LOGGER.debug("end addConfirms");
                List<DistributedObject> currentConfirmDOList = null;
                LOGGER.debug("init findConfirmedDO");
                currentConfirmDOList = findConfirmedDO(sessionFactory, doClass, classSimpleName);
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
                Set<DistributedObject> currentResultDOSet = new HashSet<DistributedObject>(
                        findConfirmedDO(sessionFactory, doClass, classSimpleName));
                LOGGER.debug("end findConfirmedDO");
                final int newLimit = currentLimit - currentResultDOSet.size();
                if (newLimit > 0) {
                    List<DistributedObject> newResultDOList = findResponseResult(sessionFactory, doClass, newLimit);
                    if (doSyncClass.getDoClass().getName().contains("GoodRequestPosition") ||
                            doSyncClass.getDoClass().getName().contains("GoodRequest")) {
                        currentResultDOSet.clear();
                    }
                    currentResultDOSet.addAll(newResultDOList);
                    LOGGER.debug("end findResponseResult");
                    LOGGER.debug("init addConfirms");
                    addConfirms(sessionFactory, classSimpleName, new ArrayList<DistributedObject>(currentResultDOSet));
                    LOGGER.debug("end addConfirms");
                }
                LOGGER.debug("init processDistributedObjectsList");
                List<DistributedObject> distributedObjectsList = processDistributedObjectsList(sessionFactory,
                        doSyncClass);
                if (doSyncClass.getDoClass() == ECafeSettings.class) {
                    for (DistributedObject dob : distributedObjectsList) {
                        if (((ECafeSettings) dob).isPreOrderFeeding()) {
                            currentResultDOSet.add(dob);
                        }
                    }
                }
                LOGGER.debug("end processDistributedObjectsList");
                currentDOListMap.put(doSyncClass, distributedObjectsList);
                resultDOMap.put(doSyncClass,  new ArrayList<DistributedObject>(currentResultDOSet));

                if (doSyncClass.getDoClass().getName().contains("Staff")) {
                    refreshStaffs(doSyncClass, currentResultDOSet, distributedObjectsList);
                }
            }
        }
        incomeDOMap = currentDOListMap;
        LOGGER.debug("RO end process section");
    }

    private void refreshStaffs(DOSyncClass doSyncClass, Set<DistributedObject> currentResultDOSet,
            List<DistributedObject> distributedObjectsList) {

        if (distributedObjectsList != null && !distributedObjectsList.isEmpty()) {
            List<DistributedObject> refreshedStaffList = new ArrayList<DistributedObject>(currentResultDOSet);
            for (DistributedObject distributedObject : distributedObjectsList) {
                if (distributedObject.getTagName().equals("M") && distributedObject.getGlobalVersion() != null) {
                    for (DistributedObject distributedObj : refreshedStaffList) {
                        if (distributedObj.getGuid().equals(distributedObject.getGuid())) {
                            refreshedStaffList.remove(distributedObj);
                            refreshedStaffList.add(distributedObject);
                            break;
                        }
                    }
                    resultDOMap.put(doSyncClass, new ArrayList<DistributedObject>(refreshedStaffList));
                }
            }
        }
    }

    private List<DistributedObject> findResponseResult(SessionFactory sessionFactory,
            Class<? extends DistributedObject> doClass, final Integer currentLimit) {
        List<DistributedObject> currentResultDOList = new ArrayList<DistributedObject>();
        Session persistenceSession = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            DistributedObject refDistributedObject = doClass.newInstance();
            final String classSimpleName = doClass.getSimpleName();
            Long currentMaxVersion = currentMaxVersions.get(classSimpleName);
            final String currentLastGuid = currentLastGuids.get(classSimpleName);
            DistributedObject.InformationContents informationContent = currentInformationContents.get(classSimpleName);
            if (informationContent != null) {
                refDistributedObject.setNewInformationContent(informationContent);
            }
            List<DistributedObject> currentDOList = refDistributedObject
                    .process(persistenceSession, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
            if (!CollectionUtils.isEmpty(currentDOList)) {
                currentResultDOList.addAll(currentDOList);
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
        // обновляем ComplexId
        if (doClass.getSimpleName().equals("GoodRequestPosition")) {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                updateComplexId(session, currentResultDOList);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                LOGGER.error("Error findResponseResult: " + e.getMessage(), e);
            } finally {
                HibernateUtils.close(session, LOGGER);
                if (StringUtils.isNotEmpty(errorMessage)) {
                    saveException(sessionFactory, errorMessage);
                }
            }
            // Не выводим записи с пустым товаром
            GoodRequestRepository goodRequestRepository = GoodRequestRepository.getInstance();
            List<DistributedObject> currentResultDOListResult = new ArrayList<>();
            for(DistributedObject distributedObject : currentResultDOList) {
                if (!goodRequestRepository.isGoodRequestPositionWithoutGood((GoodRequestPosition) distributedObject)) {
                    currentResultDOListResult.add(distributedObject);
                }
            }
            currentResultDOList = currentResultDOListResult;
        }
        // Не выводим записи, все позиции которых с пустым товаром
        if (doClass.getSimpleName().equals("GoodRequest")) {
            GoodRequestRepository goodRequestRepository = GoodRequestRepository.getInstance();
            List<DistributedObject> currentResultDOListResult = new ArrayList<>();
            for (DistributedObject distributedObject : currentResultDOList) {
                if (!goodRequestRepository.isGoodRequestWithoutGood((GoodRequest) distributedObject)) {
                    currentResultDOListResult.add(distributedObject);
                }
            }
            currentResultDOList = currentResultDOListResult;
        }
        return currentResultDOList;
    }

    private void updateComplexId(Session session, List<DistributedObject> currentResultDOList) {
        for (DistributedObject distributedObject : currentResultDOList) {
            GoodRequestPosition goodRequestPosition = (GoodRequestPosition) distributedObject;
            if (goodRequestPosition.getGuidOfGR() != null && (goodRequestPosition.getComplexId() == null ||
                    goodRequestPosition.getComplexId() == 0)) {
                try {
                    Integer currentComplexID = DAOUtils.getComplexIdForGoodRequestPosition(session,
                            goodRequestPosition.getGuid());
                    if (currentComplexID == null) {
                        currentComplexID = 0;
                    }
                    goodRequestPosition.setComplexId(currentComplexID);
                    final String sql = "update GoodRequestPosition set complexid = :currentComplexID "
                            + "where guid = :guid";
                    Query query = session.createQuery(sql);
                    query.setParameter("guid", goodRequestPosition.getGuid());
                    query.setParameter("currentComplexID", currentComplexID);
                    query.executeUpdate();
                } catch (Exception e) {
                    LOGGER.error("Error by updating complexId: " + e.getMessage(), e);
                }
            }
        }
    }

    private List<DistributedObject> findConfirmedDO(SessionFactory sessionFactory,
            Class<? extends DistributedObject> doClass, String simpleName) {
        Session persistenceSession = null;
        String errorMessage = null;
        List<DistributedObject> currentConfirmDOList = new ArrayList<DistributedObject>();
        if (simpleName.equals("ExchangeBook")) {
            return currentConfirmDOList; // todo ExchangeBooks.class hardcoded
        }
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
                criteria.add(Restrictions.eq("orgOwner", idOfOrg));
                criteria.addOrder(Order.asc("globalVersion"));
                criteria.addOrder(Order.asc("guid"));
                if (doClass.getSimpleName().equals("GoodRequestPosition")) {
                    criteria.add(Restrictions.gt("gr.doneDate", new Date()));
                }
                if (doClass.getSimpleName().equals("GoodRequest")) {
                    criteria.add(Restrictions.gt("doneDate", new Date()));
                }
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

    @SuppressWarnings("unchecked")
    private void addConfirms(SessionFactory sessionFactory, String simpleName,
            List<DistributedObject> confirmDistributedObjectList) {
        if (simpleName.equals("ExchangeBook")) {
            return; // todo ExchangeBooks.class hardcoded
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (confirmDistributedObjectList != null && !confirmDistributedObjectList.isEmpty()) {
                final int size = confirmDistributedObjectList.size();
                Set<String> dbGUIDs = new HashSet<String>();
                List<String> currentGUIDs = new ArrayList<String>(size);
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
                    List<String> dbGUIDs1 = (List<String>) uuidCriteria.list();
                    dbGUIDs.addAll(dbGUIDs1);
                }
                duration = System.currentTimeMillis() - duration;
                LOGGER.debug("addConfirms: end find exist UUID duration: " + duration);
                final boolean b =
                        (dbGUIDs != null && currentGUIDs == null) || (dbGUIDs == null && currentGUIDs != null) || (
                                dbGUIDs != null && currentGUIDs != null && dbGUIDs.size() != currentGUIDs.size());
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
        Calendar calendarStart = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date startDate = calendarStart.getTime();
        List<DistributedObject> distributedObjects = incomeDOMap.get(doSyncClass);
        List<DistributedObject> distributedObjectList = new LinkedList<>();
        LOGGER.debug("processDistributedObjectsList: init data");
        if (!distributedObjects.isEmpty()) {
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            Long currentMaxVersion = updateDOVersion(sessionFactory, doSyncClass.getDoClass().getSimpleName());
            for (DistributedObject distributedObject : distributedObjects) {
                LOGGER.debug("Process: {}", distributedObject.toString());
                if (!distributedObject.getClass().getSimpleName().equals("ExchangeBook")) {
                    DistributedObject currentDistributedObject = processCurrentObject(sessionFactory, distributedObject,
                            currentMaxVersion);
                    distributedObjectList.add(currentDistributedObject);
                }
            }
            /* generate result list */
            List<DistributedObject> currentResultDOList = resultDOMap.get(doSyncClass);
            /* уберем все объекты которые есть в конфирме */
            if (!(currentResultDOList == null || currentResultDOList.isEmpty())) {
                currentResultDOList.removeAll(distributedObjectList);
            }
        }
        if (doSyncClass.getDoClass() == GoodRequestPosition.class && !distributedObjectList.isEmpty()) {
            notifyOrgsAboutChangeGoodRequests(startDate, distributedObjectList);
        }
        LOGGER.debug("processDistributedObjectsList: end");
        return distributedObjectList;
    }

    private void notifyOrgsAboutChangeGoodRequests(Date startDate, List<DistributedObject> distributedObjectList) {
        Calendar calendarEnd = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date lastCreateOrUpdateDate = calendarEnd.getTime();
        calendarEnd.add(Calendar.MINUTE, 1);
        final Date endGenerateTime = calendarEnd.getTime();
        // разослать уведомления всем организациям, чьи позиции изменились
        HashMap<Long, List<String>> mapPositions = new HashMap<Long, List<String>>();
        for (DistributedObject position : distributedObjectList) {
            Long orgOwner = position.getOrgOwner();
            if (!mapPositions.containsKey(orgOwner)) {
                mapPositions.put(orgOwner, new ArrayList<String>());
            }
            mapPositions.get(orgOwner).add(position.getGuid());
        }
        GoodRequestsChangeAsyncNotificationService notificationService = GoodRequestsChangeAsyncNotificationService
                .getInstance();
        for (Long orgOwner : mapPositions.keySet()) {
            List<String> guids = mapPositions.get(orgOwner);
            notificationService.notifyOrg(orgOwner, startDate, endGenerateTime, lastCreateOrUpdateDate, guids, false);
        }
    }

    private Long updateDOVersion(SessionFactory sessionFactory, String doClass) {
        Long version = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            version = DOVersionRepository.updateClassVersion(doClass, persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
        return version;
    }

    private DistributedObject processCurrentObject(SessionFactory sessionFactory, DistributedObject distributedObject,
            Long currentMaxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        DistributedObject currentDO = null;
        final Class<? extends DistributedObject> aClass = distributedObject.getClass();
        final String simpleClassName = aClass.getSimpleName();
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria currentDOCriteria = persistenceSession.createCriteria(aClass);
            currentDO = distributedObject.getCurrentDistributedObject(currentDOCriteria);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, LOGGER);
            HibernateUtils.close(persistenceSession, LOGGER);
        }
        /* Проверки вне транзакции */
        if (distributedObject.getTagName().equals("C") && currentDO != null) {
            final String message = simpleClassName + " DUPLICATE_GUID : " + distributedObject.getGuid();
            distributedObject.setDistributedObjectException(new DistributedObjectException(message));
            return distributedObject;
        }
        if (distributedObject.getTagName().equals("M") && currentDO == null) {
            final String message = simpleClassName + " NOT_FOUND_VALUE : " + distributedObject.getGuid();
            distributedObject.setDistributedObjectException(new DistributedObjectException(message));
            return distributedObject;
        }
        if (distributedObject instanceof LibraryDistributedObject) {
            distributedObject = makePreprocessAndProcessDOLibrary(sessionFactory,
                    (LibraryDistributedObject) distributedObject, (LibraryDistributedObject) currentDO,
                    currentMaxVersion);
        } else {
            distributedObject = makePreprocessAndProcessDO(sessionFactory, distributedObject, currentDO,
                    currentMaxVersion);
        }
        return distributedObject;
    }

    private DistributedObject makePreprocessAndProcessDO(SessionFactory sessionFactory,
            DistributedObject distributedObject, DistributedObject currentDO, Long currentMaxVersion) {
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
                    //Если заявка бала неправильной, а затем стала правильной (при модификации)
                    if (distributedObject.getGlobalVersion() != null && distributedObject.getGlobalVersion() == 0L)
                        distributedObject.setGlobalVersion(currentMaxVersion);
                    distributedObject = processDistributedObject(persistenceSession, distributedObject,
                            currentMaxVersion, currentDO);
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
            //Была попытка создания записи с неверной датой
            if (e.getMessage().equals("CANT_CHANGE_GRP_ON_DATE") && distributedObject.getTagName().equals("C")) {
                try {
                    Long temp = distributedObject.getGlobalVersion();
                    distributedObject.setGlobalVersion(0L);
                    if ((DistributedObject)distributedObject instanceof GoodRequestPosition) {
                        ((GoodRequestPosition) ((DistributedObject) distributedObject)).setTotalCount(0L);
                        ((GoodRequestPosition) ((DistributedObject) distributedObject)).setDailySampleCount(0L);
                    }
                    processDistributedObject(persistenceSession, distributedObject,
                            currentMaxVersion, currentDO);
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    distributedObject.setGlobalVersion(temp);
                } catch (Exception er) {
                }
            }
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            distributedObject.setDistributedObjectException(e);
            errorMessage = "Error processCurrentObject: " + e.getMessage();
            LOGGER.error(errorMessage);
        } catch (Exception e) {
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

    private LibraryDistributedObject makePreprocessAndProcessDOLibrary(SessionFactory sessionFactory,
            LibraryDistributedObject distributedObject, LibraryDistributedObject currentDO, Long currentMaxVersion) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        String errorMessage = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (distributedObject.getDistributedObjectException() == null) {
                String tagName = distributedObject.getTagName();
                if (!(distributedObject.getDeletedState() == null || distributedObject.getDeletedState())) {
                    distributedObject.mergedDistributedObject = null;
                    distributedObject.preProcess(persistenceSession, idOfOrg);
                    //Если заявка бала неправильной, а затем стала правильной (при модификации)
                    if (distributedObject.getGlobalVersion() != null && distributedObject.getGlobalVersion() == 0L)
                        distributedObject.setGlobalVersion(1L);
                    distributedObject = (LibraryDistributedObject) processDistributedObject(persistenceSession,
                            distributedObject, currentMaxVersion, currentDO);
                } else {
                    distributedObject = (LibraryDistributedObject) updateDeleteState(persistenceSession,
                            distributedObject, currentMaxVersion);
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
            //Была попытка создания записи с неверной датой
            if (e.getMessage().equals("CANT_CHANGE_GRP_ON_DATE") && distributedObject.getTagName().equals("C")) {
                try {
                    Long temp = distributedObject.getGlobalVersion();
                    distributedObject.setGlobalVersion(0L);
                    if ((DistributedObject)distributedObject instanceof GoodRequestPosition) {
                        ((GoodRequestPosition) ((DistributedObject) distributedObject)).setTotalCount(0L);
                        ((GoodRequestPosition) ((DistributedObject) distributedObject)).setDailySampleCount(0L);
                    }
                    processDistributedObject(persistenceSession, distributedObject,
                            currentMaxVersion, currentDO);
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    distributedObject.setGlobalVersion(temp);
                } catch (Exception er) {
                }
            }
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            distributedObject.setDistributedObjectException(e);
            errorMessage = "Error processCurrentObject: " + e.getMessage();
            LOGGER.error(errorMessage);
            if (distributedObject.mergedDistributedObject != null) {
                try {
                    distributedObject.mergedDistributedObject.setGlobalVersion(currentMaxVersion);
                    persistenceSession.update(distributedObject.mergedDistributedObject);
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                    //теперь добавим слитую запись БЗ в коллекцию для отправки клиенту в этой же сессии
                    List<DistributedObject> list = new ArrayList<DistributedObject>();
                    list.add(distributedObject.mergedDistributedObject);
                    DOSyncClass syncClass = new DOSyncClass(distributedObject.mergedDistributedObject.getClass(), 0);
                    resultDOMap.put(syncClass, list);
                } catch (Exception e2) {
                    distributedObject.mergedDistributedObject
                            .setDistributedObjectException(new DistributedObjectException("Internal Error"));
                    LOGGER.error(distributedObject.mergedDistributedObject.toString(), e);
                }
            }
        } catch (Exception e) {
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
        currentDO.setLastUpdate(new Date());
        currentDO.setDeleteDate(new Date());
        currentDO.setGlobalVersion(currentMaxVersion);
        currentDO.setDeletedState(true);
        currentDO.setTagName("M");
        currentDO.preProcess(persistenceSession, idOfOrg);
        persistenceSession.update(currentDO);
        return currentDO;
    }

    private DistributedObject processDistributedObject(Session persistenceSession, DistributedObject distributedObject,
            Long currentMaxVersion, DistributedObject currentDO) throws Exception {
        final Class<? extends DistributedObject> aClass = distributedObject.getClass();
        // Создание в БД нового экземпляра РО.
        if (distributedObject.getTagName().equals("C")) {
            if (distributedObject.getGlobalVersion() == null || distributedObject.getGlobalVersion() != 0L) {
                distributedObject.setGlobalVersion(currentMaxVersion);
            }
            distributedObject.setGlobalVersionOnCreate(currentMaxVersion);
            distributedObject.setCreatedDate(new Date());
            if (distributedObject instanceof GoodRequestPosition) {
                ((GoodRequestPosition) distributedObject).setNotified(false);
            }
            if (distributedObject instanceof Good) {
                if (null == ((Good) distributedObject).getGoodType()) {
                    ((Good) distributedObject).setGoodType(GoodType.UNSPECIFIED);
                }
                if (null == ((Good) distributedObject).getAgeGroupType()) {
                    ((Good) distributedObject).setAgeGroupType(GoodAgeGroupType.UNSPECIFIED);
                }
                if (null == ((Good) distributedObject).getDailySale()) {
                    ((Good) distributedObject).setDailySale(Boolean.FALSE);
                }
            }
            if (distributedObject instanceof ECafeSettings) {
                createOrgSettingByECafeSetting(persistenceSession, (ECafeSettings) distributedObject);
            }
            persistenceSession.persist(distributedObject);
            distributedObject.setTagName("C");
        }
        // Изменение существующего в БД экземпляра РО.
        if (distributedObject.getTagName().equals("M")) {
            //Никаких изменений в БД не вносить, если там заявка была отклонена по неверной дате
            if (distributedObject.getGlobalVersion() == null || distributedObject.getGlobalVersion() != 0L) {
                DistributedObjectModifier modifier = ModifierTypeFactory.createModifier(distributedObject);
                modifier.modifyDO(persistenceSession, distributedObject, currentMaxVersion, currentDO, idOfOrg,
                        conflictDocument);
            }


            /*Long currentVersion = currentDO.getGlobalVersion();
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
            currentDO.preProcess(persistenceSession, idOfOrg);
            currentDO.updateVersionFromParent(persistenceSession);
            persistenceSession.update(currentDO);
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setTagName("M");
            if(distributedObject instanceof ECafeSettings) {
                updateOrgSettingByECafeSetting(persistenceSession, (ECafeSettings) distributedObject);
            }*/
        }
        return distributedObject;
    }

    /*private void updateOrgSettingByECafeSetting(Session persistenceSession, ECafeSettings eCafeSettings) throws Exception {
        Date now = new Date();
        Long lastVersionOfOrgSetting = OrgSettingDAOUtils.getLastVersionOfOrgSettings(persistenceSession);
        Long lastVersionOfOrgSettingItem = OrgSettingDAOUtils.getLastVersionOfOrgSettingsItem(persistenceSession);

        Long nextVersionOfOrgSetting = (lastVersionOfOrgSetting == null ? 0L : lastVersionOfOrgSetting) + 1L;
        Long nextVersionOfOrgSettingItem = (lastVersionOfOrgSettingItem == null ? 0L : lastVersionOfOrgSettingItem) + 1L;

        OrgSetting setting = OrgSettingDAOUtils.getOrgSettingByGroupIdAndOrg(persistenceSession,
                eCafeSettings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING, eCafeSettings.getOrgOwner().intValue());
        if(setting == null){
            setting = new OrgSetting();
            setting.setCreatedDate(now);
            setting.setIdOfOrg(eCafeSettings.getOrgOwner());
            setting.setSettingGroup(OrgSettingGroup.getGroupById(eCafeSettings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
            setting.setLastUpdate(now);
            setting.setVersion(nextVersionOfOrgSetting);
            persistenceSession.save(setting);
        } else {
            setting.setLastUpdate(now);
            setting.setVersion(nextVersionOfOrgSetting);
        }

        SettingValueParser valueParser = new SettingValueParser(eCafeSettings.getSettingValue(), eCafeSettings.getSettingsId());
        Set<OrgSettingItem> itemsFromECafeSetting = valueParser.getParserBySettingValue().buildSetOfOrgSettingItem(setting, nextVersionOfOrgSetting);
        Map<Integer, OrgSettingItem> itemsFromOrgSetting = buildHashMap(setting.getOrgSettingItems());

        for(OrgSettingItem item : itemsFromECafeSetting){
            if(!itemsFromOrgSetting.containsKey(item.getSettingType())){
                setting.getOrgSettingItems().add(item);
                persistenceSession.persist(item);
            } else {
                OrgSettingItem orgSettingItem = itemsFromOrgSetting.get(item.getSettingType());
                orgSettingItem.setLastUpdate(now);
                orgSettingItem.setVersion(nextVersionOfOrgSettingItem);
                orgSettingItem.setSettingValue(item.getSettingValue());
                persistenceSession.persist(orgSettingItem);
            }
        }
        persistenceSession.persist(setting);
    }

    private Map<Integer, OrgSettingItem> buildHashMap(Set<OrgSettingItem> orgSettingItems) {
        Map<Integer, OrgSettingItem> map = new HashMap<>();
        for(OrgSettingItem item : orgSettingItems){
            map.put(item.getSettingType(), item);
        }
        return map;
    }*/

    private void createOrgSettingByECafeSetting(Session persistenceSession, ECafeSettings eCafeSettings)
            throws Exception {
        Date now = new Date();
        OrgSetting setting = new OrgSetting();
        Long lastVersionOfOrgSetting = OrgSettingDAOUtils.getLastVersionOfOrgSettings(persistenceSession);
        Long lastVersionOfOrgSettingItem = OrgSettingDAOUtils.getLastVersionOfOrgSettingsItem(persistenceSession);

        Long nextVersionOfOrgSetting = (lastVersionOfOrgSetting == null ? 0L : lastVersionOfOrgSetting) + 1L;
        Long nextVersionOfOrgSettingItem =
                (lastVersionOfOrgSettingItem == null ? 0L : lastVersionOfOrgSettingItem) + 1L;

        setting.setCreatedDate(now);
        setting.setLastUpdate(now);
        setting.setIdOfOrg(eCafeSettings.getOrgOwner());
        setting.setSettingGroup(OrgSettingGroup.getGroupById(
                eCafeSettings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
        setting.setVersion(nextVersionOfOrgSetting);

        SettingValueParser valueParser = new SettingValueParser(eCafeSettings.getSettingValue(),
                eCafeSettings.getSettingsId());

        Set<OrgSettingItem> items = valueParser.getParserBySettingValue()
                .buildSetOfOrgSettingItem(setting, nextVersionOfOrgSettingItem);
        setting.setOrgSettingItems(items);

        persistenceSession.persist(setting);
    }

    /*private DOConflict createConflict(DistributedObject distributedObject, DistributedObject currentDO)
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

    private String createStringElement(DocumentItem document, DistributedObject distributedObject)
            throws TransformerException {
        Element element = document.createElement("O");
        element = distributedObject.toElement(element);
        return XMLUtils.nodeToString(element);
    }*/

}
