/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EditGroupClientsGroupRequest {

    @JsonProperty("NewGroupId")
    private long newGroupId;

    @JsonProperty("OldGroupId")
    private List<Long> oldGroupIds;

    public long getNewGroupId() {
        return newGroupId;
    }

    public void setNewGroupId(long newGroupId) {
        this.newGroupId = newGroupId;
    }

    public List<Long> getOldGroupIds() {
        return oldGroupIds;
    }

    public void setOldGroupIds(List<Long> oldGroupIds) {
        this.oldGroupIds = oldGroupIds;
    }
}
