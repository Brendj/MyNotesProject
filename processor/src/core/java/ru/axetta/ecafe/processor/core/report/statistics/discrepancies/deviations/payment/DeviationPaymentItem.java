/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.10.14
 * Time: 11:30
 */

public class DeviationPaymentItem {

    private Long rowNum;       // Номер по порядку
    private String orgName;    // Название организации
    private String address;    // Адресс организации

    private List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList;


    public DeviationPaymentItem() {
    }

    public DeviationPaymentItem(Long rowNum, String orgName, String address,
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList) {
        this.rowNum = rowNum;
        this.orgName = orgName;
        this.address = address;
        this.deviationPaymentSubReportItemList = deviationPaymentSubReportItemList;
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

    public List<DeviationPaymentSubReportItem> getDeviationPaymentSubReportItemList() {
        return deviationPaymentSubReportItemList;
    }

    public void setDeviationPaymentSubReportItemList(
            List<DeviationPaymentSubReportItem> deviationPaymentSubReportItemList) {
        this.deviationPaymentSubReportItemList = deviationPaymentSubReportItemList;
    }
}
