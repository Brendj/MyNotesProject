/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 13:07
 */
public class SpecialDate {
    private Long idOfSpecialDate;
    private Long idOfOrg;
    private Date date;
    private Org org;
    private Boolean isWeekend;
    private Boolean deleted;
    private Org orgOwner; // От какой организации создана запись
    private Long version;
    private String comment;
    private Long idOfClientGroup;

    public SpecialDate() {
    }

    public SpecialDate(CompositeIdOfSpecialDate compositeIdOfSpecialDate, Boolean isWeekend, String comment) {
        this.idOfOrg = compositeIdOfSpecialDate.getIdOfOrg();
        this.date = compositeIdOfSpecialDate.getDate();
        this.isWeekend = isWeekend;
        this.comment = comment;
    }

    /*public CompositeIdOfSpecialDate getCompositeIdOfSpecialDate() {
        return compositeIdOfSpecialDate;
    }

    public void setCompositeIdOfSpecialDate(CompositeIdOfSpecialDate compositeIdOfSpecialDate) {
        this.compositeIdOfSpecialDate = compositeIdOfSpecialDate;
    }*/

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Org orgOwner) {
        this.orgOwner = orgOwner;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdOfSpecialDate() {
        return idOfSpecialDate;
    }

    public void setIdOfSpecialDate(Long idOfSpecialDate) {
        this.idOfSpecialDate = idOfSpecialDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
