/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditClientsGroupRequest {

    @JsonProperty("OrgId")
    private long orgId;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("ContractIds")
    private List<Long> contractIds;

    @JsonProperty("StrictEditMode")
    private boolean strictEditMode;

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
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
