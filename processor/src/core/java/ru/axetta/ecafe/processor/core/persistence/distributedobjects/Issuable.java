/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 13.07.12
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class Issuable extends DistributedObject {

    private long barcode;
    private char type;
    private Publication publication;

    private String guidPublication;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "barcode", barcode);
        setAttribute(element, "type", type);
        setAttribute(element, "publicationid", publication.getGlobalId());
    }

    @Override
    protected Issuable parseAttributes(Node node) throws ParseException {
        Long longBarCode = getLongAttributeValue(node, "barcode");
        if (longBarCode != null) {
            setBarcode(longBarCode);
        }
        Character charType = getCharacterAttributeValue(node, "type");
        if (charType != null) {
            setType(charType);
        }


        guidPublication = getStringAttributeValue(node, "GUIDPublication", 36);
        return this;
    }

    @Override
    public void preProcess() {
        setPublication(DAOService.getInstance().findDistributedObjectByRefGUID(Publication.class, guidPublication));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setBarcode(((Issuable) distributedObject).getBarcode());
        setType(((Issuable) distributedObject).getType());
        setPublication(((Issuable) distributedObject).getPublication());
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }
}
