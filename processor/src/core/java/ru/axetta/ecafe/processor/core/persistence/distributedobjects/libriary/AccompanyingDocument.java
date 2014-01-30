/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 17.08.12
 * Time: 19:32
 * To change this template use File | Settings | File Templates.
 */
public class AccompanyingDocument extends LibraryDistributedObject {

    private TypeOfAccompanyingDocument typeOfAccompanyingDocument;
    private String accompanyingDocumentNumber;
    private Source source;

    private String guidTypeOfAccompanyingDocument;
    private String guidSource;
    private Set<Ksu1Record> ksu1RecordInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("source","s", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("typeOfAccompanyingDocument","t", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("accompanyingDocumentNumber"), "accompanyingDocumentNumber");

        projectionList.add(Projections.property("t.guid"), "guidTypeOfAccompanyingDocument");
        projectionList.add(Projections.property("s.guid"), "guidSource");

        criteria.setProjection(projectionList);
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "AccompanyingDocumentNumber", accompanyingDocumentNumber);
        XMLUtils.setAttributeIfNotNull(element, "GuidTypeOfAccompanyingDocument", guidTypeOfAccompanyingDocument);
        XMLUtils.setAttributeIfNotNull(element, "GuidSource", guidSource);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException{
        Source s = DAOUtils.findDistributedObjectByRefGUID(Source.class, session, guidSource);
        if(s==null){
            DistributedObjectException distributedObjectException = new DistributedObjectException("Source NOT_FOUND_VALUE");
            distributedObjectException.setData(guidSource);
            throw distributedObjectException;
        } else {
            setSource(s);
        }
        TypeOfAccompanyingDocument tad = DAOUtils.findDistributedObjectByRefGUID(TypeOfAccompanyingDocument.class, session, guidTypeOfAccompanyingDocument);
        if(tad==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("TypeOfAccompanyingDocument NOT_FOUND_VALUE");
            distributedObjectException.setData(guidTypeOfAccompanyingDocument);
            throw distributedObjectException;
        } else {
            setTypeOfAccompanyingDocument(tad);
        }
    }

    @Override
    public AccompanyingDocument parseAttributes(Node node) throws Exception {
        String stringAccompanyingDocumentNumber = XMLUtils.getStringAttributeValue(node, "AccompanyingDocumentNumber", 32);
        if (stringAccompanyingDocumentNumber != null) {
            setAccompanyingDocumentNumber(stringAccompanyingDocumentNumber);
        }
        guidTypeOfAccompanyingDocument = XMLUtils.getStringAttributeValue(node, "GuidTypeOfAccompanyingDocument", 36);
        guidSource = XMLUtils.getStringAttributeValue(node, "GuidSource", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setAccompanyingDocumentNumber(((AccompanyingDocument) distributedObject).getAccompanyingDocumentNumber());
        setTypeOfAccompanyingDocument(((AccompanyingDocument) distributedObject).getTypeOfAccompanyingDocument());
        setGuidTypeOfAccompanyingDocument(((AccompanyingDocument) distributedObject).getGuidTypeOfAccompanyingDocument());
        setSource(((AccompanyingDocument) distributedObject).getSource());
        setGuidSource(((AccompanyingDocument) distributedObject).getGuidSource());
    }

    public TypeOfAccompanyingDocument getTypeOfAccompanyingDocument() {
        return typeOfAccompanyingDocument;
    }

    public void setTypeOfAccompanyingDocument(TypeOfAccompanyingDocument typeOfAccompanyingDocument) {
        this.typeOfAccompanyingDocument = typeOfAccompanyingDocument;
    }

    public String getAccompanyingDocumentNumber() {
        return accompanyingDocumentNumber;
    }

    public void setAccompanyingDocumentNumber(String accompanyingDocumentNumber) {
        this.accompanyingDocumentNumber = accompanyingDocumentNumber;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Set<Ksu1Record> getKsu1RecordInternal() {
        return ksu1RecordInternal;
    }

    public void setKsu1RecordInternal(Set<Ksu1Record> ksu1RecordInternal) {
        this.ksu1RecordInternal = ksu1RecordInternal;
    }

    public String getGuidTypeOfAccompanyingDocument() {
        return guidTypeOfAccompanyingDocument;
    }

    public void setGuidTypeOfAccompanyingDocument(String guidTypeOfAccompanyingDocument) {
        this.guidTypeOfAccompanyingDocument = guidTypeOfAccompanyingDocument;
    }

    public String getGuidSource() {
        return guidSource;
    }

    public void setGuidSource(String guidSource) {
        this.guidSource = guidSource;
    }
}
