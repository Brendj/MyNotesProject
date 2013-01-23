/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.contragent;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.13
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public class OrgItem {

    private final Long idOfOrg;
    private final String shortName;

    public OrgItem(Long idOfOrg, String shortName) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }
}
