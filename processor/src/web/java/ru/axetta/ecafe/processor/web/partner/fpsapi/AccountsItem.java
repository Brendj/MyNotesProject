/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

public class AccountsItem {
    private Long id;
    private String accounttypeid = "1";
    private String accouttypename = "Горячее питание";
    private Double sum;

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

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum/100;
    }
}
