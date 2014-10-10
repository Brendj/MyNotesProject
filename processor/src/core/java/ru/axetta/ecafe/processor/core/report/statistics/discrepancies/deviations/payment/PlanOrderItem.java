/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 09.10.14
 * Time: 13:44
 */

public class PlanOrderItem  {

    public Long idOfClient;
    public Integer idOfComplex;
    public Long idOfRule;
    public Date orderDate;

    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
    }

    Date getOrderDate() {
        return orderDate;
    }

    void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    Long getIdOfRule() {
        return idOfRule;
    }

    void setIdOfRule(Long idOfRule) {
        this.idOfRule = idOfRule;
    }

    Integer getIdOfComplex() {
        return idOfComplex;
    }

    void setIdOfComplex(Integer idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    Long getIdOfClient() {
        return idOfClient;
    }

    void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }
}