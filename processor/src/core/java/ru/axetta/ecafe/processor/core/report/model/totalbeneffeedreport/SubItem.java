/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.totalbeneffeedreport;

/**
 * User: shamil
 * Date: 11.02.15
 * Time: 14:14
 */
public class SubItem {
    private int orderType;

    private long idoforg;
    private long idofclient;

    public SubItem(int orderType, long idofclient) {
        this.orderType = orderType;
        this.idofclient = idofclient;
    }

    public SubItem(long idoforg, long idofclient) {
        this.idoforg = idoforg;
        this.idofclient = idofclient;
    }

    public SubItem(long idofclient) {
        this.idofclient = idofclient;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

}
