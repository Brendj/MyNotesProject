/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

/**
 * User: regal
 * Date: 27.01.15
 * Time: 11:42
 */
public class OrgItem {

    private long idOfOrg;
    private String officialName;
    private String district;

    public OrgItem(long idOfOrg, String officialName, String district) {
        this.idOfOrg = idOfOrg;
        this.officialName = officialName;
        this.district = district;
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
