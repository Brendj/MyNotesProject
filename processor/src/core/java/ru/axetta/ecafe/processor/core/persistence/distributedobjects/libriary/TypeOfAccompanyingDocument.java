/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 17.08.12
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class TypeOfAccompanyingDocument extends DistributedObject {

    private long idOfTypeOfAccompanyingDocument;
    private String typeOfAccompanyingDocumentName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public TypeOfAccompanyingDocument parseAttributes(Node node) throws Exception{

        String typeOfAccompanyingDocumentName = getStringAttributeValue(node, "typeOfAccompanyingDocumentName", 1024);
        if (typeOfAccompanyingDocumentName != null) {
            setTypeOfAccompanyingDocumentName(typeOfAccompanyingDocumentName);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {

        setTypeOfAccompanyingDocumentName(
                ((TypeOfAccompanyingDocument) distributedObject).getTypeOfAccompanyingDocumentName());
    }

    public long getIdOfTypeOfAccompanyingDocument() {
        return idOfTypeOfAccompanyingDocument;
    }

    public void setIdOfTypeOfAccompanyingDocument(long idOfTypeOfAccompanyingDocument) {
        this.idOfTypeOfAccompanyingDocument = idOfTypeOfAccompanyingDocument;
    }

    public String getTypeOfAccompanyingDocumentName() {
        return typeOfAccompanyingDocumentName;
    }

    public void setTypeOfAccompanyingDocumentName(String typeOfAccompanyingDocumentName) {
        this.typeOfAccompanyingDocumentName = typeOfAccompanyingDocumentName;
    }

    @Override
    public String toString() {
        return "TypeOfAccompanyingDocument{" +
                "idOfTypeOfAccompanyingDocument=" + idOfTypeOfAccompanyingDocument +
                ", typeOfAccompanyingDocumentName='" + typeOfAccompanyingDocumentName + '\'' +
                '}';
    }
}
