/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models;

import ru.iteco.dtszn.models.compositkey.OrderCompositeKey;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_orders")
public class Order {
    @EmbeddedId
    private OrderCompositeKey compositeKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idoforg")
    private Org org;

    public OrderCompositeKey getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(OrderCompositeKey compositeKey) {
        this.compositeKey = compositeKey;
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
        return Objects.equals(compositeKey, order.compositeKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeKey);
    }
}
