/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.Date;

public class JsonPaymentInfo extends GeoplanerEventInfo {
    private Integer sourceType;
    private Long paySum;
    private Long balanceBefore;
    private Long actualBalance;
    private Date createTime;
    private String gender;

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Long getPaySum() {
        return paySum;
    }

    public void setPaySum(Long paySum) {
        this.paySum = paySum;
    }

    public Long getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Long balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(Long actualBalance) {
        this.actualBalance = actualBalance;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
