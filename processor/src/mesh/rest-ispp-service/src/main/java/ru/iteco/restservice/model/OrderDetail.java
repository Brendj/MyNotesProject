/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.OrderDetailCompositeId;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_orderdetails")
public class OrderDetail {
    public static final int TYPE_COMPLEX_MIN = 50;
    public static final int TYPE_COMPLEX_MAX = 99;

    @EmbeddedId
    private OrderDetailCompositeId compositeId;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "rprice")
    private Long rprice;

    @Column(name = "menudetailname")
    private String menuDetailName;

    @Column(name = "menutype")
    private Integer menuType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="idoforder", insertable = false, updatable = false),
            @JoinColumn(name="idoforderdetail", insertable = false, updatable = false)
    })
    private Order order;

    @ManyToOne
    @JoinColumn()

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderDetailCompositeId getCompositeId() {
        return compositeId;
    }

    public void setCompositeId(OrderDetailCompositeId compositeId) {
        this.compositeId = compositeId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Long getRprice() {
        return rprice;
    }

    public void setRprice(Long rprice) {
        this.rprice = rprice;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDetail that = (OrderDetail) o;
        return Objects.equals(compositeId, that.compositeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeId);
    }
}
