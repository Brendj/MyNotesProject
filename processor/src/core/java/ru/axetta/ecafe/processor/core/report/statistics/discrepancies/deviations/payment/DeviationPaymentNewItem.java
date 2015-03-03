/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.03.15
 * Time: 16:15
 */

public class DeviationPaymentNewItem {

    private Long rowNum;       // Номер по порядку
    private String orgName;    // Название организации
    private String address;    // Адресс организации
    private String mainBuilding; // Главный корпус

    private List<DeviationPaymentNewSubReportItem> deviationPaymentNewSubReportItemList;


    public DeviationPaymentNewItem() {
    }

    public DeviationPaymentNewItem(Long rowNum, String orgName, String address, String mainBuilding,
            List<DeviationPaymentNewSubReportItem> deviationPaymentNewSubReportItemList) {
        this.rowNum = rowNum;
        this.orgName = orgName;
        this.address = address;
        this.mainBuilding = mainBuilding;
        this.deviationPaymentNewSubReportItemList = deviationPaymentNewSubReportItemList;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMainBuilding() {
        return mainBuilding;
    }

    public void setMainBuilding(String mainBuilding) {
        this.mainBuilding = mainBuilding;
    }

    public List<DeviationPaymentNewSubReportItem> getDeviationPaymentNewSubReportItemList() {
        return deviationPaymentNewSubReportItemList;
    }

    public void setDeviationPaymentNewSubReportItemList(
            List<DeviationPaymentNewSubReportItem> deviationPaymentNewSubReportItemList) {
        this.deviationPaymentNewSubReportItemList = deviationPaymentNewSubReportItemList;
    }
}
