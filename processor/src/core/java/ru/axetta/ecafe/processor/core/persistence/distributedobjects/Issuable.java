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

    private Long barcode;
    private char type;
    private Instance instance;
    private JournalItem journalItem;

    private String guidInstance;
    private String guidJournalItem;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
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

        guidInstance = getStringAttributeValue(node, "guidInstance", 1024);
        guidJournalItem = getStringAttributeValue(node, "guidJournalItem", 1024);
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setInstance(daoService.findDistributedObjectByRefGUID(Instance.class, guidInstance));
        setJournalItem(daoService.findDistributedObjectByRefGUID(JournalItem.class, guidJournalItem));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setBarcode(((Issuable) distributedObject).getBarcode());
        setType(((Issuable) distributedObject).getType());
        setBarcode(((Issuable) distributedObject).getBarcode());
        setType((((Issuable) distributedObject).getType()));
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public JournalItem getJournalItem() {
        return journalItem;
    }

    public void setJournalItem(JournalItem journalItem) {
        this.journalItem = journalItem;
    }

    @Override
    public String toString() {
        return "Issuable{" +
                "barcode=" + barcode +
                ", type=" + type +
                ", instance=" + instance +
                ", journalItem=" + journalItem +
                '}';
    }
}
