/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.09.15
 * Time: 11:05
 */

public class LatePaymentReportModel {

    private Long num;
    private String orgname;
    private String address;
    private Long benefitcount;
    private Long daycount;
    private Long feedcount;

    public LatePaymentReportModel() {
    }

    public LatePaymentReportModel(Long num, String orgname, String address, Long benefitcount, Long daycount,
            Long feedcount) {
        this.num = num;
        this.orgname = orgname;
        this.address = address;
        this.benefitcount = benefitcount;
        this.daycount = daycount;
        this.feedcount = feedcount;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getBenefitcount() {
        return benefitcount;
    }

    public void setBenefitcount(Long benefitcount) {
        this.benefitcount = benefitcount;
    }

    public Long getDaycount() {
        return daycount;
    }

    public void setDaycount(Long daycount) {
        this.daycount = daycount;
    }

    public Long getFeedcount() {
        return feedcount;
    }

    public void setFeedcount(Long feedcount) {
        this.feedcount = feedcount;
    }
}
