/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by anvarov on 06.07.2017.
 */
public class OrgNameAndAddress implements Comparable<OrgNameAndAddress>  {

    private String condition;
    private String item;

    public OrgNameAndAddress(String condition, String item) {
        this.condition = condition;
        this.item = item;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        return condition.hashCode();
    }

    @Override
    public int compareTo(OrgNameAndAddress o) {
        return o.getCondition().compareTo(this.condition);
    }
}
