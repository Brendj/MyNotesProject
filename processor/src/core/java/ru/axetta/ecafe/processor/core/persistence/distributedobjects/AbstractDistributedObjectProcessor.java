/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
 * Time: 1:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDistributedObjectProcessor {

    @PersistenceContext
    EntityManager entityManager;

    public abstract void process(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg,
            Document document);

    protected void processDistributedObject(DistributedObject distributedObject, long currentMaxVersion, Long idOfOrg,
            Document document) throws Exception {
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
                distributedObject = DAOService.getInstance().mergeDistributedObject(distributedObject, objectVersion);
                distributedObject.setTagName("M");
            }
        }
    }

    protected DistributedObject createDistributedObject(DistributedObject distributedObject, long currentVersion)
            throws DistributedObjectException {
        long id = getGlobalIDByGUID(distributedObject);
        if (id > 0) {
            throw new DistributedObjectException(1);
        }
        distributedObject.setCreatedDate(new Date());
        distributedObject.setGlobalVersion(currentVersion);
        return entityManager.merge(distributedObject);
    }

    protected long getGlobalIDByGUID(DistributedObject distributedObject) {
        TypedQuery<Long> query = null;
        try {
            query = entityManager.createQuery(
                    "select id from " + distributedObject.getClass().getSimpleName() + " where guid='"
                            + distributedObject.getGuid() + "'", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean updateDeleteState(DistributedObject distributedObject) {
        StringBuilder stringQuery = new StringBuilder("update ");
        stringQuery.append(distributedObject.getClass().getSimpleName());
        stringQuery.append(" set deletedState=:deletedState where guid='");
        stringQuery.append(distributedObject.getGuid());
        stringQuery.append("'");
        Query q = entityManager.createQuery(stringQuery.toString());
        q.setParameter("deletedState", true);
        return (q.executeUpdate() != 0);
    }

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
