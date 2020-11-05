/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class PlanOrder {
    private Long idOfPlanOrder;
    private Org org;
    private String groupName;
    private Client client;
    private Date planDate;
    private int idOfComplex;
    private String complexName;
    private User userRequestToPay;
    private Boolean toPay;
    private Long idOfOrder;
    private Order order;
    private User userConfirmToPay;
    private DiscountRule discountRule;
    private Date createDate;
    private Date lastUpdate;

    public PlanOrder(){ }

    public PlanOrder(Long idOfPlanOrder, Org org, String groupName, Client client, Date planDate, int idOfComplex,
            String complexName, User userRequestToPay, Boolean toPay,
            Long idOfOrder, User userConfirmToPay, DiscountRule discountRule){
        this.idOfPlanOrder = idOfPlanOrder;
        this.org = org;
        this.groupName = groupName;
        this.client = client;
        this.planDate = planDate;
        this.idOfComplex = idOfComplex;
        this.complexName = complexName;
        this.userRequestToPay = userRequestToPay;
        this.toPay = toPay;
        this.idOfOrder = idOfOrder;
        this.userConfirmToPay = userConfirmToPay;
        this.createDate = new Date();
        this.lastUpdate = this.createDate;
        this.discountRule = discountRule;
    }



    public Long getIdOfPlanOrder() {
        return idOfPlanOrder;
    }

    private void setIdOfPlanOrder(Long idOfPlanOrder) {
        this.idOfPlanOrder = idOfPlanOrder;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public User getUserRequestToPay() {
        return userRequestToPay;
    }

    public void setUserRequestToPay(User classroomTeacher) {
        this.userRequestToPay = classroomTeacher;
    }

    public Boolean getToPay() {
        return toPay;
    }

    public void setToPay(Boolean toPay) {
        this.toPay = toPay;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUserConfirmToPay() {
        return userConfirmToPay;
    }

    public void setUserConfirmToPay(User userConfirmToPay) {
        this.userConfirmToPay = userConfirmToPay;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public DiscountRule getDiscountRule() { return discountRule; }

    public void setDiscountRule(DiscountRule discountRule) { this.discountRule = discountRule; }
}
