/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.charts;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 01.07.17
 * Time: 10:37
 */

public class DataItem {
    private Long idOfOrg;
    private String label;
    private Integer value;

    public DataItem(Long idOfOrg, String label, Integer value) {
        this.idOfOrg = idOfOrg;
        this.label = label;
        this.value = value;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
