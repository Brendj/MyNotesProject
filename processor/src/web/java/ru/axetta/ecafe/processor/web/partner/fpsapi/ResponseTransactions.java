/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseTransactions extends Result{
    private List<TransactionItem> transactions;


    public ResponseTransactions(){
        this.transactions = new LinkedList<TransactionItem>();
    }

    public List<TransactionItem> getTransaction() {
        return transactions;
    }

    public void setTransaction(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }
}
