/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditGroupClientsGroupRequest {

    @JsonProperty("NewOrgId")
    private long newOrgId;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("OldGroups")
    private List<Long> oldGroups;

    @JsonProperty("StrictEditMode")
    private boolean strictEditMode;

    public long getNewOrgId() {
        return newOrgId;
    }

    public void setNewOrgId(long orgId) {
        this.newOrgId = orgId;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public List<Long> getOldGroups() {
        return oldGroups;
    }

    public void setOldGroups(List<Long> oldGroups) {
        this.oldGroups = oldGroups;
    }

    public boolean isStrictEditMode() {
        return strictEditMode;
    }

    public void setStrictEditMode(boolean strictEditMode) {
        this.strictEditMode = strictEditMode;
    }
}
