/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 08.07.16
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class GroupNamesToOrgs {

    private Long idOfGroupNameToOrg;
    private Long idOfMainOrg;
    private Long idOfOrg;
    private Integer mainBuilding;
    private String groupName;
    private Long version;
    private String parentGroupName;
    private Boolean isMiddleGroup;


    public GroupNamesToOrgs() {
    }

    public GroupNamesToOrgs(Long idOfGroupNameToOrg, Long idOfOrg, Long idOfMainOrg, Integer mainBuilding,
            String groupName,long version) {
        this.idOfGroupNameToOrg = idOfGroupNameToOrg;
        this.idOfOrg = idOfOrg;
        this.idOfMainOrg = idOfMainOrg;
        this.mainBuilding = mainBuilding;
        this.groupName = groupName;
        this.version = version;
    }

    public GroupNamesToOrgs(Long idOfMainOrg, Long idOfOrg, Integer mainBuilding, String groupName, Long version,
            String parentGroupName, Boolean isMiddleGroup) {
        this.idOfMainOrg = idOfMainOrg;
        this.idOfOrg = idOfOrg;
        this.mainBuilding = mainBuilding;
        this.groupName = groupName;
        this.version = version;
        this.parentGroupName = parentGroupName;
        this.isMiddleGroup = isMiddleGroup;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }



    public Long getIdOfGroupNameToOrg() {
        return idOfGroupNameToOrg;
    }

    public void setIdOfGroupNameToOrg(Long idOfGroupNameToOrg) {
        this.idOfGroupNameToOrg = idOfGroupNameToOrg;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfMainOrg() {
        return idOfMainOrg;
    }

    public void setIdOfMainOrg(Long idOfMainOrg) {
        this.idOfMainOrg = idOfMainOrg;
    }

    public Integer getMainBuilding() {
        return mainBuilding;
    }

    public void setMainBuilding(Integer mainBuilding) {
        this.mainBuilding = mainBuilding;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }

    public Boolean getIsMiddleGroup() {
        return isMiddleGroup;
    }

    public void setIsMiddleGroup(Boolean middleGroup) {
        isMiddleGroup = middleGroup;
    }
}
