/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 15:22
 */

public class AdjustmentPaymentReportModel {

    private Long num;
    private String orgnum;
    private String address;
    private Long passage;
    private Long food;
    private Long reserve;

    public AdjustmentPaymentReportModel() {
    }

    public AdjustmentPaymentReportModel(Long num, String orgnum, String address, Long passage, Long food, Long reserve) {
        this.num = num;
        this.orgnum = orgnum;
        this.address = address;
        this.passage = passage;
        this.food = food;
        this.reserve = reserve;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
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

    public Long getPassage() {
        return passage;
    }

    public void setPassage(Long passage) {
        this.passage = passage;
    }

    public Long getFood() {
        return food;
    }

    public void setFood(Long food) {
        this.food = food;
    }

    public Long getReserve() {
        return reserve;
    }

    public void setReserve(Long reserve) {
        this.reserve = reserve;
    }
}
