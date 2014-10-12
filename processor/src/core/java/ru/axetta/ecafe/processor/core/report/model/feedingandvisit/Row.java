/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

/**
 * User: shamil
 * Date: 06.10.14
 * Time: 13:55
 */
public class Row {
    private Long clientId;
    private String name;

    private Integer day;

    private String entry = "Н";
    private Integer color = 0;

    public Row() {
    }

    public Row(String name, Integer day) {
        this.name = name;
        this.day = day;
    }

    public Row(Long clientId, String name, Integer day, String entry, Integer color) {
        this.clientId = clientId;
        this.name = name;
        this.day = day;
        this.entry = entry;
        this.color = color;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
