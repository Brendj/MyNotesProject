/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class DiscountGroupsListRequest {

    @JsonProperty("DiscountId")
    private Long discountId;

    @JsonProperty("Status")
    private Boolean status;

    @JsonProperty("Groups")
    private List<String> groups;

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
