/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.complianceWithOrderAndConsumption;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 12.09.13
 * Time: 14:51
 */

public class CWOACItem {

    private String orgName;
    private String district;
    private Long requestCount = 0L;
    private Long consumedCount = 0L;
    private Long writtenOffCount = 0L;

    public CWOACItem(String orgName, String district, Long requestCount) {
        this.orgName = orgName;
        this.district = district;
        this.requestCount = requestCount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
         this.requestCount = requestCount;
    }

    public Long getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(Long consumedCount) {
        this.consumedCount = consumedCount;
    }

    public Long getWrittenOffCount() {
        return writtenOffCount;
    }

    public void setWrittenOffCount(Long writtenOffCount) {
        this.writtenOffCount = writtenOffCount;
    }
}
