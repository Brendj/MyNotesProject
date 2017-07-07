/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.latepayment;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 04.09.15
 * Time: 15:54
 */

public class LatePaymentReportModelByDays {

    private String orgname;
    private String address;
    private Long benefitcount;
    private Long daycount;
    private Long feedcount;
    private Date date;
    private Long idOfOrg;
    private Long reservcount;

    public LatePaymentReportModelByDays() {
    }

    public LatePaymentReportModelByDays(String orgname, String address, Long benefitcount, Long daycount,
            Long feedcount, Date date, Long idOfOrg, Long reservcount) {
        this.orgname = orgname;
        this.address = address;
        this.benefitcount = benefitcount;
        this.daycount = daycount;
        this.feedcount = feedcount;
        this.date = date;
        this.idOfOrg = idOfOrg;
        this.reservcount = reservcount;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getReservcount() {
        return reservcount;
    }

    public void setReservcount(Long reservcount) {
        this.reservcount = reservcount;
    }
}
