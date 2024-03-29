/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class Item {

    private String district;
    private String orgTypeCategory;
    private String shortName;
    private String number;
    private String address;
    private Date currentDate;
    private Long requestCount;
    private Long forecastQty;
    private Long orderCount;
    private Long orderReserveCount;
    private Long idOfOrg;

    void fillOrgInfo(OrgItem item) {
        district = item.getDistrict();
        idOfOrg = item.getIdOfOrg();
        shortName = item.getOrgShortName();
        number = Org.extractOrgNumberFromName(shortName);
        address = item.getAddress();
        orgTypeCategory = item.getOrgTypeCategory();
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getOrgTypeCategory() {
        return orgTypeCategory;
    }

    public void setOrgTypeCategory(String orgTypeCategory) {
        this.orgTypeCategory = orgTypeCategory;
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

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public Double getPercent() {
        return (requestCount==null || orderCount ==null || requestCount==0)?0.0:(requestCount - orderCount)*100.0/requestCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getOrderReserveCount() {
        return orderReserveCount;
    }

    public void setOrderReserveCount(Long orderReserveCount) {
        this.orderReserveCount = orderReserveCount;
    }

    public Long getForecastQty() {
        return forecastQty;
    }

    public void setForecastQty(Long forecastQty) {
        this.forecastQty = forecastQty;
    }
}
