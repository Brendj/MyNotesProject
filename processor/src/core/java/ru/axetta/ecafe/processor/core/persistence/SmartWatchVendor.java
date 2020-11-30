/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Objects;

public class SmartWatchVendor {
    private Long idOfVendor;
    private String name;
    private String apiKey;
    private Boolean enableService = false;
    private Integer cardSignCertNum;
    private Boolean enablePushes = false;
    private String enterEventsEndPoint;
    private String purchasesEndPoint;
    private String paymentEndPoint;

    public SmartWatchVendor(){

    }

    public Long getIdOfVendor() {
        return idOfVendor;
    }

    public void setIdOfVendor(Long idOfVendor) {
        this.idOfVendor = idOfVendor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getEnableService() {
        return enableService;
    }

    public void setEnableService(Boolean enableService) {
        this.enableService = enableService;
    }

    public Integer getCardSignCertNum() {
        return cardSignCertNum;
    }

    public void setCardSignCertNum(Integer cardSignCertNum) {
        this.cardSignCertNum = cardSignCertNum;
    }

    public Boolean getEnablePushes() {
        return enablePushes;
    }

    public void setEnablePushes(Boolean enablePushes) {
        this.enablePushes = enablePushes;
    }

    public String getEnterEventsEndPoint() {
        return enterEventsEndPoint;
    }

    public void setEnterEventsEndPoint(String enterEventsEndPoint) {
        this.enterEventsEndPoint = enterEventsEndPoint;
    }

    public String getPurchasesEndPoint() {
        return purchasesEndPoint;
    }

    public void setPurchasesEndPoint(String purchasesEndPoint) {
        this.purchasesEndPoint = purchasesEndPoint;
    }

    public String getPaymentEndPoint() {
        return paymentEndPoint;
    }

    public void setPaymentEndPoint(String paymentEndPoint) {
        this.paymentEndPoint = paymentEndPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SmartWatchVendor vendor = (SmartWatchVendor) o;
        return Objects.equals(idOfVendor, vendor.idOfVendor) && Objects.equals(name, vendor.name) && Objects
                .equals(apiKey, vendor.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfVendor, name, apiKey);
    }
}
