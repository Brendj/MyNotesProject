/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 04.02.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class SentSmsItem implements Serializable {
    private String orgName;
    private Long ts;
    private List<SentSmsValue> value;

    public SentSmsItem(String orgName) {
        this.orgName = orgName;
    }

    public SentSmsItem(String orgName, Long ts, List<SentSmsValue> value) {
        this.orgName = orgName;
        this.ts = ts;
        this.value = value;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public List<SentSmsValue> getValue() {
        return value;
    }

    public void setValue(List<SentSmsValue> value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SentSmsItem that = (SentSmsItem) o;
        return Objects.equals(orgName, that.orgName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgName);
    }
}
