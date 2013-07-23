/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.07.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRole {

    private Long idOfRole;
    private String roleName;
    private String extendRoleName;

    public ComplexRole(Long idOfRole, String roleName) {
        this.idOfRole = idOfRole;
        this.roleName = roleName;
    }

    public Long getIdOfRole() {
        return idOfRole;
    }

    private void setIdOfRole(Long idOfRole) {
        this.idOfRole = idOfRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getExtendRoleName() {
        return extendRoleName;
    }

    public void setExtendRoleName(String extendRoleName) {
        this.extendRoleName = extendRoleName;
    }

    protected ComplexRole() {}
}
