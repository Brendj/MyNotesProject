/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku.dataflow;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Order {
    @JsonProperty(value = "order_id")
    private Long idOfOrder;
    @JsonProperty(value = "organization_id")
    private Long idOfOrg;
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonProperty(value = "ordered_at")
    private Date orderDate;
    private List<Complex> complexes = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    @JsonProperty(value = "contract_id")
    private Long contractId;

    public Order(Long idOfOrder, Long idOfOrg, Date orderDate) {
        this.idOfOrder = idOfOrder;
        this.idOfOrg = idOfOrg;
        this.orderDate = orderDate;
    }

    public Order(Long contractId, Long idOfOrder, Long idOfOrg, Date orderDate) {
        this.contractId = contractId;
        this.idOfOrder = idOfOrder;
        this.idOfOrg = idOfOrg;
        this.orderDate = orderDate;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<Complex> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<Complex> complexes) {
        this.complexes = complexes;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}