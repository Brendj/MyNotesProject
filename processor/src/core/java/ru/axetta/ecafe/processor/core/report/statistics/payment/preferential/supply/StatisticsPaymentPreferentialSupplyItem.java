/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsPaymentPreferentialSupplyItem {

    private String district;
    private String type;
    private String shortName;
    private String number;
    private String address;
    private Date paymentDate;
    private Long orderedCount;
    private Long actualPresenceCount;

    public StatisticsPaymentPreferentialSupplyItem() {}

    public void setOrgInfo(Org org){
        district = org.getDistrict();
        type = org.getType().toString();
        shortName = org.getShortName();
        number = Org.extractOrgNumberFromName(org.getOfficialName());
        address = org.getAddress();
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getOrderedCount() {
        return orderedCount;
    }

    public void setOrderedCount(Long orderedCount) {
        this.orderedCount = orderedCount;
    }

    public Long getActualPresenceCount() {
        return actualPresenceCount;
    }

    public void setActualPresenceCount(Long actualPresenceCount) {
        this.actualPresenceCount = actualPresenceCount;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
