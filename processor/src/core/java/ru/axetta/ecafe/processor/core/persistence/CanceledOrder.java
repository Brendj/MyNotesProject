/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.04.14
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class CanceledOrder {

    private Long idOfCanceledOrder;
    private Long idOfOrder;
    private Long idOfTransaction;
    private Org org;
    private Order order;
    private Date createTime;

    public CanceledOrder() {}

    public CanceledOrder(Order order, Org org) {
        this.order = order;
        //this.idOfOrder = order.getCompositeIdOfOrder().getIdOfOrder();
        //this.org = org;
        createTime = new Date();
    }

    public Long getIdOfCanceledOrder() {
        return idOfCanceledOrder;
    }

    void setIdOfCanceledOrder(Long idOfCanceledOrder) {
        this.idOfCanceledOrder = idOfCanceledOrder;
    }

    public Long getIdOfTransaction() {
        return idOfTransaction;
    }

    public void setIdOfTransaction(Long idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Org getOrg() {
        return org;
    }

    void setOrg(Org org) {
        this.org = org;
    }

    public Order getOrder() {
        return order;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public Date getCreateTime() {
        return createTime;
    }

    void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
