/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.OrderDetailCompositeId;
import ru.iteco.restservice.model.enums.OrderDetailFRationType;
import ru.iteco.restservice.model.wt.WtDish;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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

    @Column(name = "idofmenufromsync")
    private Long idOfMenuFromSync;

    @Column(name = "fration")
    @Enumerated
    private OrderDetailFRationType rationType;

    @Column(name = "menuoutput")
    private String menuOutput;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="idoforder", insertable = false, updatable = false),
            @JoinColumn(name="idoforg", insertable = false, updatable = false)
    })
    private Order order;

    @ManyToOne
    @JoinColumn(name = "idofdish", insertable = false, updatable = false)
    private WtDish dish;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "idofmenufromsync", insertable = false, updatable = false)
    private Menu menu;

    public Long getIdOfMenuFromSync() {
        return idOfMenuFromSync;
    }

    public void setIdOfMenuFromSync(Long idOfMenuFromSync) {
        this.idOfMenuFromSync = idOfMenuFromSync;
    }

    public WtDish getDish() {
        return dish;
    }

    public void setDish(WtDish dish) {
        this.dish = dish;
    }

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

    public OrderDetailFRationType getRationType() {
        return rationType;
    }

    public void setRationType(OrderDetailFRationType rationType) {
        this.rationType = rationType;
    }

    public String getMenuOutput() {
        return menuOutput;
    }

    public void setMenuOutput(String menuOutput) {
        this.menuOutput = menuOutput;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
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
