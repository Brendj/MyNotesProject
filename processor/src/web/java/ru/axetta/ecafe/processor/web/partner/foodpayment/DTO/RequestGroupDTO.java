/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.DTO;

import org.codehaus.jackson.annotate.JsonProperty;

public class RequestGroupDTO {
    @JsonProperty("OrgId")
    private Long orgId;

    @JsonProperty("GroupId")
    private Long groupId;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
