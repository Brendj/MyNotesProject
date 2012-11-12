/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.DateFormat;
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
    private Map<DistributedObjectsEnum, List<DistributedObject>> resultDistributedObjectsListMap = new HashMap<DistributedObjectsEnum, List<DistributedObject>>();
    /**
     * Список глобальных объектов на базе процессинга
     */
    /* Пара ключ значение ключ имя класса значение список объектов этого класса */
    private Map<DistributedObjectsEnum, List<DistributedObject>> distributedObjectsListMap = new HashMap<DistributedObjectsEnum, List<DistributedObject>>();
    private List<DOConfirm> confirmDistributedObject = new ArrayList<DOConfirm>();
    private Document document;
    private Long idOfOrg;
    private final DateFormat dateOnlyFormat;
    private final DateFormat timeFormat;

    public Manager(DateFormat dateOnlyFormat, DateFormat timeFormat){
        this.dateOnlyFormat = dateOnlyFormat;
        this.timeFormat = timeFormat;
    }

    /**
     * Берет информацию из элемента <RO> входного xml документа. Выполняет действия, указанные в этом элементе
     * (create, update). При успехе выполнения действия формируется объект класса DistributedObjectItem и сохраняется
     * в список - поле distributedObjectItems.
     *
     * @param node Элемент <RO>
     * @throws Exception
     */
    public void build(Node node) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            if (logger.isDebugEnabled()) {
                logger.debug("RO parse XML section");
            }
            if(node.getNodeName().equals("Confirm")){
                if (logger.isDebugEnabled()) {
                    logger.debug("RO parse Confirm XML section");
                }
                Node childNode = node.getFirstChild();
                while (childNode != null) {
                    buildConfirm(childNode);
                    childNode = childNode.getNextSibling();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("RO end parse Confirm XML section");
                }
            } else {
                DistributedObjectsEnum currentObject = null;
                currentObject = DistributedObjectsEnum.parse(node.getNodeName());
                if(currentObject != null){
                    if (logger.isDebugEnabled()) {
                        logger.debug("RO parse '"+currentObject.name()+"' node XML section");
                    }
                    currentMaxVersions.put(currentObject.name(), Long.parseLong(getAttributeValue(node, "V")));
                    node = node.getFirstChild();
                    while (node != null) {
                        if (Node.ELEMENT_NODE == node.getNodeType()) {
                            DistributedObject distributedObject = createDistributedObject(currentObject);
                            distributedObject.setTimeFormat(timeFormat);
                            distributedObject.setDateOnlyFormat(dateOnlyFormat);
                            try{
                                distributedObject = distributedObject.build(node);
                            } catch (DistributedObjectException e){
                                distributedObject.setDistributedObjectException(e);
                                logger.error(distributedObject.toString(), e);
                            } catch (Exception e){
                                //distributedObject.setErrorType(DistributedObjectException.ErrorType.UNKNOWN_ERROR);
                                distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
                                logger.error(distributedObject.toString(), e);
                            }
                            if (!distributedObjectsListMap.containsKey(currentObject)) {
                                distributedObjectsListMap.put(currentObject, new ArrayList<DistributedObject>());
                            }
                            distributedObjectsListMap.get(currentObject).add(distributedObject);
                        }
                        node = node.getNextSibling();
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("RO end parse '"+currentObject.name()+"' node XML section");
                    }
                } else {
                    logger.warn(String.format("Section '%s' processing is not known",node.getNodeName()));
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("RO end parse XML section");
            }
        }

    }

    public Element toElement(Document document) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("RO section begin generate XML node");
        }
        Element elementRO = document.createElement("RO");
        Element confirmElement = document.createElement("Confirm");
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        String tagName;
        List<DistributedObject> distributedObjects = null;
        /* generate confirm element */
        if (logger.isDebugEnabled()) {
            logger.debug("Generate confirm element");
        }
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
                distributedObject.setTimeFormat(timeFormat);
                distributedObject.setDateOnlyFormat(dateOnlyFormat);
                elementMap.get(tagName).appendChild(distributedObject.toConfirmElement(element));
            }
            elementRO.appendChild(confirmElement);
            elementMap.clear();
            distributedObjects.clear();
        }
        /* generate result objects */
        if (logger.isDebugEnabled()) {
            logger.debug("Generate result objects.");
        }
        for (DistributedObjectsEnum key : resultDistributedObjectsListMap.keySet()) {
            distributedObjects = resultDistributedObjectsListMap.get(key);
            for (DistributedObject distributedObject : distributedObjects) {
                tagName = DistributedObjectsEnum.parse(distributedObject.getClass()).name();
                if (!elementMap.containsKey(tagName)) {
                    Element distributedObjectElement = document.createElement(tagName);
                    elementRO.appendChild(distributedObjectElement);
                    elementMap.put(tagName, distributedObjectElement);
                }
                Element element = document.createElement("O");
                distributedObject.setDateOnlyFormat(dateOnlyFormat);
                distributedObject.setTimeFormat(timeFormat);
                elementMap.get(tagName).appendChild(distributedObject.toElement(element));
            }
            elementMap.clear();
            distributedObjects.clear();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Complete manager generate XML node.");
        }
        return elementRO;
    }

    public void process(SessionFactory sessionFactory) {
        if (logger.isDebugEnabled()) {
            logger.debug("RO begin process section");
        }
        DistributedObjectsEnumComparator distributedObjectsEnumComparator = new DistributedObjectsEnumComparator();
        DistributedObjectsEnum[] array = DistributedObjectsEnum.values();
        Arrays.sort(array, distributedObjectsEnumComparator);
        clearConfirmTable(sessionFactory);
        Map<DistributedObjectsEnum, List<DistributedObject>> currentDistributedObjectsListMap = new HashMap<DistributedObjectsEnum, List<DistributedObject>>();
        for (DistributedObjectsEnum anArray : array) {
            List<DistributedObject> currentResultDistributedObjectsList = generateResponseResult(sessionFactory, anArray.getValue(), currentMaxVersions.get(anArray.getValue().getSimpleName()));
            resultDistributedObjectsListMap.put(anArray, currentResultDistributedObjectsList);
            if (!(distributedObjectsListMap.get(anArray) == null || distributedObjectsListMap.get(anArray).isEmpty())) {
                List<DistributedObject> distributedObjectsList = processDistributedObjectsList(sessionFactory,
                        distributedObjectsListMap.get(anArray), anArray);
                currentDistributedObjectsListMap.put(anArray, distributedObjectsList);
            }
            addConfirms(sessionFactory, currentResultDistributedObjectsList);
            List<DistributedObject> currentConfirmDistributedObjectsList = generateConfirmResponseResult(sessionFactory, anArray.getValue());
            if(!(currentConfirmDistributedObjectsList == null || currentConfirmDistributedObjectsList.isEmpty())){
                for (DistributedObject distributedObject: currentConfirmDistributedObjectsList){
                    if(!currentResultDistributedObjectsList.contains(distributedObject)){
                        currentResultDistributedObjectsList.add(distributedObject);
                    }
                }
            }
        }
        distributedObjectsListMap = currentDistributedObjectsListMap;
        if (logger.isDebugEnabled()) {
            logger.debug("RO end process section");
        }
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    private void buildConfirm(Node node) throws Exception {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            DistributedObjectsEnum currentObject = DistributedObjectsEnum.parse(node.getNodeName());
            DOConfirm confirm = new DOConfirm();
            confirm.setDistributedObjectClassName(currentObject.name());
            confirm.setGuid(node.getAttributes().getNamedItem("Guid").getTextContent());
            confirm.setOrgOwner(idOfOrg);
            confirmDistributedObject.add(confirm);
        }
    }

    private void addConfirms(SessionFactory sessionFactory, List<DistributedObject> confirmDistributedObjectList){
        for (DistributedObject distributedObject: confirmDistributedObjectList){
            DOConfirm doConfirm = new DOConfirm();
            doConfirm.setDistributedObjectClassName(DistributedObjectsEnum.parse(distributedObject.getClass()).name());
            doConfirm.setGuid(distributedObject.getGuid());
            doConfirm.setOrgOwner(idOfOrg);
            addConfirm(sessionFactory, doConfirm);
        }
    }

    private void addConfirm(SessionFactory sessionFactory, DOConfirm doConfirm){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Query query = persistenceSession.createQuery("from DOConfirm where orgOwner=:orgOwner and distributedObjectClassName=:distributedObjectClassName and guid=:guid");
            query.setParameter("orgOwner",doConfirm.getOrgOwner());
            query.setParameter("distributedObjectClassName", doConfirm.getDistributedObjectClassName());
            query.setParameter("guid",doConfirm.getGuid());
            List list = query.list();
            if(list==null || list.isEmpty()){
                persistenceSession.save(doConfirm);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private List<DistributedObject> processDistributedObjectsList(SessionFactory sessionFactory, List<DistributedObject> distributedObjects, DistributedObjectsEnum objectClass){
        List<DistributedObject> distributedObjectList = new ArrayList<DistributedObject>(0);
        if(!(distributedObjects==null || distributedObjects.isEmpty())){
            // Все объекты одного типа получают одну (новую) версию и все их изменения пишуться с этой версией.
            Long currentMaxVersion = updateVersionByDistributedObjects(sessionFactory, objectClass.name());
            for (DistributedObject distributedObject : distributedObjects) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Process: "+distributedObject.toString());
                }
                DistributedObject currentDistributedObject = processCurrentObject(sessionFactory, distributedObject, currentMaxVersion);
                distributedObjectList.add(currentDistributedObject);
            }
            /* generate result list */
            List<DistributedObject> currentResultDistributedObjectsList = resultDistributedObjectsListMap.get(objectClass);
            /* уберем все объекты которые есть в конфирме */
            if(!(currentResultDistributedObjectsList == null || currentResultDistributedObjectsList.isEmpty())){
                currentResultDistributedObjectsList.removeAll(distributedObjectList);
            }
            resultDistributedObjectsListMap.put(objectClass, currentResultDistributedObjectsList);
        }
        return distributedObjectList;
    }

    private DistributedObject processCurrentObject(SessionFactory sessionFactory,DistributedObject distributedObject,Long currentMaxVersion){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if(distributedObject.getDistributedObjectException()==null){
                if (!(distributedObject.getDeletedState()==null || distributedObject.getDeletedState())) {
                    distributedObject.preProcess(persistenceSession);
                    if(distributedObject instanceof IConfigProvider){
                        ConfigurationProvider configurationProvider = getConfigurationProvider(persistenceSession, distributedObject.getClass());
                        ((IConfigProvider) distributedObject).setIdOfConfigurationProvider(configurationProvider.getIdOfConfigurationProvider());
                    }
                    distributedObject = processDistributedObject(persistenceSession, distributedObject, currentMaxVersion);
                } else {
                    String tagName = distributedObject.getTagName();
                    distributedObject = updateDeleteState(persistenceSession, distributedObject, currentMaxVersion);
                    distributedObject.setTagName(tagName);
                    distributedObject.setDateOnlyFormat(dateOnlyFormat);
                    distributedObject.setTimeFormat(timeFormat);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (DistributedObjectException e){
            // Произошла ошибка при обрабоке одного объекта - нужно как то сообщить об этом пользователю
            //distributedObject.setError(e.getErrorType());
            distributedObject.setDistributedObjectException(e);
            logger.error(distributedObject.toString(), e);
        } catch (Exception e){
            //distributedObject.setError(DistributedObjectException.ErrorType.UNKNOWN_ERROR);
            distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
            logger.error(distributedObject.toString(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return distributedObject;
    }

    //private List<Long> getListIdOfOrgList(Session session){
    //    List<Long> resultList = new LinkedList<Long>();
    //    Query query = session.createQuery("select idOfDestOrg from MenuExchangeRule where idOfSourceOrg=:idOfOrg");
    //    query.setParameter("idOfOrg",idOfOrg);
    //    List list = query.list();
    //    if(!(list==null || list.isEmpty())){
    //        for (Object object: list){
    //            resultList.add((Long) object);
    //        }
    //    }
    //    query = session.createQuery("select idOfSourceOrg from MenuExchangeRule where idOfDestOrg=:idOfOrg");
    //    query.setParameter("idOfOrg",idOfOrg);
    //    list = query.list();
    //    if(!(list==null || list.isEmpty())){
    //        for (Object object: list){
    //            resultList.add((Long) object);
    //        }
    //    }
    //    return resultList;
    //}

    private ConfigurationProvider getConfigurationProvider(Session session, Class<? extends DistributedObject> clazz) throws Exception{
        List classList = Arrays.asList(clazz.getInterfaces());
        ConfigurationProvider configurationProvider = null;
        if(classList.contains(IConfigProvider.class)){
            Org org = DAOUtils.findOrg(session,idOfOrg);
            configurationProvider = org.getConfigurationProvider();
            /* Если есть конфигурация синхронизируемой организации */
            if(configurationProvider==null){
                Query query = session.createQuery("from MenuExchangeRule where idOfDestOrg=:idOfOrg");
                query.setParameter("idOfOrg",idOfOrg);
                List list = query.list();
                if(!(list == null || list.isEmpty())){
                    Org sourceOrg = DAOUtils.findOrg(session, ((MenuExchangeRule) list.get(0)).getIdOfSourceOrg());
                    if(sourceOrg != null){
                        configurationProvider = sourceOrg.getConfigurationProvider();
                    }
                }
            }
            if(configurationProvider == null) {
                //return new ArrayList<DistributedObject>(0);
                // При выбрасывании исключения падает вся синхронизация как быть с библиотекой?
                throw new DistributedObjectException("CONFIGURATION_PROVIDER_NOT_FOUND");
            }
        }
        return configurationProvider;
    }

    private List<DistributedObject> generateResponseResult(SessionFactory sessionFactory, Class<? extends DistributedObject> clazz, Long currentMaxVersion){
        List<DistributedObject> result = new LinkedList<DistributedObject>();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List classList = Arrays.asList(clazz.getInterfaces());
            String where = "";
            if(classList.contains(IConfigProvider.class)){
                ConfigurationProvider configurationProvider = getConfigurationProvider(persistenceSession, clazz);
                where = " idOfConfigurationProvider="+configurationProvider.getIdOfConfigurationProvider();
            }
            // вытянем номер организации поставщика если есть.
            List<Long> menuExchangeRuleList = DAOUtils.getListIdOfOrgList(persistenceSession, idOfOrg);
            String whereOrgSource = "";
            if(!(menuExchangeRuleList == null || menuExchangeRuleList.isEmpty() || menuExchangeRuleList.get(0)==null)){
                whereOrgSource = " orgOwner in ("+  menuExchangeRuleList.toString().replaceAll("[^0-9,]","") + ", "+idOfOrg+")";
            } else{
                whereOrgSource = " orgOwner = "+idOfOrg;
            }
            where = (where.equals("")?"(" + whereOrgSource + " or orgOwner is null )": where + " and  (" + whereOrgSource + " or orgOwner is null )")+" ";
            if(currentMaxVersion != null){
                where = (where.equals("")?"": where + " and ") + " globalVersion>"+currentMaxVersion;
                // TODO: where = (where.equals("")?"": where + " and ") + " globalVersion>"+currentMaxVersion+ " and not (createVersion>"+currentMaxVersion+" and deletedState)";
            }
            String sendAllWhere = " (sendAll is null or sendAll=0) ";
            String select = "from " + clazz.getSimpleName() + (where.equals("")?"" + sendAllWhere:" where "+ sendAllWhere + " and " + where);
            Query query = persistenceSession.createQuery(select);
            List list = query.list();
            if(!(list==null || list.isEmpty())){
                for (Object object: list){
                    result.add((DistributedObject) object);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }catch (Exception e){
            logger.error("Error generateResponseResult: ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private List<DistributedObject> generateConfirmResponseResult(SessionFactory sessionFactory, Class<? extends DistributedObject> clazz){
        List<DistributedObject> result = new LinkedList<DistributedObject>();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Query query = persistenceSession.createQuery("Select guid from DOConfirm where orgOwner=:orgOwner and distributedObjectClassName=:distributedObjectClassName");
            query.setParameter("orgOwner",idOfOrg);
            query.setParameter("distributedObjectClassName",DistributedObjectsEnum.parse(clazz).name());
            List list = query.list();
            List<String> stringList = new LinkedList<String>();
            if(!(list==null || list.isEmpty())){
                for (Object object: list){
                    stringList.add((String) object);
                }
                Criteria criteria = persistenceSession.createCriteria(clazz);
                criteria.add(Restrictions.in("guid",stringList));
                List objects = criteria.list();
                if(!(objects==null || objects.isEmpty())){
                    for (Object object: objects){
                        result.add((DistributedObject) object);
                    }
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        }catch (Exception e){
            logger.error("Error getDistributedObjects: ",e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private Long getDistributedObjectVersion(Session session, DistributedObject distributedObject) throws Exception{
        String where = String.format("from %s where guid = '%s'",distributedObject.getClass().getSimpleName(),distributedObject.getGuid());
        Query query = session.createQuery(where);
        List list = query.list();
        if(list==null || list.isEmpty()){
            throw new DistributedObjectException(distributedObject.getClass().getSimpleName()+" NOT_FOUND_VALUE : "+distributedObject.getGuid());
        }
        return ((DistributedObject)list.get(0)).getGlobalVersion();
    }

    private DistributedObject updateDeleteState(Session session, DistributedObject distributedObject,Long currentMaxVersion) throws Exception{
        Long id = getGlobalIDByGUID(session, distributedObject);
        if (id < 0) {
            throw new DistributedObjectException(distributedObject.getClass().getSimpleName()+" NOT_FOUND_VALUE : "+distributedObject.getGuid());
        }
        DistributedObject object = (DistributedObject) session.get(distributedObject.getClass(), id);
        object.setGlobalVersion(currentMaxVersion);
        object.setDeletedState(true);
        object.setDeleteDate(new Date());
        session.save(object);
        return (DistributedObject) session.get(distributedObject.getClass(), id);
    }

    private DistributedObject processDistributedObject(Session session,DistributedObject distributedObject, long currentMaxVersion) throws Exception {
        if (distributedObject.getTagName().equals("C")) {
            distributedObject = createDistributedObject(session, distributedObject, currentMaxVersion);
            distributedObject.setTagName("C");
        }
        if (distributedObject.getTagName().equals("M")) {
            Long currentVersion = getDistributedObjectVersion(session, distributedObject);
            Long objectVersion = distributedObject.getGlobalVersion();
            if (!objectVersion.equals(currentVersion)) {
                createConflict(session, distributedObject, currentMaxVersion);
            }
            distributedObject = mergeDistributedObject(session, distributedObject, currentMaxVersion);
            distributedObject.setTagName("M");
        }
        return distributedObject;
    }

    private void createConflict(Session session, DistributedObject distributedObject, Long currentVersion) throws Exception {
        DOConflict conflict = new DOConflict();
        conflict.setValueInc(createStringElement(getSimpleDocument(), distributedObject));
        conflict.setgVersionInc(distributedObject.getGlobalVersion());
        conflict.setIdOfOrg(idOfOrg);
        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
        String where = String.format("from %s where guid='%s'",distributedObject.getClass().getSimpleName(),distributedObject.getGuid());
        Query query = session.createQuery(where);
        List list = query.list();
        DistributedObject currDistributedObject = null;
        if(!(list == null || list.isEmpty())){
            currDistributedObject = (DistributedObject) list.get(0);
            conflict.setgVersionCur(currDistributedObject.getGlobalVersion());
            conflict.setgVersionResult(currentVersion);
            conflict.setValueCur(createStringElement(getSimpleDocument(), currDistributedObject));
            conflict.setCreateConflictDate(new Date());
            session.persist(conflict);
        }
    }

    private DistributedObject mergeDistributedObject(Session session, DistributedObject distributedObject, long currentVersion) throws Exception{
        long id = getGlobalIDByGUID(session,distributedObject);
        if (id < 0) {
            throw new DistributedObjectException(distributedObject.getClass().getSimpleName()+" NOT_FOUND_VALUE : "+distributedObject.getGuid());
            //throw new DistributedObjectException("NOT_FOUND_VALUE");
        }
        DistributedObject object = (DistributedObject) session.get(distributedObject.getClass(), id);
        object.fill(distributedObject);
        object.setDeletedState(distributedObject.getDeletedState());
        object.setLastUpdate(new Date());
        object.setGlobalVersion(currentVersion);
        return (DistributedObject) session.merge(object);
    }

    private DistributedObject createDistributedObject(Session session, DistributedObject distributedObject, long currentVersion)
            throws DistributedObjectException {
        long id = getGlobalIDByGUID(session,distributedObject);
        if (id > 0) {
            throw new DistributedObjectException(distributedObject.getClass().getSimpleName()+" DUPLICATE_GUID : "+distributedObject.getGuid());
            //throw new DistributedObjectException(DistributedObjectException.ErrorType.DUPLICATE_VALUE);
        }
        distributedObject.setCreatedDate(new Date());
        distributedObject.setGlobalVersion(currentVersion);
        distributedObject.setDeletedState(distributedObject.getDeletedState());
        return (DistributedObject) session.merge(distributedObject);
    }

    private DistributedObject createDistributedObject(DistributedObjectsEnum distributedObjectsEnum) throws Exception {
        Class cl = distributedObjectsEnum.getValue();
        return (DistributedObject) cl.newInstance();
    }

    private String getAttributeValue(Node node, String attributeName) {
        return (node.getAttributes().getNamedItem(attributeName) != null ? node.getAttributes()
                .getNamedItem(attributeName).getTextContent() : null);
    }

    private Long updateVersionByDistributedObjects(SessionFactory sessionFactory, String name) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Long version = null;
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Query query = persistenceSession.createQuery("from DOVersion where UPPER(distributedObjectClassName)=:distributedObjectClassName");
            query.setParameter("distributedObjectClassName",name.toUpperCase());
            List list = query.list();
            DOVersion doVersion = null;
            if(list.isEmpty()) {
                doVersion = new DOVersion();
                doVersion.setCurrentVersion(0L);
                doVersion.setDistributedObjectClassName(name);
                version = 0L;
            } else {
                doVersion = (DOVersion) list.get(0);
                doVersion = (DOVersion) persistenceSession.merge(doVersion);
                version = doVersion.getCurrentVersion()+1;
                doVersion.setCurrentVersion(version);
            }
            persistenceSession.save(doVersion);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return version;
    }

    private void clearConfirmTable(SessionFactory sessionFactory){
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Begin clear confirm elements");
        }
        try {
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            for (DOConfirm confirm: confirmDistributedObject){
                String where = String.format("from DOConfirm where orgOwner=%d and UPPER(distributedObjectClassName)='%s' and guid='%s'",confirm.getOrgOwner(),confirm.getDistributedObjectClassName().toUpperCase(),confirm.getGuid());
                Query query = persistenceSession.createQuery(where);
                List list = query.list();
                for (Object object: list){
                    DOConfirm doConfirm = (DOConfirm) object;
                    persistenceSession.delete(doConfirm);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("End clear confirm elements");
        }
    }

    private long getGlobalIDByGUID(Session session, DistributedObject distributedObject) {
        Long result = -1L;
        String where = String.format("select id from %s where guid='%s'",distributedObject.getClass().getSimpleName(), distributedObject.getGuid());
        Query query = session.createQuery(where);
        List list = query.list();
        if(!(list == null || list.isEmpty())){
            result = (Long) list.get(0);
        }
        return result;
    }

    /* взять из XML Utills */
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

    private Document getSimpleDocument() throws Exception {
        if (document == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            document = factory.newDocumentBuilder().newDocument();
        }
        return document;
    }

}
