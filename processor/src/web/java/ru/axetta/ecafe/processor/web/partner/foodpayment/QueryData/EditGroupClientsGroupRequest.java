/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import ru.axetta.ecafe.processor.web.partner.foodpayment.DTO.RequestGroupDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditGroupClientsGroupRequest {

    @JsonProperty("OrgId")
    private long orgId;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("OldGroups")
    private List<RequestGroupDTO> oldGroups;

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

    public List<RequestGroupDTO> getOldGroups() {
        return oldGroups;
    }

    public void setOldGroups(List<RequestGroupDTO> oldGroups) {
        this.oldGroups = oldGroups;
    }

    public boolean isStrictEditMode() {
        return strictEditMode;
    }

    public void setStrictEditMode(boolean strictEditMode) {
        this.strictEditMode = strictEditMode;
    }
}
