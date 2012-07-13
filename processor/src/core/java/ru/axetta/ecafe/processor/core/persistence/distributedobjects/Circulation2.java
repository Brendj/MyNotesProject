/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Publ;

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
public class Circulation2 extends DistributedObject {

    private Publication2 publication;
    private Org org;
    private Client client;
    private Date issuanceDate;
    private Date refundDate;
    private Date realRefundDate;
    private int quantity;

    private long idofpubl;
    private long idoforg;
    private long idofclient;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "issuanceDate", issuanceDate);
        setAttribute(element, "refundDate", refundDate);
        setAttribute(element, "realRefundDate", realRefundDate);
        setAttribute(element, "quantity", quantity);
    }

    @Override
    protected Circulation2 parseAttributes(Node node) throws ParseException {
        String stringIssuanceDate = getStringAttributeValue(node, "issuanceDate", 32);
        if (stringIssuanceDate != null) {
            setIssuanceDate(simpleDateFormat.parse(stringIssuanceDate));
        }
        String stringRefundDate = getStringAttributeValue(node, "refundDate", 32);
        if (stringRefundDate != null) {
            setRefundDate(simpleDateFormat.parse(stringRefundDate));
        }
        String stringRealRefundDate = getStringAttributeValue(node, "realRefundDate", 128);
        if (stringRealRefundDate != null) {
            setRealRefundDate(simpleDateFormat.parse(stringRealRefundDate));
        }
        String stringQuantity = getStringAttributeValue(node, "quantity", 512);
        if (stringQuantity != null) {
            setQuantity(Integer.parseInt(stringQuantity));
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIssuanceDate(((Circulation2) distributedObject).getIssuanceDate());
        setRefundDate(((Circulation2) distributedObject).getRefundDate());
        setRealRefundDate(((Circulation2) distributedObject).getRealRefundDate());
        setQuantity(((Circulation2) distributedObject).getQuantity());
    }

    public Publication2 getPublication() {
        return publication;
    }

    public void setPublication(Publication2 publication) {
        this.publication = publication;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public long getIdofpubl() {
        return idofpubl;
    }

    public void setIdofpubl(long idofpubl) {
        this.idofpubl = idofpubl;
    }

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }
}
