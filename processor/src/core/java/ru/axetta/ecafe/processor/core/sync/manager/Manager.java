/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOGroupsFactory;
import ru.axetta.ecafe.processor.core.sync.doGroups.DOSyncClass;
import ru.axetta.ecafe.processor.core.sync.doGroups.IDOGroup;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
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
    private Logger logger = LoggerFactory.getLogger(Manager.class);
    /**
     * Ключи = имена элементов (Пример элемента: <Pr>),
     * значения = текущие максимальые версии объектов(Пример версии: атрибут V тега <Pr V="20">)
     */
    private HashMap<String, Long> currentMaxVersions = new HashMap<String, Long>();
    private SortedMap<DOSyncClass, List<DistributedObject>> resultDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private SortedMap<DOSyncClass, List<DistributedObject>> incomeDOMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
    private List<DOConfirm> doConfirms = new ArrayList<DOConfirm>();
    private Long idOfOrg;
    private SyncHistory syncHistory;
    private Document conflictDocument;

    private DOSyncService doService;

    public Manager(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
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
        String[] doGroupNames = StringUtils.split(XMLUtils.getAttributeValue(roNode, "DO_GROUPS"), ';');
        // Если группы явно не указаны, то пока берем все.
        if (doGroupNames == null || doGroupNames.length == 0)
            doGroupNames = new String[]{"ProductsGroup", "DocumentGroup", "SettingsGroup", "LibraryGroup"};
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
        logger.debug("RO parse '{}' node XML section", name);
        currentMaxVersions.put(doSyncClass.getDoClass().getSimpleName(), XMLUtils.getLongAttributeValue(doNode, "V"));
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
                        if (e instanceof DistributedObjectException)
                            distributedObject.setDistributedObjectException((DistributedObjectException) e);
                        else
                            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
                        logger.error(distributedObject.toString(), e);
                    }
                }
                List<DistributedObject> doList = incomeDOMap.get(doSyncClass);
                doList.add(distributedObject);
            }
            doNode = doNode.getNextSibling();
        }
        logger.debug("RO end parse '{}' node XML section", name);
    }

    public Element toElement(Document document) throws Exception {
        logger.debug("RO section begin generate XML node");
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        List<DistributedObject> distributedObjects;
        /* generate confirm element */
        logger.debug("Generate confirm element");
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
        logger.debug("Generate result objects.");
        for (Map.Entry<DOSyncClass, List<DistributedObject>> entry : resultDOMap.entrySet()) {
            distributedObjects = entry.getValue();
            String classTagName = entry.getKey().getDoClass().getSimpleName();
            Element doClassElement = document.createElement(classTagName);
            elementRO.appendChild(doClassElement);
            for (DistributedObject distributedObject : distributedObjects) {
                Element element = document.createElement("O");
                doClassElement.appendChild(distributedObject.toElement(element));
            }
            distributedObjects.clear();
        }
        logger.debug("Complete manager generate XML node.");
        return elementRO;
    }

    public void process(SessionFactory sessionFactory) {
        logger.debug("RO begin process section");
        doService.deleteDOConfirms(doConfirms);
        SortedMap<DOSyncClass, List<DistributedObject>> currentDOListMap = new TreeMap<DOSyncClass, List<DistributedObject>>();
        ConfigurationProvider configurationProvider = getConfigurationProvider(sessionFactory);
        Session session = sessionFactory.openSession();
        List<Long> menuExchangeRuleList = DAOUtils.getListIdOfOrgList(session, idOfOrg);
        HibernateUtils.close(session, logger);

        for (DOSyncClass doSyncClass : incomeDOMap.keySet()) {
            List<DistributedObject> currentResultDOList = generateResponseResult(sessionFactory, doSyncClass,
                    configurationProvider, menuExchangeRuleList);
            resultDOMap.put(doSyncClass, currentResultDOList);
            List<DistributedObject> distributedObjectsList = processDistributedObjectsList(sessionFactory, doSyncClass, configurationProvider);
            currentDOListMap.put(doSyncClass, distributedObjectsList);
            addConfirms(currentResultDOList);
            List<DistributedObject> currentConfirmDOList = doService.findConfirmedDO(doSyncClass.getDoClass(), idOfOrg);
            if (!currentConfirmDOList.isEmpty()) {
                for (DistributedObject distributedObject : currentConfirmDOList) {
                    if (!currentResultDOList.contains(distributedObject)) {
                        currentResultDOList.add(distributedObject);
                    }
                }
            }
        }
        incomeDOMap = currentDOListMap;
        logger.debug("RO end process section");
    }

    private void buildConfirmNode(Node node) throws Exception {
        logger.debug("RO parse Confirm XML section");
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
        logger.debug("RO end parse Confirm XML section");
    }

    private void addConfirms(List<DistributedObject> confirmDistributedObjectList) {
        for (DistributedObject distributedObject : confirmDistributedObjectList) {
            DOConfirm doConfirm = new DOConfirm();
            doConfirm.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
            doConfirm.setGuid(distributedObject.getGuid());
            doConfirm.setOrgOwner(idOfOrg);
            doService.addConfirm(doConfirm);
        }
    }

    private List<DistributedObject> processDistributedObjectsList(SessionFactory sessionFactory, DOSyncClass doSyncClass,
            ConfigurationProvider configurationProvider) {
        List<DistributedObject> distributedObjects = incomeDOMap.get(doSyncClass);
        List<DistributedObject> distributedObjectList = new ArrayList<DistributedObject>();
        if (!distributedObjects.isEmpty()) {
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            Long currentMaxVersion = doService.updateDOVersion(doSyncClass.getDoClass());
            for (DistributedObject distributedObject : distributedObjects) {
                logger.debug("Process: {}", distributedObject.toString());
                DistributedObject currentDistributedObject = processCurrentObject(sessionFactory, distributedObject, currentMaxVersion,
                        configurationProvider);
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

    private DistributedObject processCurrentObject(SessionFactory sessionFactory, DistributedObject distributedObject,
            Long currentMaxVersion, ConfigurationProvider configurationProvider) {
        Session persistenceSession = null;
        try {
            if (distributedObject.getDistributedObjectException() == null) {
                persistenceSession = sessionFactory.openSession();
                if (!(distributedObject.getDeletedState() == null || distributedObject.getDeletedState())) {
                    distributedObject.preProcess(persistenceSession);
                    if (distributedObject instanceof IConfigProvider) {
                        if (configurationProvider == null) {
                            throw new DistributedObjectException("CONFIGURATION_PROVIDER_NOT_FOUND");
                        } else {
                            ((IConfigProvider) distributedObject)
                                    .setIdOfConfigurationProvider(configurationProvider.getIdOfConfigurationProvider());
                        }
                    }
                    distributedObject = processDistributedObject(distributedObject, currentMaxVersion);
                } else {
                    String tagName = distributedObject.getTagName();
                    distributedObject = updateDeleteState(distributedObject, currentMaxVersion);
                    distributedObject.setTagName(tagName);
                }
            }
        } catch (DistributedObjectException e) {
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            // TODO: записать в журнал ошибок
            distributedObject.setDistributedObjectException(e);
            logger.error(distributedObject.toString(), e);
        } catch (Exception e) {
            // TODO: записать в журнал ошибок
            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
            logger.error(distributedObject.toString(), e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return distributedObject;
    }

    private ConfigurationProvider getConfigurationProvider(SessionFactory sessionFactory){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ConfigurationProvider configurationProvider = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Org org = DAOUtils.findOrg(persistenceSession,idOfOrg);
            configurationProvider = org.getConfigurationProvider();
            /* Если есть конфигурация синхронизируемой организации */
            if(configurationProvider==null){
                Query query = persistenceSession.createQuery("from MenuExchangeRule where idOfDestOrg=:idOfOrg");
                query.setParameter("idOfOrg",idOfOrg);
                List list = query.list();
                if(!(list == null || list.isEmpty())){
                    Org sourceOrg = DAOUtils.findOrg(persistenceSession, ((MenuExchangeRule) list.get(0)).getIdOfSourceOrg());
                    if(sourceOrg != null){
                        configurationProvider = sourceOrg.getConfigurationProvider();
                    }
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }  catch (Exception e){
            logger.error("Exception get ConfigurationProvider: ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return configurationProvider;
    }

    private List<DistributedObject> generateResponseResult(SessionFactory sessionFactory, DOSyncClass doSyncClass, ConfigurationProvider configurationProvider, List<Long> menuExchangeRuleList){
        List<DistributedObject> result = new ArrayList<DistributedObject>();
        Class<?> clazz = doSyncClass.getDoClass();
        Long currentMaxVersion = currentMaxVersions.get(doSyncClass.getDoClass().getSimpleName());
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria criteria = persistenceSession.createCriteria(clazz);
            List classList = Arrays.asList(clazz.getInterfaces());

            Criterion sendToAnyAllRestriction = Restrictions.conjunction();
            sendToAnyAllRestriction = ((Conjunction)sendToAnyAllRestriction)
                    .add(Restrictions.isNull("orgOwner"))
                    .add(Restrictions.eq("sendAll",SendToAssociatedOrgs.SendToAll));

            Criterion sendToAllRestriction = Restrictions.conjunction();
            Set<Long>  allOrg = new TreeSet<Long>();
            allOrg.addAll(menuExchangeRuleList);
            if(!menuExchangeRuleList.isEmpty()){
                allOrg.addAll(DAOUtils.getListIdOfOrgList(persistenceSession, menuExchangeRuleList.get(0)));
            } else {
                throw new DistributedObjectException("The organization has no source menu");
            }
            sendToAllRestriction = ((Conjunction)sendToAllRestriction)
                    .add(Restrictions.in("orgOwner",allOrg))
                    .add(Restrictions.eq("sendAll",SendToAssociatedOrgs.SendToAll));

            Criterion sendToMainRestriction = Restrictions.conjunction();
            allOrg.addAll(menuExchangeRuleList);
            allOrg.add(idOfOrg);
            sendToMainRestriction = ((Conjunction)sendToMainRestriction)
                    .add(Restrictions.in("orgOwner",allOrg))
                    .add(Restrictions.eq("sendAll",SendToAssociatedOrgs.SendToMain));

            Criterion sendToSelfRestriction = Restrictions.conjunction();
            sendToSelfRestriction = ((Conjunction)sendToSelfRestriction)
                    .add(Restrictions.eq("orgOwner",idOfOrg))
                    .add(Restrictions.eq("sendAll",SendToAssociatedOrgs.SendToSelf));

            /* собираем все условия в дизюнкцию */
            Criterion sendToAndOrgRestriction = Restrictions.disjunction()
                    .add(sendToAnyAllRestriction)
                    .add(sendToAllRestriction)
                    .add(sendToMainRestriction)
                    .add(sendToSelfRestriction);

            Criterion resultCriterion =  Restrictions.conjunction().add(sendToAndOrgRestriction);

            Criterion restrictionConfigProvider = null;
            if(classList.contains(IConfigProvider.class) && configurationProvider!=null){
                restrictionConfigProvider = Restrictions.eq("idOfConfigurationProvider",configurationProvider.getIdOfConfigurationProvider());
                ((Conjunction)resultCriterion).add(restrictionConfigProvider);
            }

            Criterion restrictionCurrentMaxVersion = null;
            if(currentMaxVersion != null){
                restrictionCurrentMaxVersion = Restrictions.gt("globalVersion",currentMaxVersion);
                ((Conjunction)resultCriterion).add(restrictionCurrentMaxVersion);
                // TODO: where = (where.equals("")?"": where + " and ") + " globalVersion>"+currentMaxVersion+ " and not (createVersion>"+currentMaxVersion+" and deletedState)";
            }

            criteria.add(resultCriterion);

            List list = criteria.list();
            if(!(list==null || list.isEmpty())){
                for (Object object: list){
                    DistributedObject distributedObject = (DistributedObject) object;
                    List clazzs = Arrays.asList(distributedObject.getClass().getInterfaces());
                    if(clazzs.contains(IConfigProvider.class)){
                         if (configurationProvider==null){
                             distributedObject.setDistributedObjectException(new DistributedObjectException("CONFIGURATION_PROVIDER_NOT_FOUND"));
                         }
                    }
                    result.add(distributedObject);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }catch (Exception e){
            // TODO: записать в журнал ошибок
            Org org = null;
            try {
                org = DAOUtils.getOrgReference(persistenceSession, idOfOrg);
                SyncHistoryException syncHistoryException = new SyncHistoryException(org, syncHistory, "Error generateResponseResult: " + e.getMessage());
                persistenceSession.save(syncHistoryException);
            } catch (Exception e1) {
                logger.error("createSyncHistory exception: ",e1);
            }
            logger.error("Error generateResponseResult: ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private DistributedObject updateDeleteState(DistributedObject distributedObject, Long currentMaxVersion) throws Exception {
        DistributedObject currentDO = doService.findByGuid(distributedObject.getClass(), distributedObject.getGuid());
        if (currentDO == null) {
            throw new DistributedObjectException(
                    distributedObject.getClass().getSimpleName() + " NOT_FOUND_VALUE : " + distributedObject.getGuid());
        }
        currentDO.setGlobalVersion(currentMaxVersion);
        currentDO.setDeletedState(true);
        currentDO.setDeleteDate(new Date());
        return doService.update(currentDO);
    }

    private DistributedObject processDistributedObject(DistributedObject distributedObject, Long currentMaxVersion) throws Exception {
        DistributedObject currentDO = doService.findByGuid(distributedObject.getClass(), distributedObject.getGuid());
        // Создание в БД нового экземпляра РО.
        if (distributedObject.getTagName().equals("C")) {
            if (currentDO != null) {
                throw new DistributedObjectException(
                        distributedObject.getClass().getSimpleName() + " DUPLICATE_GUID : " + distributedObject.getGuid());
            }
            distributedObject.setGlobalVersion(currentMaxVersion);
            distributedObject.setGlobalVersionOnCreate(currentMaxVersion);
            distributedObject = doService.createDO(distributedObject);
            distributedObject.setTagName("C");
        }
        // Изменение существующего в БД экземпляра РО.
        if (distributedObject.getTagName().equals("M")) {
            if (currentDO == null)
                throw new DistributedObjectException(
                        distributedObject.getClass().getSimpleName() + " NOT_FOUND_VALUE : " + distributedObject.getGuid());
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
            currentDO.setGlobalVersion(currentVersion);
            distributedObject = doService.mergeDO(currentDO, doConflict);
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
