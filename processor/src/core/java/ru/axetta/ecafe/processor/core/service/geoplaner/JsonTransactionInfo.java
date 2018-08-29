/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.Date;

public class JsonTransactionInfo {
    private Long trackerUid;
    private Long trackerId;
    private Date transactionTime;
    private Integer sourceType;
    private Long transactionSum;
    private String sourceName;

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Long getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(Long transactionSum) {
        this.transactionSum = transactionSum;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getTrackerUid() {
        return trackerUid;
    }

    public void setTrackerUid(Long trackerUid) {
        this.trackerUid = trackerUid;
    }

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }
}
