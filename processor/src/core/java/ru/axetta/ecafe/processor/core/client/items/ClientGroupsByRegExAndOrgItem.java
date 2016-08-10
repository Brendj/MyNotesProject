/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client.items;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.08.16
 * Time: 12:31
 */

public class ClientGroupsByRegExAndOrgItem {

    public Long idOfOrg;
    public Long idOfClientGroup;

    public ClientGroupsByRegExAndOrgItem() {
    }

    public ClientGroupsByRegExAndOrgItem(Long idOfOrg, Long idOfClientGroup) {
        this.idOfOrg = idOfOrg;
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }
}
