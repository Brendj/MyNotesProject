/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApplicationForFoodReportItem {
    private String serviceNumber;
    protected Date createdDate;
    protected ApplicationForFoodStatus applicationForFoodStatus;
    private Date lastUpdate;
    private Long contractId;
    private String fio;
    private Long idOfOrg;
    private String orgName;
    private String benefit;
    private boolean isInoe;
    private ApplicationForFood applicationForFood;
    private boolean isChanged;
    private List<ApplicationForFoodStatus> statuses;
    private String mobile;

    public ApplicationForFoodReportItem() {

    }

    public ApplicationForFoodReportItem(ApplicationForFood applicationForFood) {
        this.serviceNumber = applicationForFood.getServiceNumber();
        this.createdDate = applicationForFood.getCreatedDate();
        this.applicationForFoodStatus = applicationForFood.getStatus();
        this.lastUpdate = applicationForFood.getLastUpdate();
        this.contractId = applicationForFood.getClient().getContractId();
        this.fio = applicationForFood.getClient().getPerson().getFullName();
        this.idOfOrg = applicationForFood.getClient().getOrg().getIdOfOrg();
        this.orgName = applicationForFood.getClient().getOrg().getShortNameInfoService();
        this.benefit = applicationForFood.getDtisznCode() == null ? "Иное" : applicationForFood.getDtisznCode().toString();
        this.isInoe = applicationForFood.getDtisznCode() == null;
        this.applicationForFood = applicationForFood;
        isChanged = false;
        this.mobile = applicationForFood.getMobile();
        statuses = new ArrayList<ApplicationForFoodStatus>();
    }

    public String getApplicationForFoodStateString() {
        return applicationForFoodStatus.getApplicationForFoodState().getCode().toString()
                + (applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.DENIED) ? "." + applicationForFoodStatus.getDeclineReason().getCode() : "");
    }

    public String getStatusTitle() {
        String description = applicationForFoodStatus.getApplicationForFoodState().getDescription();
        String declineReasonDescription = applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.DENIED) ? applicationForFoodStatus.getDeclineReason().getDescription() : "";
        return description + ". " + declineReasonDescription;
    }

    public Boolean getIsPaused() {
        if (!isInoe) return false;
        return applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.PAUSED);
    }

    public Boolean getIsResumed() {
        if (!isInoe) return false;
        return applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.RESUME);
    }

    public String getArchieved() {
        return applicationForFood.getArchived() ? "Да" : "Нет";
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ApplicationForFoodStatus getApplicationForFoodStatus() {
        return applicationForFoodStatus;
    }

    public void setApplicationForFoodStatus(ApplicationForFoodStatus applicationForFoodStatus) {
        this.applicationForFoodStatus = applicationForFoodStatus;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public boolean isInoe() {
        return isInoe;
    }

    public void setInoe(boolean inoe) {
        isInoe = inoe;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public List<ApplicationForFoodStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<ApplicationForFoodStatus> statuses) {
        this.statuses = statuses;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}