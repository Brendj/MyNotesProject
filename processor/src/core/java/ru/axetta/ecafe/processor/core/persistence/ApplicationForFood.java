/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.Date;
import java.util.Set;

public class ApplicationForFood {
    private Long idOfApplicationForFood;
    private Client client;
    private Date createdDate;
    private Date discountDateStart;
    private Date discountDateEnd;
    private ApplicationForFoodStatus status;
    private String mobile;
    private String applicantName;
    private String applicantSecondName;
    private String applicantSurname;
    private Date lastUpdate;
    private Boolean archived;
    private String serviceNumber;
    private ApplicationForFoodCreatorType creatorType;
    private String idOfDocOrder;
    private Date docOrderDate;
    private Long version;
    private Boolean sendToAISContingent;
    private Date archiveDate;
    private Set<ApplicationForFoodHistory> applicationForFoodHistories;
    private Set<ApplicationForFoodDiscount> dtisznCodes;

    public ApplicationForFood(Client client, ApplicationForFoodStatus status, String mobile, String applicantName,
            String applicantSecondName, String applicantSurname, String serviceNumber, ApplicationForFoodCreatorType creatorType,
            String idOfDocOrder, Date docOrderDate, Long version) {
        this.client = client;
        this.createdDate = CalendarUtils.truncateToSecond(new Date()).getTime();
        this.status = status;
        this.mobile = mobile;
        this.applicantName = applicantName;
        this.applicantSecondName = applicantSecondName;
        this.applicantSurname = applicantSurname;
        this.lastUpdate = this.createdDate;
        this.version = version;
        this.archived = false;
        this.serviceNumber = serviceNumber;
        this.creatorType = creatorType;
        this.idOfDocOrder = idOfDocOrder;
        this.docOrderDate = docOrderDate;
        this.sendToAISContingent = false;
    }

    public ApplicationForFood() {

    }

    public boolean isInoe() {
        //Если льгота одна и она Иное, то true
        return (dtisznCodes != null && dtisznCodes.size() == 1 && dtisznCodes.iterator().next().getDtisznCode() == null);
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
        this.setArchiveDate(archived ? new Date() : null);
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

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public ApplicationForFoodCreatorType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(ApplicationForFoodCreatorType creatorType) {
        this.creatorType = creatorType;
    }

    public String getIdOfDocOrder() {
        return idOfDocOrder;
    }

    public void setIdOfDocOrder(String idOfDocOrder) {
        this.idOfDocOrder = idOfDocOrder;
    }

    public Date getDocOrderDate() {
        return docOrderDate;
    }

    public void setDocOrderDate(Date docOrderDate) {
        this.docOrderDate = docOrderDate;
    }

    public Boolean getSendToAISContingent() {
        return sendToAISContingent;
    }

    public void setSendToAISContingent(Boolean sendToAISContingent) {
        this.sendToAISContingent = sendToAISContingent;
    }

    public Date getDiscountDateStart() {
        return discountDateStart;
    }

    public void setDiscountDateStart(Date discountDateStart) {
        this.discountDateStart = discountDateStart;
    }

    public Date getDiscountDateEnd() {
        return discountDateEnd;
    }

    public void setDiscountDateEnd(Date discountDateEnd) {
        this.discountDateEnd = discountDateEnd;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Set<ApplicationForFoodDiscount> getDtisznCodes() {
        return dtisznCodes;
    }

    public void setDtisznCodes(Set<ApplicationForFoodDiscount> dtisznCodes) {
        this.dtisznCodes = dtisznCodes;
    }
}
