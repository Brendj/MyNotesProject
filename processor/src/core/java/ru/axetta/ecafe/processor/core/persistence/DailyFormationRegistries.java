/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.12.14
 * Time: 13:38
 */

public class DailyFormationRegistries {
    private Long idOFDailyFormationRegistries;
    private Date generatedDate;
    private Contragent idOfContragent;
    private String contragentName;
    private String orgNum;
    private Org idOfOrg;
    private String officialName;
    private String address;
    private Long totalBalance;
    private Long rechargeAmount;
    private Long salesAmount;

    public DailyFormationRegistries() {
    }

    public DailyFormationRegistries(Long idOFDailyFormationRegistries, Date generatedDate, Contragent idOfContragent,
            String contragentName, String orgNum, Org idOfOrg, String officialName, String address, Long totalBalance,
            Long rechargeAmount, Long salesAmount) {
        this.idOFDailyFormationRegistries = idOFDailyFormationRegistries;
        this.generatedDate = generatedDate;
        this.idOfContragent = idOfContragent;
        this.contragentName = contragentName;
        this.orgNum = orgNum;
        this.idOfOrg = idOfOrg;
        this.officialName = officialName;
        this.address = address;
        this.totalBalance = totalBalance;
        this.rechargeAmount = rechargeAmount;
        this.salesAmount = salesAmount;
    }

    public Long getIdOFDailyFormationRegistries() {
        return idOFDailyFormationRegistries;
    }

    public void setIdOFDailyFormationRegistries(Long idOFDailyFormationRegistries) {
        this.idOFDailyFormationRegistries = idOFDailyFormationRegistries;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public Contragent getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Contragent idOfContragent) {
        this.idOfContragent = idOfContragent;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public Org getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Org idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Long getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Long rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Long getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(Long salesAmount) {
        this.salesAmount = salesAmount;
    }
}
