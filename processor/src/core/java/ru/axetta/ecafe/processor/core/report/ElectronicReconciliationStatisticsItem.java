/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 12:59
 */

public class ElectronicReconciliationStatisticsItem {

    private Long rowNum;                // Номер по порядку
    private String orgName;             // Название организации
    private String orgType;             // Тип ОО
    private String district;            // Округ
    private String address;             // Адресс ОО

    private String verificationStatus;  // Статус сверки ОО
    private String verificationStatusPowerSupplier; // Статус сверки ПП (поставщик питания)

    public ElectronicReconciliationStatisticsItem() {
    }

    public ElectronicReconciliationStatisticsItem(Long rowNum, String orgName, String orgType, String district,
            String address, String verificationStatus, String verificationStatusPowerSupplier) {
        this.rowNum = rowNum;
        this.orgName = orgName;
        this.orgType = orgType;
        this.district = district;
        this.address = address;
        this.verificationStatus = verificationStatus;
        this.verificationStatusPowerSupplier = verificationStatusPowerSupplier;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationStatusPowerSupplier() {
        return verificationStatusPowerSupplier;
    }

    public void setVerificationStatusPowerSupplier(String verificationStatusPowerSupplier) {
        this.verificationStatusPowerSupplier = verificationStatusPowerSupplier;
    }
}
