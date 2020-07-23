/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditClientsGroupRequest {

    @JsonProperty("orgId")
    private long orgId;

    @JsonProperty("NewGroupId")
    private long newGroupId;

    @JsonProperty("contractId")
    private List<Long> contractIds;

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getNewGroupId() {
        return newGroupId;
    }

    public void setNewGroupId(long newGroupId) {
        this.newGroupId = newGroupId;
    }

    public List<Long> getContractIds() {
        return contractIds;
    }

    public void setContractIds(List<Long> contractIds) {
        this.contractIds = contractIds;
    }
}
