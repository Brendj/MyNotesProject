/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.List;

public class ComplexMenuReportItem {
    private String idOfOrg;
    private String orgCount;
    private List<String> org;
    private List<String> complexList;
    private  List<ComplexItem> complexItem;

    public ComplexMenuReportItem(List<String> org, String orgCount, List<String> complexList) {
        this.org = org;
        this.orgCount = orgCount;
        this.complexList = complexList;
    }

    public ComplexMenuReportItem(String idOfOrg, String orgCount, List<ComplexItem> complexItem) {
        this.idOfOrg = idOfOrg;
        this.orgCount = orgCount;
        this.complexItem = complexItem;
    }

    public List<ComplexItem> getComplexItem() {
        return complexItem;
    }

    public void setComplexItem(List<ComplexItem> complexItem) {
        this.complexItem = complexItem;
    }

    public String getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(String idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgCount() {
        return orgCount;
    }

    public void setOrgCount(String orgCount) {
        this.orgCount = orgCount;
    }

    public List<String> getOrg() {
        return org;
    }

    public void setOrg(List<String> org) {
        this.org = org;
    }

    public List<String> getComplexList() {
        return complexList;
    }

    public void setComplexList(List<String> complexList) {
        this.complexList = complexList;
    }

}

