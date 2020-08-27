/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditClientsGroupRequest {

    @JsonProperty("NewOrgId")
    private long newOrgId;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("ContractIds")
    private List<Long> contractIds;

    @JsonProperty("StrictEditMode")
    private boolean strictEditMode;

    public long getNewOrgId() {
        return newOrgId;
    }

    public void setNewOrgId(long newOrgId) {
        this.newOrgId = newOrgId;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public List<Long> getContractIds() {
        return contractIds;
    }

    public void setContractIds(List<Long> contractIds) {
        this.contractIds = contractIds;
    }

    public boolean isStrictEditMode() {
        return strictEditMode;
    }

    public void setStrictEditMode(boolean strictEditMode) {
        this.strictEditMode = strictEditMode;
    }
}
