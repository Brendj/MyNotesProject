/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 04.02.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class SentSmsItem implements Serializable {
    private Long uniqueId;
    private Long columnId;
    private String orgNum;
    private String date;
    private Long ts;
    private String value;

    public SentSmsItem(Long uniqueId, String orgNum, String date, Long ts, String value) {
        this.uniqueId = uniqueId;
        this.columnId = null;
        this.orgNum = orgNum;
        this.date = date;
        this.ts = ts;
        this.value = value;
    }

    public SentSmsItem(Long uniqueId, Long columnId, String orgNum, String date, Long ts, String value) {
        this.uniqueId = uniqueId;
        this.columnId = columnId;
        this.orgNum = orgNum;
        this.date = date;
        this.ts = ts;
        this.value = value;
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
