/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.payment.orders;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 31.01.14
 * Time: 12:23
 */

public class OrgItem {

    private Long idOfOrg;
    private String orgShortName;
    private String address;
    private String orgTypeCategory;

    public OrgItem(Long idOfOrg, String orgShortName, String address, String orgTypeCategory) {
        this.idOfOrg = idOfOrg;
        this.orgShortName = orgShortName;
        this.address = address;
        this.orgTypeCategory = orgTypeCategory;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgTypeCategory() {
        return orgTypeCategory;
    }

    public void setOrgTypeCategory(String orgTypeCategory) {
        this.orgTypeCategory = orgTypeCategory;
    }
}
