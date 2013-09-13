/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.complianceWithOrderAndConsumption;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 12.09.13
 * Time: 14:51
 */

public class CWOACItem {

    private String orgName;
    private Long ordersCount;
    private Long consumedCount;
    private Long difference;
    private BigDecimal diffPercent;
    private Long writtenOffCount;
    private BigDecimal writtenOffPercent;

    public CWOACItem(String orgName, Long ordersCount, Long consumedCount, Long writtenOffCount) {
        this.orgName = orgName;
        this.ordersCount = ordersCount;
        this.consumedCount = consumedCount;
        this.difference = ordersCount - consumedCount;
        this.diffPercent = consumedCount == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN)
                : BigDecimal.valueOf(100 * difference / (double) consumedCount).setScale(2, RoundingMode.HALF_EVEN);
        this.writtenOffCount = writtenOffCount;
        this.writtenOffPercent = consumedCount == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN)
                : BigDecimal.valueOf(100 * writtenOffCount / (double) consumedCount)
                        .setScale(2, RoundingMode.HALF_EVEN);
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(Long ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Long getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(Long consumedCount) {
        this.consumedCount = consumedCount;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }

    public BigDecimal getDiffPercent() {
        return diffPercent;
    }

    public void setDiffPercent(BigDecimal diffPercent) {
        this.diffPercent = diffPercent;
    }

    public Long getWrittenOffCount() {
        return writtenOffCount;
    }

    public void setWrittenOffCount(Long writtenOffCount) {
        this.writtenOffCount = writtenOffCount;
    }

    public BigDecimal getWrittenOffPercent() {
        return writtenOffPercent;
    }

    public void setWrittenOffPercent(BigDecimal writtenOffPercent) {
        this.writtenOffPercent = writtenOffPercent;
    }
}
