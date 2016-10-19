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

    public String date; // Дата
    public Date taloonDate;
    private String verificationStatus;  // Статус сверки ОО
    private String verificationStatusPowerSupplier; // Статус сверки ПП (поставщик питания)

    public ElectronicReconciliationStatisticsSubItem() {
    }

    public ElectronicReconciliationStatisticsSubItem(String date, Date taloonDate, String verificationStatus,
            String verificationStatusPowerSupplier) {
        this.date = date;
        this.taloonDate = taloonDate;
        this.verificationStatus = verificationStatus;
        this.verificationStatusPowerSupplier = verificationStatusPowerSupplier;
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

    @Override
    public int compareTo(ElectronicReconciliationStatisticsSubItem o) {
        int retCode = this.taloonDate.compareTo(o.getTaloonDate());
        return retCode;
    }
}


