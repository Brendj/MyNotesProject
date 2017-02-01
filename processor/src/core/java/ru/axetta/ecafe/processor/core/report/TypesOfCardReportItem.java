package ru.axetta.ecafe.processor.core.report;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 06.11.14
 * Time: 16:48
 */

public class TypesOfCardReportItem {

    private String region;

    private Long servActCount;
    private Long servBlockCount;

    private Long skuActCount;
    private Long skuBlockCount;

    private Long othActCount;
    private Long othBlockCount;

    private Long allActCount;
    private Long allBlockCount;

    private Long skmActCount;
    private Long skmBlockCount;

    private Long clockActCount;
    private Long clockBlockCount;

    private Long socActCount;
    private Long socBlockCount;

    private List<TypesOfCardSubreportItem> typesOfCardSubeportItems;

    public TypesOfCardReportItem(String region, Long servActCount, Long servBlockCount, Long skuActCount,
            Long skuBlockCount, Long othActCount, Long othBlockCount, Long allActCount, Long allBlockCount,
            Long skmActCount, Long skmBlockCount, Long clockActCount, Long clockBlockCount,
            Long socActCount, Long socBlockCount) {
        this.region = region;
        this.servActCount = servActCount;
        this.servBlockCount = servBlockCount;
        this.skuActCount = skuActCount;
        this.skuBlockCount = skuBlockCount;
        this.othActCount = othActCount;
        this.othBlockCount = othBlockCount;
        this.allActCount = allActCount;
        this.allBlockCount = allBlockCount;
        this.skmActCount = skmActCount;
        this.skmBlockCount = skmBlockCount;
        this.clockActCount = clockActCount;
        this.clockBlockCount = clockBlockCount;
        this.socActCount = socActCount;
        this.socBlockCount = socBlockCount;
    }

    public TypesOfCardReportItem() {
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

    public Long getAllActCount() {
        return allActCount;
    }

    public void setAllActCount(Long allActCount) {
        this.allActCount = allActCount;
    }

    public Long getAllBlockCount() {
        return allBlockCount;
    }

    public void setAllBlockCount(Long allBlockCount) {
        this.allBlockCount = allBlockCount;
    }

    public List<TypesOfCardSubreportItem> getTypesOfCardSubeportItems() {
        return typesOfCardSubeportItems;
    }

    public void setTypesOfCardSubeportItems(List<TypesOfCardSubreportItem> typesOfCardSubeportItems) {
        this.typesOfCardSubeportItems = typesOfCardSubeportItems;
    }

    public Long getSkmActCount() {
        return skmActCount;
    }

    public void setSkmActCount(Long skmActCount) {
        this.skmActCount = skmActCount;
    }

    public Long getSkmBlockCount() {
        return skmBlockCount;
    }

    public void setSkmBlockCount(Long skmBlockCount) {
        this.skmBlockCount = skmBlockCount;
    }

    public Long getClockActCount() {
        return clockActCount;
    }

    public void setClockActCount(Long clockActCount) {
        this.clockActCount = clockActCount;
    }

    public Long getClockBlockCount() {
        return clockBlockCount;
    }

    public void setClockBlockCount(Long clockBlockCount) {
        this.clockBlockCount = clockBlockCount;
    }

    public Long getSocActCount() {
        return socActCount;
    }

    public void setSocActCount(Long socActCount) {
        this.socActCount = socActCount;
    }

    public Long getSocBlockCount() {
        return socBlockCount;
    }

    public void setSocBlockCount(Long socBlockCount) {
        this.socBlockCount = socBlockCount;
    }
}
