/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.web.partner.foodpayment.DTO.EditClientsGroupsGroupDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ResponseEditClientsGroup extends Result {
    @JsonProperty("Groups")
    private List<EditClientsGroupsGroupDTO> groups;

    public List<EditClientsGroupsGroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<EditClientsGroupsGroupDTO> groups) {
        this.groups = groups;
    }
}
