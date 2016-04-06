/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * User: akmukov
 * Date: 04.04.2016
 */
public class ClientGroupManager {
    private Long idOfClientGroupManager;
    private Long version;
    private String clientGroupName;
    private Long idOfClient;
    private Long orgOwner;
    private int managerType = Types.GROUP_TEACHER_MANAGER.getTypeCode();
    private boolean deleted = false;

    public ClientGroupManager() {
    }

    public enum Types{
        GROUP_TEACHER_MANAGER(0);

        private final int typeCode;

        Types(int typeCode) {
            this.typeCode = typeCode;
        }

        public int getTypeCode() {
            return typeCode;
        }
    }

    public ClientGroupManager(Long idOfClient, String clientGroupName, Long orgOwner) {
        this.idOfClient = idOfClient;
        this.clientGroupName = clientGroupName;
        this.orgOwner = orgOwner;
        this.managerType = Types.GROUP_TEACHER_MANAGER.getTypeCode();
    }

    public Long getIdOfClientGroupManager() {
        return idOfClientGroupManager;
    }

    public void setIdOfClientGroupManager(Long idOfClientGroupManager) {
        this.idOfClientGroupManager = idOfClientGroupManager;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getClientGroupName() {
        return clientGroupName;
    }

    public void setClientGroupName(String clientGroupName) {
        this.clientGroupName = clientGroupName;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Integer getManagerType() {
        return managerType;
    }

    void setManagerType(Integer managerType) {
        this.managerType = managerType;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean disabled) {
        this.deleted = disabled;
    }
}
