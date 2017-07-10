/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 10:15
 */

public class LatePaymentDetailedReportModel {

    private String orgnum;
    private String address;
    private String paymentDate;
    private Long idOfOrg;

    private List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList;

    public LatePaymentDetailedReportModel(String orgnum, String address, String paymentDate, Long idOfOrg) {
        this.orgnum = orgnum;
        this.address = address;
        this.paymentDate = paymentDate;
        this.idOfOrg = idOfOrg;
    }

    public LatePaymentDetailedReportModel() {
    }

    public String getOrgnum() {
        return orgnum;
    }

    public void setOrgnum(String orgnum) {
        this.orgnum = orgnum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public List<LatePaymentDetailedSubReportModel> getLatePaymentDetailedSubReportModelList() {
        return latePaymentDetailedSubReportModelList;
    }

    public void setLatePaymentDetailedSubReportModelList(
            List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList) {
        this.latePaymentDetailedSubReportModelList = latePaymentDetailedSubReportModelList;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
