/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 17:02
 */
public class ClientItem {
    private long id;
    private long idOfOrg;
    private String orgName;
    private String fullName;
    private String groupName;

    private int planType = IN_PLAN_TYPE;
    public static final int IN_PLAN_TYPE = 0;
    public static final int IN_RESERVE_TYPE = 1;


    public ClientItem(long id, String fullName,  String groupName, int planType) {
        this.id = id;
        this.fullName = fullName;
        this.groupName = groupName;
        this.planType = planType;
    }

    public ClientItem(long id, long idOfOrg,String orgName, String fullName, String groupName, int planType) {
        this.id = id;
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.fullName = fullName;
        this.groupName = groupName;
        this.planType = planType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }
}
