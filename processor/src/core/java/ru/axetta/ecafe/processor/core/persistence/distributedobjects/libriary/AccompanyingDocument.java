/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
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
public class AccompanyingDocument extends DistributedObject {

    private TypeOfAccompanyingDocument typeOfAccompanyingDocument;
    private String accompanyingDocumentNumber;
    private Source source;

    private String guidTypeOfAccompanyingDocument;
    private String guidSource;
    private Set<Ksu1Record> ksu1RecordInternal;

    public Set<Ksu1Record> getKsu1RecordInternal() {
        return ksu1RecordInternal;
    }

    public void setKsu1RecordInternal(Set<Ksu1Record> ksu1RecordInternal) {
        this.ksu1RecordInternal = ksu1RecordInternal;
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "AccompanyingDocumentNumber", accompanyingDocumentNumber);
        setAttribute(element, "GuidTypeOfAccompanyingDocument", guidTypeOfAccompanyingDocument);
        setAttribute(element, "GuidSource", guidSource);
    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException{
        Source s = (Source) DAOUtils.findDistributedObjectByRefGUID(session, guidSource);
        if(s==null){
            DistributedObjectException distributedObjectException = new DistributedObjectException("Source NOT_FOUND_VALUE");
            distributedObjectException.setData(guidSource);
            throw distributedObjectException;
        } else {
            setSource(s);
        }
        TypeOfAccompanyingDocument tad = (TypeOfAccompanyingDocument) DAOUtils.findDistributedObjectByRefGUID(session, guidTypeOfAccompanyingDocument);
        if(tad==null) {
            DistributedObjectException distributedObjectException = new DistributedObjectException("TypeOfAccompanyingDocument NOT_FOUND_VALUE");
            distributedObjectException.setData(guidTypeOfAccompanyingDocument);
            throw distributedObjectException;
        } else {
            setTypeOfAccompanyingDocument(tad);
        }
    }

    @Override
    public AccompanyingDocument parseAttributes(Node node) throws Exception{

        String stringAccompanyingDocumentNumber = getStringAttributeValue(node,"AccompanyingDocumentNumber",32);
        if(stringAccompanyingDocumentNumber!=null) {
            setAccompanyingDocumentNumber(stringAccompanyingDocumentNumber);
        }

        guidTypeOfAccompanyingDocument = getStringAttributeValue(node, "GuidTypeOfAccompanyingDocument", 36);
        guidSource = getStringAttributeValue(node, "GuidSource", 36);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setSource(((AccompanyingDocument) distributedObject).getSource());
        setTypeOfAccompanyingDocument(((AccompanyingDocument) distributedObject).getTypeOfAccompanyingDocument());
        setAccompanyingDocumentNumber(((AccompanyingDocument) distributedObject).getAccompanyingDocumentNumber());
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
}
