/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.GroupNameDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ResponseManagerGroups extends Result {
    @JsonProperty("GroupManager")
    private List<GroupNameDTO> groupNameDTOList;

    public ResponseManagerGroups() {

    }

    public ResponseManagerGroups(List<GroupNameDTO> groupNameDTOList){
        super(0, "OK");
        this.groupNameDTOList = groupNameDTOList;
    }

    public List<GroupNameDTO> getGroupNameDTOList() {
        return groupNameDTOList;
    }

    public void setGroupNameDTOList(List<GroupNameDTO> groupNameDTOList) {
        this.groupNameDTOList = groupNameDTOList;
    }
}
