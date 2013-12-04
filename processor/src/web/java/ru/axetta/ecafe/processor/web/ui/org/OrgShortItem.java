/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.04.13
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class OrgShortItem {

    private Long idOfOrg;
    private String shortName;
    private String officialName;
    private String address;
    private Boolean selected = false;

    public OrgShortItem() {
        selected = false;
    }

    public OrgShortItem(Long idOfOrg, String shortName, String officialName, String address) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
        this.officialName = officialName;
        this.address = address;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
