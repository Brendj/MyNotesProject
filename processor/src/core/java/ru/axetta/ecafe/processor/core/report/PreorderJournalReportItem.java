/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.PreorderMenuDetail;

import java.util.Date;

public class PreorderJournalReportItem {
    private Integer number;
    private Date preorderDate;
    private Long idOfOrg;
    private String fio;
    private String groupName;
    private String complexName;
    private Integer amount;
    private String isDeleted;
    private String deleteReason;
    private String isRegular;
    private Date createdDate;
    private Date lastUpdate;
    private String mobile;

    public PreorderJournalReportItem() {

    }

    public PreorderJournalReportItem(Integer number, PreorderComplex preorderComplex) {
        this.number = number;
        this.preorderDate = preorderComplex.getPreorderDate();
        this.idOfOrg = preorderComplex.getIdOfOrgOnCreate();
        this.fio = preorderComplex.getClient().getPerson().getFullName();
        this.groupName = preorderComplex.getClient().getClientGroup().getGroupName();
        this.complexName = preorderComplex.getComplexName();
        if (preorderComplex.getAmount().equals(0)) {
            this.complexName += ": \n";
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                this.complexName += preorderMenuDetail.getMenuDetailName() + "\n";
            }
            this.complexName = this.complexName.substring(0, this.complexName.length()-1);
        }
        this.amount = preorderComplex.getAmount() == 0 ? null : preorderComplex.getAmount();
        this.isDeleted = preorderComplex.getDeletedState() ? "Да" : "";
        this.deleteReason = preorderComplex.getDeletedState() ? preorderComplex.getState().toString() : "";
        this.isRegular = preorderComplex.getRegularPreorder() != null ? "Да" : "";
        this.createdDate = preorderComplex.getCreatedDate();
        this.lastUpdate = preorderComplex.getLastUpdate();
        this.mobile = preorderComplex.getMobile();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String deleted) {
        isDeleted = deleted;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public String getIsRegular() {
        return isRegular;
    }

    public void setIsRegular(String regular) {
        isRegular = regular;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
