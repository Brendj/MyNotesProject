package ru.axetta.ecafe.processor.web.ui.card;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.11.14
 * Time: 16:48
 */

public class TypesOfCardReportItem {

    private String orgName;
    private String address;
    private String region;

    private Long servActCount;
    private Long servBlockCount;

    private Long skuActCount;
    private Long skuBlockCount;

    private Long othActCount;
    private Long othBlockCount;

    public TypesOfCardReportItem(String orgName, String address, String region, Long servActCount, Long servBlockCount,
            Long skuActCount, Long skuBlockCount, Long othActCount, Long othBlockCount) {
        this.orgName = orgName;
        this.address = address;
        this.region = region;
        this.servActCount = servActCount;
        this.servBlockCount = servBlockCount;
        this.skuActCount = skuActCount;
        this.skuBlockCount = skuBlockCount;
        this.othActCount = othActCount;
        this.othBlockCount = othBlockCount;
    }

    public TypesOfCardReportItem() {
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getServActCount() {
        return servActCount;
    }

    public void setServActCount(Long servActCount) {
        this.servActCount = servActCount;
    }

    public Long getServBlockCount() {
        return servBlockCount;
    }

    public void setServBlockCount(Long servBlockCount) {
        this.servBlockCount = servBlockCount;
    }

    public Long getSkuActCount() {
        return skuActCount;
    }

    public void setSkuActCount(Long skuActCount) {
        this.skuActCount = skuActCount;
    }

    public Long getSkuBlockCount() {
        return skuBlockCount;
    }

    public void setSkuBlockCount(Long skuBlockCount) {
        this.skuBlockCount = skuBlockCount;
    }

    public Long getOthActCount() {
        return othActCount;
    }

    public void setOthActCount(Long othActCount) {
        this.othActCount = othActCount;
    }

    public Long getOthBlockCount() {
        return othBlockCount;
    }

    public void setOthBlockCount(Long othBlockCount) {
        this.othBlockCount = othBlockCount;
    }
}
