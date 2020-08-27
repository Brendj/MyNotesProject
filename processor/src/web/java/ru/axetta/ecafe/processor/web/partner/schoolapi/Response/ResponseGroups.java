/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.GroupInfo;

import java.util.LinkedList;
import java.util.List;

public class ResponseGroups extends Result {
    private List<GroupInfo> groups;
    public ResponseGroups(){
        groups = new LinkedList<GroupInfo>();
    }

    public void setGroups(List<GroupInfo> groupsInfo){
        this.groups = groupsInfo;
    }

    public List<GroupInfo> getGroups(){
        return groups;
    }
}
