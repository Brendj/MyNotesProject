/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import java.util.LinkedList;
import java.util.List;

public class GroupInfo {
    private String groupName;
    private Long groupId;
    private Long orgId;
    private List<GroupManager> managers;

    public GroupInfo(){
        managers = new LinkedList<GroupManager>();
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public Long getGroupId(){
        return groupId;
    }

    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public List<GroupManager> getManagers(){
        return managers;
    }

    public void setManagers(List<GroupManager> managers){
        this.managers = managers;
    }
}
