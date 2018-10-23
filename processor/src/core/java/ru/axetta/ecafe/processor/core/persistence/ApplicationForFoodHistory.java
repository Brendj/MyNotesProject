/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ApplicationForFoodHistory {
    private Long idOfApplicationForFoodHistory;
    private ApplicationForFood applicationForFood;
    private ApplicationForFoodStatus status;
    private Date createdDate;
    private Date sendDate;
    private Long version;

    public ApplicationForFoodHistory(ApplicationForFood applicationForFood, ApplicationForFoodStatus status, Date sendDate, Long version) {
        this.applicationForFood = applicationForFood;
        this.status = status;
        this.createdDate = new Date();
        this.sendDate = sendDate;
        this.version = version;
    }

    public ApplicationForFoodHistory() {

    }

    public Long getIdOfApplicationForFoodHistory() {
        return idOfApplicationForFoodHistory;
    }

    public void setIdOfApplicationForFoodHistory(Long idOfApplicationForFoodHistory) {
        this.idOfApplicationForFoodHistory = idOfApplicationForFoodHistory;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public ApplicationForFoodStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationForFoodStatus status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
