/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 28.12.14
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class SimpleOrganizationItem {

    private Long idofOrg;
    private String shortName;
    /*
    * @see ru.axetta.ecafe.processor.core.persistence.OrganizationType
    * */
    private int type;

    public SimpleOrganizationItem() {
    }

    public SimpleOrganizationItem(Long idofOrg, String shortName, int type) {
        this.idofOrg = idofOrg;
        this.shortName = shortName;
        this.type = type;
    }

    public Long getIdofOrg() {
        return idofOrg;
    }

    public void setIdofOrg(Long idofOrg) {
        this.idofOrg = idofOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
