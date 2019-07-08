/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;
import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHoldRequestStatus;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import java.util.Date;

/**
 * Created by nuc on 04.10.2018.
 */
public class ClientBalanceHoldPageItem {
    private Long idOfClientBalanceHold;
    private Date createdDate;
    private Long contractId;
    private String fio;
    private Long declarerContractId;
    private String declarerPhone;
    private String declarerFio;
    private Long balance;
    private Long balanceHold;
    private String requestStatus;
    private String inn;
    private String rs;
    private String bank;
    private String bik;
    private String korr;

    public ClientBalanceHoldPageItem() {

    }

    public ClientBalanceHoldPageItem(ClientBalanceHold clientBalanceHold) {
        this.idOfClientBalanceHold = clientBalanceHold.getIdOfClientBalanceHold();
        this.createdDate = clientBalanceHold.getCreatedDate();
        this.contractId = clientBalanceHold.getClient().getContractId();
        this.fio = clientBalanceHold.getClient().getPerson().getFullName();
        if (clientBalanceHold.getDeclarer() != null) {
            this.declarerContractId = clientBalanceHold.getDeclarer().getContractId();
            this.declarerFio = clientBalanceHold.getDeclarer().getPerson().getFullName();
        }
        this.declarerPhone = clientBalanceHold.getPhoneOfDeclarer();
        this.balance = clientBalanceHold.getClient().getBalance();
        this.balanceHold = clientBalanceHold.getHoldSum();
        this.requestStatus = clientBalanceHold.getRequestStatus().toString();
        this.inn = clientBalanceHold.getDeclarerInn();
        this.rs = clientBalanceHold.getDeclarerAccount();
        this.bank = clientBalanceHold.getDeclarerBank();
        this.bik = clientBalanceHold.getDeclarerBik();
        this.korr = clientBalanceHold.getDeclarerCorrAccount();
    }

    public boolean showButton() {
        if (this.requestStatus.equals(ClientBalanceHoldRequestStatus.SUBSCRIBED.toString())) return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientBalanceHoldPageItem)) return false;
        return idOfClientBalanceHold != null && idOfClientBalanceHold.equals(((ClientBalanceHoldPageItem) o).getIdOfClientBalanceHold());
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreatedDateStr() {
        return CalendarUtils.dateTimeToString(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Long getDeclarerContractId() {
        return declarerContractId;
    }

    public void setDeclarerContractId(Long declarerContractId) {
        this.declarerContractId = declarerContractId;
    }

    public String getDeclarerPhone() {
        return declarerPhone;
    }

    public void setDeclarerPhone(String declarerPhone) {
        this.declarerPhone = declarerPhone;
    }

    public String getDeclarerFio() {
        return declarerFio;
    }

    public void setDeclarerFio(String declarerFio) {
        this.declarerFio = declarerFio;
    }

    public Long getBalance() {
        return balance;
    }

    public String getBalanceStr() {
        return CurrencyStringUtils.copecksToRubles(balance);
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getBalanceHold() {
        return balanceHold;
    }

    public String getBalanceHoldStr() {
        return CurrencyStringUtils.copecksToRubles(balanceHold);
    }

    public void setBalanceHold(Long balanceHold) {
        this.balanceHold = balanceHold;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Long getIdOfClientBalanceHold() {
        return idOfClientBalanceHold;
    }

    public void setIdOfClientBalanceHold(Long idOfClientBalanceHold) {
        this.idOfClientBalanceHold = idOfClientBalanceHold;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getKorr() {
        return korr;
    }

    public void setKorr(String korr) {
        this.korr = korr;
    }
}
