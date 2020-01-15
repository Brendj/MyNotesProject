/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 19.01.15
 * Time: 17:59
 */
@Deprecated
public class StornedOrdersOrganizationSalesAmount {

    public Long idOfOrg;
    public Long stornedSalesAmount;
    public Date ordersDate;
    public Date generateStornDate;

    public StornedOrdersOrganizationSalesAmount() {
    }

    public StornedOrdersOrganizationSalesAmount(Long idOfOrg, Long stornedSalesAmount, Date ordersDate,
            Date generateStornDate) {
        this.idOfOrg = idOfOrg;
        this.stornedSalesAmount = stornedSalesAmount;
        this.ordersDate = ordersDate;
        this.generateStornDate = generateStornDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getStornedSalesAmount() {
        return stornedSalesAmount;
    }

    public void setStornedSalesAmount(Long stornedSalesAmount) {
        this.stornedSalesAmount = stornedSalesAmount;
    }

    public Date getOrdersDate() {
        return ordersDate;
    }

    public void setOrdersDate(Date ordersDate) {
        this.ordersDate = ordersDate;
    }

    public Date getGenerateStornDate() {
        return generateStornDate;
    }

    public void setGenerateStornDate(Date generateStornDate) {
        this.generateStornDate = generateStornDate;
    }

}
