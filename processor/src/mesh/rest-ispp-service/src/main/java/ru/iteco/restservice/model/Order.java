/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.OrderCompositeId;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_orders")
public class Order {
    public static final int DISCOUNT_TYPE = 4;
    public static final int DISCOUNT_TYPE_RESERVE = 6;

    @EmbeddedId
    private OrderCompositeId compositeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idofclient")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetailSet;

    @Column(name = "orderdate")
    private Long orderDate;

    @Column(name = "createddate")
    private Long createdDate;

    @Column(name = "ordertype")
    private Integer orderType;

    @Column(name = "rsum")
    private Long rsum;

    @Column(name = "state")
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Set<OrderDetail> getOrderDetailSet() {
        return orderDetailSet;
    }

    public void setOrderDetailSet(Set<OrderDetail> orderDetailSet) {
        this.orderDetailSet = orderDetailSet;
    }

    public OrderCompositeId getCompositeId() {
        return compositeId;
    }

    public void setCompositeId(OrderCompositeId compositeKey) {
        this.compositeId = compositeKey;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getRsum() {
        return rsum;
    }

    public void setRsum(Long rsum) {
        this.rsum = rsum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(compositeId, order.compositeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeId);
    }
}
