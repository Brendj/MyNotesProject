/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

/**
 * Created with IntelliJ IDEA.
 * User: a.anvarov
 */

public class ItemTotal {

    private String district;
    private String orgTypeCategory;
    private String shortName;
    private String number;
    private String address;

    private Long totalRequestCount;
    private Long totalForecastQty;
    private Long totalOrderCount;
    private Long totalOrderReserveCount;
    private Long idOfOrg;

    public ItemTotal(String district, String orgTypeCategory, String shortName, String number, String address,
            Long totalRequestCount, Long totalForecastQty, Long totalOrderCount, Long totalOrderReserveCount,
            Long idOfOrg) {
        this.district = district;
        this.orgTypeCategory = orgTypeCategory;
        this.shortName = shortName;
        this.number = number;
        this.address = address;
        this.totalRequestCount = totalRequestCount;
        this.totalForecastQty = totalForecastQty;
        this.totalOrderCount = totalOrderCount;
        this.totalOrderReserveCount = totalOrderReserveCount;
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

    public Long getTotalRequestCount() {
        return totalRequestCount;
    }

    public void setTotalRequestCount(Long totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    public Long getTotalForecastQty() {
        return totalForecastQty;
    }

    public void setTotalForecastQty(Long totalForecastQty) {
        this.totalForecastQty = totalForecastQty;
    }

    public Long getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Long totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public Long getTotalOrderReserveCount() {
        return totalOrderReserveCount;
    }

    public void setTotalOrderReserveCount(Long totalOrderReserveCount) {
        this.totalOrderReserveCount = totalOrderReserveCount;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Double getPercent() {
        return (totalRequestCount == null || totalOrderCount == null || totalRequestCount == 0) ? 0.0
                : (totalRequestCount - totalOrderCount) * 100.0 / totalRequestCount;
    }
}
