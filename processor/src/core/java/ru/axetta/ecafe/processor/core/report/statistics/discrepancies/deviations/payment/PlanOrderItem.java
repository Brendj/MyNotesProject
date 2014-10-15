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
    public String clientName;

    public Integer idOfComplex;
    public Long idOfRule;
    public Date orderDate;
    public String complexName; //complex name
    public String groupName;
    public Integer orderType;


    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
    }

    public PlanOrderItem(Long idOfClient,String clientName, Integer idOfComplex, Long idOfRule, Date orderDate, String groupName) {
        this.idOfClient = idOfClient;
        this.clientName = clientName;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.groupName = groupName;
    }
    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate, String complexName) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.complexName = complexName;
    }

    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate, String complexName,
            String groupName) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.complexName = complexName;
        this.groupName = groupName;
    }
    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate, String complexName,
            String groupName, Integer orderType) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.complexName = complexName;
        this.groupName = groupName;
        this.orderType = orderType;
    }
    public PlanOrderItem(Long idOfClient, String clientName, Integer idOfComplex, Long idOfRule, Date orderDate, String complexName,
            String groupName, Integer orderType) {
        this.idOfClient = idOfClient;
        this.clientName = clientName;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.complexName = complexName;
        this.groupName = groupName;
        this.orderType = orderType;
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

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}