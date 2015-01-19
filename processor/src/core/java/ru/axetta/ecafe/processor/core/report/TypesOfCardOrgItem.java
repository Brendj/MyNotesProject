/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 19.01.15
 * Time: 11:18
 */

public class TypesOfCardOrgItem {

    private Long idOfOrg;
    private String shortName;
    private String address;

    public TypesOfCardOrgItem(Long idOfOrg, String shortName, String address) {
        this.idOfOrg = idOfOrg;
        this.shortName = shortName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
