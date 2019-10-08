/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.01.15
 * Time: 15:34
 */
@Deprecated
public class DailyOrgRegistries {

    /**
     * Номер по организации
     */
    private Long idOfDailyOrgRegistries;
    /**
     * Номер организации
     */
    private String orgNum;
    /**
     * Идентификатор организауии
     */
    private Org idOfOrg;
    /**
     * Название организации
     */
    private String officialName;
    /**
     * Адрес организации
     */
    private String address;
    /**
     * Сумма балансов
     */
    private Long totalBalance;
    /**
     * Сумма пополнений
     */
    private Long rechargeAmount;
    /**
     * Сумма продаж
     */
    private Long salesAmount;
    /**
     * Дата создания
     */
    private Date createdDate;
    /**
     * Ссылка на таблицу поставщики ТСП.
     */
    private DailyFormationRegistries dailyFormationRegistries;

    public DailyOrgRegistries() {
    }

    public DailyOrgRegistries(Long idOfDailyOrgRegistries, String orgNum, Org idOfOrg, String officialName,
            String address, Long totalBalance, Long rechargeAmount, Long salesAmount,
            DailyFormationRegistries dailyFormationRegistries, Date createdDate) {
        this.idOfDailyOrgRegistries = idOfDailyOrgRegistries;
        this.orgNum = orgNum;
        this.idOfOrg = idOfOrg;
        this.officialName = officialName;
        this.address = address;
        this.totalBalance = totalBalance;
        this.rechargeAmount = rechargeAmount;
        this.salesAmount = salesAmount;
        this.dailyFormationRegistries = dailyFormationRegistries;
        this.createdDate = createdDate;
    }

    public Long getIdOfDailyOrgRegistries() {
        return idOfDailyOrgRegistries;
    }

    public void setIdOfDailyOrgRegistries(Long idOfOrgRegistries) {
        this.idOfDailyOrgRegistries = idOfOrgRegistries;
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

    public DailyFormationRegistries getDailyFormationRegistries() {
        return dailyFormationRegistries;
    }

    public void setDailyFormationRegistries(DailyFormationRegistries dailyFormationRegistries) {
        this.dailyFormationRegistries = dailyFormationRegistries;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
