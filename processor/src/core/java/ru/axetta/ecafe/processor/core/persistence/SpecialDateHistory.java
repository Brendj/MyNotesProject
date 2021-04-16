/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 15.04.2021.
 */
public class SpecialDateHistory {
    private Long idOfSpecialDateHistory;
    private Long idOfOrg;
    private Date date;
    private Org org;
    private Boolean isWeekend;
    private Boolean deleted;
    private Long idOfOrgOwner;
    private Long version;

    public SpecialDateHistory(Long idOfOrg, Date date, Boolean isWeekend, Boolean deleted, Long idOfOrgOwner,
            Long version, String comment, Long idOfClientGroup, String staffGuid, Date armLastUpdate) {
        this.idOfOrg = idOfOrg;
        this.date = date;
        this.isWeekend = isWeekend;
        this.deleted = deleted;
        this.idOfOrgOwner = idOfOrgOwner;
        this.version = version;
        this.comment = comment;
        this.idOfClientGroup = idOfClientGroup;
        this.staffGuid = staffGuid;
        this.armLastUpdate = armLastUpdate;
        this.createdDate = new Date();
    }

    private String comment;
    private Long idOfClientGroup;
    private String staffGuid;
    private Date armLastUpdate;
    private Date createdDate;

    public SpecialDateHistory() {

    }

    public Long getIdOfSpecialDateHistory() {
        return idOfSpecialDateHistory;
    }

    public void setIdOfSpecialDateHistory(Long idOfSpecialDateHistory) {
        this.idOfSpecialDateHistory = idOfSpecialDateHistory;
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

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean weekend) {
        isWeekend = weekend;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public String getStaffGuid() {
        return staffGuid;
    }

    public void setStaffGuid(String staffGuid) {
        this.staffGuid = staffGuid;
    }

    public Date getArmLastUpdate() {
        return armLastUpdate;
    }

    public void setArmLastUpdate(Date armLastUpdate) {
        this.armLastUpdate = armLastUpdate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    public void setIdOfOrgOwner(Long idOfOrgOwner) {
        this.idOfOrgOwner = idOfOrgOwner;
    }
}
