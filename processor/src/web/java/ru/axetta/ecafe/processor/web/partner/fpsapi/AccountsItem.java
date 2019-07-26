/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

public class AccountsItem {
    private String timestamp;
    private Long id;
    private String accounttypeid = "1";
    private String accouttypename = "Горячее питание";
    private Long sum;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccounttypeid() {
        return accounttypeid;
    }

    public void setAccounttypeid(String accounttypeid) {
        this.accounttypeid = accounttypeid;
    }

    public String getAccouttypename() {
        return accouttypename;
    }

    public void setAccouttypename(String accouttypename) {
        this.accouttypename = accouttypename;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }
}
