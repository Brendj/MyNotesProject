/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.models;

import ru.iteco.msp.models.compositeId.OrderDetailCompositeId;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_orderdetails")
/*@NamedEntityGraph(
        name = "orderDetail.rule",
        attributeNodes = {
                @NamedAttributeNode(value = "rule", subgraph = "rule.categoryDiscounts")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "rule.categoryDiscounts",
                        attributeNodes = @NamedAttributeNode("categoryDiscounts")
                )
        }
)*/
public class OrderDetail {
    public static final int TYPE_COMPLEX_MIN = 50;
    public static final int TYPE_COMPLEX_MAX = 99;

    @EmbeddedId
    private OrderDetailCompositeId compositeId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="idoforder", insertable = false, updatable = false),
            @JoinColumn(name="idoforderdetail", insertable = false, updatable = false)
    })
    private Order order;

    @ManyToOne
    @JoinColumn(name = "idofrule", insertable = false, updatable = false)
    private DiscountRule rule;

    @ManyToOne
    @JoinColumn(name = "idofrule", insertable = false, updatable = false)
    private WtDiscountRule wtRule;

    @Column(name = "menudetailname")
    private String menuDetailName;

    @Column(name = "menutype")
    private Integer menuType;

    public WtDiscountRule getWtRule() {
        return wtRule;
    }

    public void setWtRule(WtDiscountRule wtRule) {
        this.wtRule = wtRule;
    }

    public DiscountRule getRule() {
        return rule;
    }

    public void setRule(DiscountRule rule) {
        this.rule = rule;
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
