/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import org.codehaus.jackson.annotate.JsonProperty;

public class GroupNameDTO {
    @JsonProperty("GroupName")
    private String groupName;

    public GroupNameDTO(){

    }

    public GroupNameDTO(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
