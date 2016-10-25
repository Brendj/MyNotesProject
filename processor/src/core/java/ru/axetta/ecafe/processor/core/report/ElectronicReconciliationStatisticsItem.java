/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 12:59
 */

public class ElectronicReconciliationStatisticsItem implements Comparable<ElectronicReconciliationStatisticsItem> {

    private Long rowNum;                // Номер по порядку
    private String orgName;             // Название организации
    private String orgType;             // Тип ОО
    private String district;            // Округ
    private String address;             // Адресс ОО

    private List<ElectronicReconciliationStatisticsSubItem> electronicReconciliationStatisticsSubItems = new ArrayList<ElectronicReconciliationStatisticsSubItem>();

    public ElectronicReconciliationStatisticsItem() {
    }

    public ElectronicReconciliationStatisticsItem(Long rowNum, String orgName, String orgType, String district, String address) {
        this.rowNum = rowNum;
        this.orgName = orgName;
        this.orgType = orgType;
        this.district = district;
        this.address = address;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ElectronicReconciliationStatisticsSubItem> getElectronicReconciliationStatisticsSubItems() {
        return electronicReconciliationStatisticsSubItems;
    }

    public void setElectronicReconciliationStatisticsSubItems(
            List<ElectronicReconciliationStatisticsSubItem> electronicReconciliationStatisticsSubItems) {
        this.electronicReconciliationStatisticsSubItems = electronicReconciliationStatisticsSubItems;
    }

    public int compareTo(ElectronicReconciliationStatisticsItem o) {
        int retCode = this.rowNum.compareTo(o.getRowNum());
        return retCode;
    }

    @Override
    public int hashCode() {
        int result = rowNum.hashCode();
        result = 31 * result + orgType.hashCode();
        result = 31 * result + district.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }
}
