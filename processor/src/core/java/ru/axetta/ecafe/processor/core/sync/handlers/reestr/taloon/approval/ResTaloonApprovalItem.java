/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.TaloonApproval;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class ResTaloonApprovalItem {

    private Long orgId;
    private Date date;
    private String name;
    private Integer qty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private Long version;
    private Boolean deletedState;
    private Integer resultCode;
    private String errorMessage;

    public ResTaloonApprovalItem() {

    }

    public ResTaloonApprovalItem(TaloonApproval taloon) {
        this.orgId = taloon.getCompositeIdOfTaloonApproval().getIdOfOrg();
        this.date = taloon.getCompositeIdOfTaloonApproval().getTaloonDate();
        this.name = taloon.getCompositeIdOfTaloonApproval().getTaloonName();
        this.qty = taloon.getQty();
        this.price = taloon.getPrice();
        this.createdType = taloon.getCreatedType();
        this.version = taloon.getVersion();
        this.deletedState = taloon.getDeletedState();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "OrgId", orgId);
        if (date != null) {
            XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToString(date));
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "Qty", qty);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        if (createdType != null) {
            XMLUtils.setAttributeIfNotNull(element, "CreatedType", createdType.ordinal());
        }
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public TaloonCreatedTypeEnum getCreatedType() {
        return createdType;
    }

    public void setCreatedType(TaloonCreatedTypeEnum createdType) {
        this.createdType = createdType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }
}
