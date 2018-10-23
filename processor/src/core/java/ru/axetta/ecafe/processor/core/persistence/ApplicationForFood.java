/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ApplicationForFood {
    private Long idOfApplicationForFood;
    private Client client;
    private Long dtisznCode;
    private Date createdDate;
    private ApplicationForFoodStatus status;
    private String mobile;
    private String applicantName;
    private String applicantSecondName;
    private String applicantSurname;
    private Date lastUpdate;
    private Boolean archived;
    private Long version;
    private Set<ApplicationForFoodHistory> applicationForFoodHistories;

    public ApplicationForFood(Client client, Long dtisznCode, ApplicationForFoodStatus status, String mobile, String applicantName,
            String applicantSecondName, String applicantSurname, Long version) {
        this.client = client;
        this.dtisznCode = dtisznCode;
        this.createdDate = new Date();
        this.status = status;
        this.mobile = mobile;
        this.applicantName = applicantName;
        this.applicantSecondName = applicantSecondName;
        this.applicantSurname = applicantSurname;
        this.lastUpdate = new Date();
        this.version = version;
        this.archived = false;
    }

    public ApplicationForFood() {

    }

    public Long getIdOfApplicationForFood() {
        return idOfApplicationForFood;
    }

    public void setIdOfApplicationForFood(Long idOfApplicationForFood) {
        this.idOfApplicationForFood = idOfApplicationForFood;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Long dtisznCode) {
        this.dtisznCode = dtisznCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ApplicationForFoodStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationForFoodStatus status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantSecondName() {
        return applicantSecondName;
    }

    public void setApplicantSecondName(String applicantSecondName) {
        this.applicantSecondName = applicantSecondName;
    }

    public String getApplicantSurname() {
        return applicantSurname;
    }

    public void setApplicantSurname(String applicantSurname) {
        this.applicantSurname = applicantSurname;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<ApplicationForFoodHistory> getApplicationForFoodHistories() {
        return applicationForFoodHistories;
    }

    public void setApplicationForFoodHistories(Set<ApplicationForFoodHistory> applicationForFoodHistories) {
        this.applicationForFoodHistories = applicationForFoodHistories;
    }
}
