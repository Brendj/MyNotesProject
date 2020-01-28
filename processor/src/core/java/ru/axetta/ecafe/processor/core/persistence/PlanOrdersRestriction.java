/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 27.01.2020.
 */
public class PlanOrdersRestriction {
    private Long idOfPlanOrdersRestriction;
    private Client client;
    private Org org;
    private Contragent contragent;
    private Integer armComplexId;
    private PlanOrdersRestrictionType planOrdersRestrictionType;
    private Long version;
    private Date createdDate;
    private Date lastUpdate;
    private Boolean deletedState;

    public PlanOrdersRestriction() {

    }

    public Long getIdOfPlanOrdersRestriction() {
        return idOfPlanOrdersRestriction;
    }

    public void setIdOfPlanOrdersRestriction(Long idOfPlanOrdersRestriction) {
        this.idOfPlanOrdersRestriction = idOfPlanOrdersRestriction;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Contragent getContragent() {
        return contragent;
    }

    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public Integer getArmComplexId() {
        return armComplexId;
    }

    public void setArmComplexId(Integer armComplexId) {
        this.armComplexId = armComplexId;
    }

    public PlanOrdersRestrictionType getPlanOrdersRestrictionType() {
        return planOrdersRestrictionType;
    }

    public void setPlanOrdersRestrictionType(PlanOrdersRestrictionType planOrdersRestrictionType) {
        this.planOrdersRestrictionType = planOrdersRestrictionType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }
}
