/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

public class ComplexOrgItem {
    private String shortNameInfoService;
    private String address;
    private String shortName;
    private String district;
    private String idOfOrg;

    public ComplexOrgItem(String idOfOrg, String shortNameInfoService, String address, String shortName, String district) {
        this.shortNameInfoService = shortNameInfoService;
        this.address = address;
        this.shortName = shortName;
        this.district = district;
        this.idOfOrg = idOfOrg;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(String idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
