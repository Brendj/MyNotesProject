/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

public class TransactionItem {
   private String id;
   private String accounttypeid;
   private String accounttypename;
   private String sum;
   private String timestamp;
   private String transactiontypeid;
   private String transactiontypename;
   private String transactiontag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccounttypeid() {
        return accounttypeid;
    }

    public void setAccounttypeid(String accounttypeid) {
        this.accounttypeid = accounttypeid;
    }

    public String getAccounttypename() {
        return accounttypename;
    }

    public void setAccounttypename(String accounttypename) {
        this.accounttypename = accounttypename;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactiontypeid() {
        return transactiontypeid;
    }

    public void setTransactiontypeid(String transactiontypeid) {
        this.transactiontypeid = transactiontypeid;
    }

    public String getTransactiontypename() {
        return transactiontypename;
    }

    public void setTransactiontypename(String transactiontypename) {
        this.transactiontypename = transactiontypename;
    }

    public String getTransactiontag() {
        return transactiontag;
    }

    public void setTransactiontag(String transactiontag) {
        this.transactiontag = transactiontag;
    }
}
