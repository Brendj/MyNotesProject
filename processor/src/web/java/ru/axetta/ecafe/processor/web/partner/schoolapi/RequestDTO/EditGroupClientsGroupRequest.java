/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditGroupClientsGroupRequest {

    @JsonProperty("NewOrgId")
    private long newOrgId;

    @JsonProperty("NewGroupName")
    private String newGroupName;

    @JsonProperty("OldGroups")
    private List<String> oldGroups;

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

    public List<String> getOldGroups() {
        return oldGroups;
    }

    public void setOldGroups(List<String> oldGroups) {
        this.oldGroups = oldGroups;
    }

    public boolean isStrictEditMode() {
        return strictEditMode;
    }

    public void setStrictEditMode(boolean strictEditMode) {
        this.strictEditMode = strictEditMode;
    }
}
