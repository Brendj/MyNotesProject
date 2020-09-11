/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created by nuc on 28.05.2020.
 */
public class PreorderDoublePaymentReportItem {
    private String contragentName;
    private Date preorderDate;
    private String orderInfo;
    private Long idOfPreorderComplex;
    private Long idOfClient;
    private String fio;
    private String groupName;
    private String complexName;
    private Long preorderSum;
    private Long paySum;

    public PreorderDoublePaymentReportItem() {

    }

    public PreorderDoublePaymentReportItem(String contragentName, Date preorderDate, String orderInfo,
            Long idOfPreorderComplex, Long idOfClient, String fio, String groupName, String complexName,
            Long preorderSum, Long paySum) {
        this.contragentName = contragentName;
        this.preorderDate = preorderDate;
        this.orderInfo = orderInfo;
        this.idOfPreorderComplex = idOfPreorderComplex;
        this.idOfClient = idOfClient;
        this.fio = fio;
        this.groupName = groupName;
        this.complexName = complexName;
        this.preorderSum = preorderSum;
        this.paySum = paySum;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public Date getPreorderDate() {
        return preorderDate;
    }

    public void setPreorderDate(Date preorderDate) {
        this.preorderDate = preorderDate;
    }

    public Long getIdOfPreorderComplex() {
        return idOfPreorderComplex;
    }

    public void setIdOfPreorderComplex(Long idOfPreorderComplex) {
        this.idOfPreorderComplex = idOfPreorderComplex;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getPreorderSum() {
        return preorderSum;
    }

    public void setPreorderSum(Long preorderSum) {
        this.preorderSum = preorderSum;
    }

    public Long getPaySum() {
        return paySum;
    }

    public void setPaySum(Long paySum) {
        this.paySum = paySum;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
