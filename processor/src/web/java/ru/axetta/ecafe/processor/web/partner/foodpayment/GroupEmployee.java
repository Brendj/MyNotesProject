/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import java.util.LinkedList;
import java.util.List;

public class GroupEmployee {
    private String groupName;
    private String groupId;
    private List<GroupManager> employees;

    public GroupEmployee(){
        employees = new LinkedList<GroupManager>();
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId(){
        return groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setEmployees(List<GroupManager> employees) {
        this.employees = employees;
    }

    public List<GroupManager> getEmployees() {
        return employees;
    }
}