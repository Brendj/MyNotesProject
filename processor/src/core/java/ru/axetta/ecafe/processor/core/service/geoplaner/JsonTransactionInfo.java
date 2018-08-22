/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import java.util.LinkedList;
import java.util.List;

public class JsonTransactionInfo {
    private Long trackerUid;
    private Long trackerId;

    private List<JsonTransactionInfoItem> transactions;


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

    public List<JsonTransactionInfoItem> getTransactions() {
        if(this.transactions == null){
            this.transactions = new LinkedList<JsonTransactionInfoItem>();
        }
        return transactions;
    }

    public void setTransactions(List<JsonTransactionInfoItem> transactions) {
        this.transactions = transactions;
    }
}
