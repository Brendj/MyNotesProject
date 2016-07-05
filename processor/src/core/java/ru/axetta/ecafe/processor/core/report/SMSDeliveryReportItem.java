/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.12.14
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class SMSDeliveryReportItem implements Serializable {
    private int uniqueId;
    private long orgId;
    private String district;
    private String orgName;
    private int columnId;
    private String shortNameInfoService;
    private String shortAddress;
    private String introductionQueue;
    private String orgStatus;
    public Map<String, String> values;
    public Boolean isEmptyValues;

    public SMSDeliveryReportItem() {
    }

    public SMSDeliveryReportItem(int uniqueId, long orgId, String district, String orgName, int columnId,
            String shortNameInfoService, String shortAddress, String introductionQueue, String orgStatus) {
        this.uniqueId = uniqueId;
        this.orgId = orgId;
        this.district = district;
        this.orgName = orgName;
        this.columnId = columnId;
        this.shortNameInfoService = shortNameInfoService;
        this.shortAddress = shortAddress;
        this.introductionQueue = introductionQueue;
        this.orgStatus = orgStatus;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getIntroductionQueue() {
        return introductionQueue;
    }

    public void setIntroductionQueue(String introductionQueue) {
        this.introductionQueue = introductionQueue;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {
        this.orgStatus = orgStatus;
    }

    public Boolean getIsEmptyValues() {
        return isEmptyValues;
    }

    public void setIsEmptyValues(Boolean emptyValues) {
        isEmptyValues = emptyValues;
    }

    public void setIsEmptyValues() {
        if(values == null || values.isEmpty()){
            setIsEmptyValues(true);
        } else {
            setIsEmptyValues(false);
        }
    }

    public Map<String, String> getValues() {
        if(values == null) {
            return Collections.EMPTY_MAP;
        } else {
            return values;
        }
    }

    public void addValue(String k, String v) {
        if (values == null) {
            values = new HashMap<String, String>();
        }
        values.put(k, v);
    }
}