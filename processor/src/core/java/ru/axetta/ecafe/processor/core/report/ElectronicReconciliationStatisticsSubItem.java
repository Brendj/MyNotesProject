/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 18.10.16
 * Time: 13:01
 */

public class ElectronicReconciliationStatisticsSubItem implements Comparable<ElectronicReconciliationStatisticsSubItem> {

    private String date; // Дата
    private Date taloonDate;
    private Long complexCount;

    private Long verificationStatusAgreed;        // Статус сверки ОО Согласовано
    private Long verificationStatusNotIndicated;  // Статус сверки ОО Не указано

    private Long powerSupplierStatusAgreed;         // Статус сверки ПП (поставщик питания) Согласовано
    private Long powerSupplierStatusNotIndicated;   // Статус сверки ПП (поставщик питания) Не указано
    private Long powerSupplierStatusRenouncement; // Статус сверки ПП (поставщик питания) Отказ

    public ElectronicReconciliationStatisticsSubItem() {
    }

    public ElectronicReconciliationStatisticsSubItem(String date, Date taloonDate, Long complexCount,
            Long verificationStatusAgreed, Long verificationStatusNotIndicated, Long powerSupplierStatusAgreed,
            Long powerSupplierStatusNotIndicated, Long powerSupplierStatusRenouncement) {
        this.date = date;
        this.taloonDate = taloonDate;
        this.complexCount = complexCount;
        this.verificationStatusAgreed = verificationStatusAgreed;
        this.verificationStatusNotIndicated = verificationStatusNotIndicated;
        this.powerSupplierStatusAgreed = powerSupplierStatusAgreed;
        this.powerSupplierStatusNotIndicated = powerSupplierStatusNotIndicated;
        this.powerSupplierStatusRenouncement = powerSupplierStatusRenouncement;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getTaloonDate() {
        return taloonDate;
    }

    public void setTaloonDate(Date taloonDate) {
        this.taloonDate = taloonDate;
    }

    public Long getComplexCount() {
        return complexCount;
    }

    public void setComplexCount(Long complexCount) {
        this.complexCount = complexCount;
    }

    public Long getVerificationStatusAgreed() {
        return verificationStatusAgreed;
    }

    public void setVerificationStatusAgreed(Long verificationStatusAgreed) {
        this.verificationStatusAgreed = verificationStatusAgreed;
    }

    public Long getVerificationStatusNotIndicated() {
        return verificationStatusNotIndicated;
    }

    public void setVerificationStatusNotIndicated(Long verificationStatusNotIndicated) {
        this.verificationStatusNotIndicated = verificationStatusNotIndicated;
    }

    public Long getPowerSupplierStatusAgreed() {
        return powerSupplierStatusAgreed;
    }

    public void setPowerSupplierStatusAgreed(Long powerSupplierStatusAgreed) {
        this.powerSupplierStatusAgreed = powerSupplierStatusAgreed;
    }

    public Long getPowerSupplierStatusNotIndicated() {
        return powerSupplierStatusNotIndicated;
    }

    public void setPowerSupplierStatusNotIndicated(Long powerSupplierStatusNotIndicated) {
        this.powerSupplierStatusNotIndicated = powerSupplierStatusNotIndicated;
    }

    public Long getPowerSupplierStatusRenouncement() {
        return powerSupplierStatusRenouncement;
    }

    public void setPowerSupplierStatusRenouncement(Long powerSupplierStatusRenouncement) {
        this.powerSupplierStatusRenouncement = powerSupplierStatusRenouncement;
    }

    @Override
    public int compareTo(ElectronicReconciliationStatisticsSubItem o) {
        int retCode = this.taloonDate.compareTo(o.getTaloonDate());
        return retCode;
    }

    @Override
    public int hashCode() {
        int result = complexCount.hashCode();
        result = 31 * result + verificationStatusAgreed.hashCode();
        result = 31 * result + verificationStatusNotIndicated.hashCode();
        result = 31 * result + powerSupplierStatusRenouncement.hashCode();
        return result;
    }
}


