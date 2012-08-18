/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 10.07.12
 * Time: 21:07
 * To change this template use File | Settings | File Templates.
 */
public class Circulation extends DistributedObject {

    public final static int ISSUED = 0, EXTENDED = 1, LOST = 2, REFUNDED = 3;

    private Circulation parentCirculation;
    private Reader reader;
    private Issuable issuable;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int status;

    String guidParentCirculation;
    String guidReader;
    String guidIssuable;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    protected Circulation parseAttributes(Node node) throws ParseException {

        guidParentCirculation = getStringAttributeValue(node, "guidParentCirculation", 1024);
        guidReader = getStringAttributeValue(node, "guidReader", 1024);
        guidIssuable = getStringAttributeValue(node, "GUIDIssuable", 1024);

        issuanceDate = getDateAttributeValue(node, "issuanceDate");
        refundDate = getDateAttributeValue(node, "refundDate");
        realRefundDate = getDateAttributeValue(node, "realRefundDate");
        status = getIntegerAttributeValue(node, "status");
        return this;
    }

    @Override
    public void preProcess() {
        DAOService daoService = DAOService.getInstance();
        setIssuable(daoService.findDistributedObjectByRefGUID(Issuable.class, guidIssuable));
        setParentCirculation(daoService.findDistributedObjectByRefGUID(Circulation.class, guidParentCirculation));
        setReader(daoService.findDistributedObjectByRefGUID(Reader.class, guidReader));
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIssuanceDate(((Circulation) distributedObject).getIssuanceDate());
        setRefundDate(((Circulation) distributedObject).getRefundDate());
        setRealRefundDate(((Circulation) distributedObject).getRealRefundDate());
        setStatus(((Circulation) distributedObject).getStatus());
        setParentCirculation(((Circulation) distributedObject).getParentCirculation());
        setIssuable(((Circulation) distributedObject).getIssuable());
        setReader(((Circulation) distributedObject).getReader());
    }

    public Circulation getParentCirculation() {
        return parentCirculation;
    }

    public void setParentCirculation(Circulation parentCirculation) {
        this.parentCirculation = parentCirculation;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Issuable getIssuable() {
        return issuable;
    }

    public void setIssuable(Issuable issuable) {
        this.issuable = issuable;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public Date getRealRefundDate() {
        return realRefundDate;
    }

    public void setRealRefundDate(Date realRefundDate) {
        this.realRefundDate = realRefundDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
