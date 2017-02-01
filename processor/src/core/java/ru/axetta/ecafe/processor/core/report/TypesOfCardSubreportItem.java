/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.11.14
 * Time: 17:40
 */

public class TypesOfCardSubreportItem {

    private String orgName;
    private String address;

    private Long servActOrgCount;
    private Long servBlockOrgCount;
    private Long skuActOrgCount;
    private Long skuBlockOrgCount;
    private Long othActOrgCount;
    private Long othBlockOrgCount;
    private Long allActOrgCount;
    private Long allBlockOrgCount;
    private Long skmActOrgCount;
    private Long skmBlockOrgCount;
    private Long clockActOrgCount;
    private Long clockBlockOrgCount;
    private Long socActOrgCount;
    private Long socBlockOrgCount;

    public TypesOfCardSubreportItem() {
    }

    public TypesOfCardSubreportItem(String orgName, String address, Long servActOrgCount, Long servBlockOrgCount,
            Long skuActOrgCount, Long skuBlockOrgCount, Long othActOrgCount, Long othBlockOrgCount, Long allActOrgCount,
            Long allBlockOrgCount, Long skmActOrgCount, Long skmBlockOrgCount, Long clockActOrgCount, Long clockBlockOrgCount,
            Long socActOrgCount, Long socBlockOrgCount) {
        this.orgName = orgName;
        this.address = address;
        this.servActOrgCount = servActOrgCount;
        this.servBlockOrgCount = servBlockOrgCount;
        this.skuActOrgCount = skuActOrgCount;
        this.skuBlockOrgCount = skuBlockOrgCount;
        this.othActOrgCount = othActOrgCount;
        this.othBlockOrgCount = othBlockOrgCount;
        this.allActOrgCount = allActOrgCount;
        this.allBlockOrgCount = allBlockOrgCount;
        this.skmActOrgCount = skmActOrgCount;
        this.skmBlockOrgCount = skmBlockOrgCount;
        this.clockActOrgCount = clockActOrgCount;
        this.clockBlockOrgCount = clockBlockOrgCount;
        this.socActOrgCount = socActOrgCount;
        this.socBlockOrgCount = socBlockOrgCount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getServActOrgCount() {
        return servActOrgCount;
    }

    public void setServActOrgCount(Long servActOrgCount) {
        this.servActOrgCount = servActOrgCount;
    }

    public Long getServBlockOrgCount() {
        return servBlockOrgCount;
    }

    public void setServBlockOrgCount(Long servBlockOrgCount) {
        this.servBlockOrgCount = servBlockOrgCount;
    }

    public Long getSkuActOrgCount() {
        return skuActOrgCount;
    }

    public void setSkuActOrgCount(Long skuActOrgCount) {
        this.skuActOrgCount = skuActOrgCount;
    }

    public Long getSkuBlockOrgCount() {
        return skuBlockOrgCount;
    }

    public void setSkuBlockOrgCount(Long skuBlockOrgCount) {
        this.skuBlockOrgCount = skuBlockOrgCount;
    }

    public Long getOthActOrgCount() {
        return othActOrgCount;
    }

    public void setOthActOrgCount(Long othActOrgCount) {
        this.othActOrgCount = othActOrgCount;
    }

    public Long getOthBlockOrgCount() {
        return othBlockOrgCount;
    }

    public void setOthBlockOrgCount(Long othBlockOrgCount) {
        this.othBlockOrgCount = othBlockOrgCount;
    }

    public Long getAllActOrgCount() {
        return allActOrgCount;
    }

    public void setAllActOrgCount(Long allActOrgCount) {
        this.allActOrgCount = allActOrgCount;
    }

    public Long getAllBlockOrgCount() {
        return allBlockOrgCount;
    }

    public void setAllBlockOrgCount(Long allBlockOrgCount) {
        this.allBlockOrgCount = allBlockOrgCount;
    }

    public Long getSkmActOrgCount() {
        return skmActOrgCount;
    }

    public void setSkmActOrgCount(Long skmActOrgCount) {
        this.skmActOrgCount = skmActOrgCount;
    }

    public Long getSkmBlockOrgCount() {
        return skmBlockOrgCount;
    }

    public void setSkmBlockOrgCount(Long skmBlockOrgCount) {
        this.skmBlockOrgCount = skmBlockOrgCount;
    }

    public Long getClockActOrgCount() {
        return clockActOrgCount;
    }

    public void setClockActOrgCount(Long clockActOrgCount) {
        this.clockActOrgCount = clockActOrgCount;
    }

    public Long getClockBlockOrgCount() {
        return clockBlockOrgCount;
    }

    public void setClockBlockOrgCount(Long clockBlockOrgCount) {
        this.clockBlockOrgCount = clockBlockOrgCount;
    }

    public Long getSocActOrgCount() {
        return socActOrgCount;
    }

    public void setSocActOrgCount(Long socActOrgCount) {
        this.socActOrgCount = socActOrgCount;
    }

    public Long getSocBlockOrgCount() {
        return socBlockOrgCount;
    }

    public void setSocBlockOrgCount(Long socBlockOrgCount) {
        this.socBlockOrgCount = socBlockOrgCount;
    }
}
