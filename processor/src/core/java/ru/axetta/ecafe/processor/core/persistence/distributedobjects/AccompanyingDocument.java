/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public AccompanyingDocument parseAttributes(Node node) {

        guidTypeOfAccompanyingDocument = getStringAttributeValue(node, "guidTypeOfAccompanyingDocument", 1024);
        guidSource = getStringAttributeValue(node, "guidSource", 1024);
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setTypeOfAccompanyingDocument(daoService
                .findDistributedObjectByRefGUID(TypeOfAccompanyingDocument.class, guidTypeOfAccompanyingDocument));
        setSource(daoService.findDistributedObjectByRefGUID(Source.class, guidSource));
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
