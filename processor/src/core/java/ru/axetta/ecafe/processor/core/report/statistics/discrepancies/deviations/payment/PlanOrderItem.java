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

public class PlanOrderItem implements Comparable<PlanOrderItem> {

    public Long idOfClient;
    public String clientName;
    public Integer idOfComplex;
    public Long idOfRule;
    public Date orderDate;
    public String complexName;
    public String groupName;
    public Integer orderType;


    public PlanOrderItem(Long idOfClient, Integer idOfComplex, Long idOfRule, Date orderDate) {
        this.idOfClient = idOfClient;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
    }

    public PlanOrderItem(Long idOfClient, String clientName, Integer idOfComplex, Long idOfRule, Date orderDate,
            String groupName, String complexName) {
        this.idOfClient = idOfClient;
        this.clientName = clientName;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.groupName = groupName;
        this.complexName = complexName;
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

    public PlanOrderItem(Long idOfClient, String clientName, Integer idOfComplex, Long idOfRule, Date orderDate,
            String complexName, String groupName, Integer orderType) {
        this.idOfClient = idOfClient;
        this.clientName = clientName;
        this.idOfComplex = idOfComplex;
        this.idOfRule = idOfRule;
        this.orderDate = orderDate;
        this.complexName = complexName;
        this.groupName = groupName;
        this.orderType = orderType;
    }

    public Date getOrderDate() {
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

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlanOrderItem that = (PlanOrderItem) o;

        if (!idOfClient.equals(that.idOfClient)) {
            return false;
        }
        if (!idOfComplex.equals(that.idOfComplex)) {
            return false;
        }
        if (!idOfRule.equals(that.idOfRule)) {
            return false;
        }
        if (!orderDate.equals(that.orderDate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfClient.hashCode();
        result = 31 * result + idOfComplex.hashCode();
        result = 31 * result + idOfRule.hashCode();
        result = 31 * result + orderDate.hashCode();
        return result;
    }

    @Override
    public int compareTo(PlanOrderItem o) {
        int retCode = this.groupName.compareTo(o.getGroupName());
        if (retCode == 0)
            retCode = this.orderDate.compareTo(o.getOrderDate());
        if (0 == retCode)
            retCode = this.clientName.compareTo(o.getClientName());

        return retCode;
    }
}