/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodStatus;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApplicationForFoodReportItem {
    private String serviceNumber;
    protected Date createdDate;
    protected ApplicationForFoodStatus applicationForFoodStatus;
    private Date lastUpdate;
    private Date archiveDate;
    private Long contractId;
    private String fio;
    private String applicantFio;
    private Long idOfOrg;
    private String orgName;
    private String benefit;
    private boolean isInoe;
    private ApplicationForFood applicationForFood;
    private boolean isChanged;
    private List<ApplicationForFoodStatus> statuses;
    private String mobile;
    private Boolean archieved;
    private Date startDate;
    private Date endDate;
    private Boolean expand;

    public ApplicationForFoodReportItem() {

    }

    public ApplicationForFoodReportItem(Session session, ApplicationForFood applicationForFood) {
        this.serviceNumber = applicationForFood.getServiceNumber();
        this.createdDate = applicationForFood.getCreatedDate();
        this.applicationForFoodStatus = applicationForFood.getStatus();
        this.lastUpdate = applicationForFood.getLastUpdate();
        this.archiveDate = applicationForFood.getArchiveDate();
        this.contractId = applicationForFood.getClient().getContractId();
        this.fio = applicationForFood.getClient().getPerson().getFullName();
        this.applicantFio = applicationForFood.getApplicantSurname() + " " + applicationForFood.getApplicantName() + " " + applicationForFood.getApplicantSecondName();
        this.idOfOrg = applicationForFood.getClient().getOrg().getIdOfOrg();
        this.orgName = applicationForFood.getClient().getOrg().getShortNameInfoService();
        this.benefit = applicationForFood.getDtisznCode() == null ? "Иное" : applicationForFood.getDtisznCode().toString();
        this.isInoe = applicationForFood.getDtisznCode() == null;
        this.applicationForFood = applicationForFood;
        isChanged = false;
        this.mobile = applicationForFood.getMobile();
        statuses = new ArrayList<ApplicationForFoodStatus>();
        this.archieved = applicationForFood.getArchived();
        if (applicationForFood.getDiscountDateStart() != null && applicationForFood.getDiscountDateEnd() != null) {
            this.startDate = applicationForFood.getDiscountDateStart();
            this.endDate = applicationForFood.getDiscountDateEnd();
        }
        this.expand = false;
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
        return false;
        //if (!isInoe || archieved) return false;
        //return applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.PAUSED);
    }

    public Boolean getIsResumed() {
        return false;
        //if (!isInoe || archieved) return false;
        //return applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.RESUME);
    }

    public Boolean canBeMovedToArchieve() {
        return isInoe && !archieved;
    }

    public Boolean canChangeDates() {
        return isInoe && !archieved
                && applicationForFoodStatus.getApplicationForFoodState().equals(ApplicationForFoodState.OK)
                && startDate != null && endDate != null;
    }

    public String getArchieved() {
        return this.archieved ? "Да" : "Нет";
    }

    public void setArchieved(Boolean archieved) {
        this.archieved = archieved;
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

    public String getApplicantFio() {
        return applicantFio;
    }

    public void setApplicantFio(String applicantFio) {
        this.applicantFio = applicantFio;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(Date archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Boolean getExpand() {
        return expand;
    }

    public void setExpand(Boolean expand) {
        this.expand = expand;
    }
}
