/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

public class SBMSKClientSummaryBase {
    private Long contractId;
    private String balance;
    private String fio;
    private String nazn;
    private String inn;

    public SBMSKClientSummaryBase(Long contractId, Long balance, String firstName, String lastName, String middleName) {
        this.contractId = contractId;
        this.balance = String.format("%d.%02d",balance / 100, balance % 100);
        //this.fio = firstName + " " + middleName + " " + lastName.charAt(0) + ".";
        this.fio = firstName + " " + lastName.charAt(0) + ".";
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getNazn() {
        return nazn;
    }

    public void setNazn(String nazn) {
        this.nazn = nazn;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }
}
