/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import java.util.LinkedList;
import java.util.List;

public class GroupInfo {
    private String groupName;
    private String groupId;
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

    public String getGroupId(){
        return groupId;
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }

    public List<GroupManager> getManagers(){
        return managers;
    }

    public void setManagers(List<GroupManager> managers){
        this.managers = managers;
    }
}
