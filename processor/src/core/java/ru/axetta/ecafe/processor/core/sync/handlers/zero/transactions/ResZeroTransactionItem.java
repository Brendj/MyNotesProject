/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.persistence.ZeroTransaction;
import ru.axetta.ecafe.processor.core.persistence.ZeroTransactionCriteriaEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class ResZeroTransactionItem {
    private Long orgId;
    private Date date;
    private ZeroTransactionCriteriaEnum idOfCriteria;
    private Integer targetLevel;
    private Integer actualLevel;
    private Integer criteriaLevel;
    private Integer idOfReason;
    private String comment;
    private Long version;
    private Integer resultCode;
    private String errorMessage;

    public ResZeroTransactionItem() {

    }

    public ResZeroTransactionItem(ZeroTransaction zt) {
        this.setOrgId(zt.getCompositeIdOfZeroTransaction().getIdOfOrg());
        this.setDate(zt.getCompositeIdOfZeroTransaction().getTransactionDate());
        this.setIdOfCriteria(zt.getCompositeIdOfZeroTransaction().getIdOfCriteria());
        this.setTargetLevel(zt.getTargetLevel());
        this.setActualLevel(zt.getActualLevel());
        this.setCriteriaLevel(zt.getCriteriaLevel());
        this.setIdOfReason(zt.getIdOfReason());
        this.setComment(zt.getComment());
        this.setVersion(zt.getVersion());
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        if (getDate() != null) {
            XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToStringFullYear(getDate()));
        }
        XMLUtils.setAttributeIfNotNull(element, "V", getVersion());
        XMLUtils.setAttributeIfNotNull(element, "Res", getResultCode());
        XMLUtils.setAttributeIfNotNull(element, "Comment", comment);
        XMLUtils.setAttributeIfNotNull(element, "TargetLevel", targetLevel);
        XMLUtils.setAttributeIfNotNull(element, "ActualLevel", actualLevel);
        XMLUtils.setAttributeIfNotNull(element, "CriteriaLevel", criteriaLevel);
        XMLUtils.setAttributeIfNotNull(element, "IdOfReason", idOfReason);
        if (idOfCriteria != null) {
            XMLUtils.setAttributeIfNotNull(element, "IdOfCriteria", idOfCriteria.ordinal());
        }
        if (getResultCode() != null && getResultCode() != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", getErrorMessage());
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

    public ZeroTransactionCriteriaEnum getIdOfCriteria() {
        return idOfCriteria;
    }

    public void setIdOfCriteria(ZeroTransactionCriteriaEnum idOfCriteria) {
        this.idOfCriteria = idOfCriteria;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Integer getActualLevel() {
        return actualLevel;
    }

    public void setActualLevel(Integer actualLevel) {
        this.actualLevel = actualLevel;
    }

    public Integer getCriteriaLevel() {
        return criteriaLevel;
    }

    public void setCriteriaLevel(Integer criteriaLevel) {
        this.criteriaLevel = criteriaLevel;
    }

    public Integer getIdOfReason() {
        return idOfReason;
    }

    public void setIdOfReason(Integer idOfReason) {
        this.idOfReason = idOfReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
