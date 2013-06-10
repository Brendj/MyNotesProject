/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class ActOfInventarization extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Date", getDateFormat().format(dateOfAct));
        setAttribute(element, "Number", number);
        setAttribute(element, "Commission", commission);
    }

    @Override
    protected ActOfInventarization parseAttributes(Node node) throws Exception{
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        Date dateOfActOfDifference = getDateTimeAttributeValue(node, "Date");
        if(dateOfActOfDifference != null) setDateOfAct(dateOfActOfDifference);
        String stringNumber = getStringAttributeValue(node, "Number",128);
        if(stringNumber != null) setNumber(stringNumber);
        String stringCommission = getStringAttributeValue(node, "Commission",512);
        if(stringCommission != null) setCommission(stringCommission);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ActOfInventarization) distributedObject).getOrgOwner());
        setDateOfAct(((ActOfInventarization) distributedObject).getDateOfAct());
        setNumber(((ActOfInventarization) distributedObject).getNumber());
        setCommission(((ActOfInventarization) distributedObject).getCommission());
    }

    private Date dateOfAct;
    private String number;
    private String commission;
    private Set<InternalIncomingDocument> internalIncomingDocumentInternal;
    private Set<InternalDisposingDocument> InternalDisposingDocumentInternal;

    public Set<InternalDisposingDocument> getInternalDisposingDocumentInternal() {
        return InternalDisposingDocumentInternal;
    }

    public void setInternalDisposingDocumentInternal(Set<InternalDisposingDocument> internalDisposingDocumentInternal) {
        InternalDisposingDocumentInternal = internalDisposingDocumentInternal;
    }

    public Set<InternalIncomingDocument> getInternalIncomingDocumentInternal() {
        return internalIncomingDocumentInternal;
    }

    public void setInternalIncomingDocumentInternal(Set<InternalIncomingDocument> internalIncomingDocumentInternal) {
        this.internalIncomingDocumentInternal = internalIncomingDocumentInternal;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateOfAct() {
        return dateOfAct;
    }

    public void setDateOfAct(Date dateOfAct) {
        this.dateOfAct = dateOfAct;
    }
}
