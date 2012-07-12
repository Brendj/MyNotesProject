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
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 12.07.12
 * Time: 1:12
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("prototype")
public class DefaultDistributedObjectProcessor implements IDistributedObjectProcessor {

    @PersistenceContext
    EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(DefaultDistributedObjectProcessor.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg, Document document) {
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
                        createConflict(distributedObject, idOfOrg, document);
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

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Transactional
    private void createConflict(DistributedObject distributedObject, Long idOfOrg, Document document) throws Exception {
        DOConflict conflict = new DOConflict();
        conflict.setValueInc(createStringElement(document, distributedObject));
        conflict.setgVersionInc(distributedObject.getGlobalVersion());
        conflict.setIdOfOrg(idOfOrg);
        conflict.setDistributedObjectClassName(distributedObject.getClass().getSimpleName());
        TypedQuery<DistributedObject> query = entityManager.createQuery(
                "from " + distributedObject.getClass().getSimpleName() + " where guid='" + distributedObject.getGuid()
                        + "'", DistributedObject.class);
        DistributedObject currDistributedObject = query.getSingleResult();
        conflict.setgVersionCur(currDistributedObject.getGlobalVersion());
        conflict.setValueCur(createStringElement(document, currDistributedObject));
        conflict.setCreateConflictDate(new Date());
        entityManager.persist(conflict);
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
}
