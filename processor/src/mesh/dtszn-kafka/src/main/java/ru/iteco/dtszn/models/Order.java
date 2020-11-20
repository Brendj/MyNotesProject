/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import ru.iteco.dtszn.models.compositeId.OrderCompositeId;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_orders")
public class Order {
    private static final int DISCOUNT_TYPE = 4;
    private static final int DISCOUNT_TYPE_RESERVE = 6;

    @EmbeddedId
    private OrderCompositeId compositeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idoforg")
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
