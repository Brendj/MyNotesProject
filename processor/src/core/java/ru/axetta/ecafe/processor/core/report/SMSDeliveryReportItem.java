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
    private String orgName;
    private int columnId;
    public Map<String, String> values;

    public SMSDeliveryReportItem() {
    }

    public SMSDeliveryReportItem(int uniqueId, String orgName, int columnId, Map<String, String> values) {
        this.uniqueId = uniqueId;
        this.orgName = orgName;
        this.columnId = columnId;
        this.values = values;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
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

    public Map<String, String> getValues() {
        if(values == null) {
            return Collections.EMPTY_MAP;
        } else {
            return values;
        }
    }

    public void addValue(String k, String v) {
        if(values == null) {
            values = new HashMap<String, String>();
        }
        values.put(k, v);
    }
}