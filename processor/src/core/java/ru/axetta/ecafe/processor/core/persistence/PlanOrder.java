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
    private ComplexInfo complexInfo;
    private String complexName;
    private User classroomTeacher;
    private Boolean toPay;
    private Order order;
    private User productionDirector;
    private Date createDate;
    private Date lastUpdate;

    public PlanOrder(){

    }

    public PlanOrder(Org org, String groupName, Client client, Date planDate, ComplexInfo complexInfo, String complexName,
            User classroomTeacher, Boolean toPay, Order order, User productionDirector){
        this.org = org;
        this.groupName = groupName;
        this.client = client;
        this.planDate = planDate;
        this.complexInfo = complexInfo;
        this.complexName = complexName;
        this.classroomTeacher = classroomTeacher;
        this.toPay = toPay;
        this.order = order;
        this.productionDirector = productionDirector;
        this.createDate = new Date();
        this.lastUpdate = this.createDate;
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

    public ComplexInfo getComplexInfo() {
        return complexInfo;
    }

    public void setComplexInfo(ComplexInfo complexInfo) {
        this.complexInfo = complexInfo;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public User getClassroomTeacher() {
        return classroomTeacher;
    }

    public void setClassroomTeacher(User classroomTeacher) {
        this.classroomTeacher = classroomTeacher;
    }

    public Boolean getToPay() {
        return toPay;
    }

    public void setToPay(Boolean toPay) {
        this.toPay = toPay;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getProductionDirector() {
        return productionDirector;
    }

    public void setProductionDirector(User productionDirector) {
        this.productionDirector = productionDirector;
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
}
