/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private Boolean validDoc;
    private Boolean validGuardianShip;
    private ApplicationForFoodMezhvedState docConfirmed;
    private ApplicationForFoodMezhvedState guardianshipConfirmed;
    private Set<ApplicationForFoodHistory> applicationForFoodHistories;
    private Set<ApplicationForFoodDiscount> dtisznCodes;

    public ApplicationForFood(Client client, ApplicationForFoodStatus status, String mobile, String applicantName,
            String applicantSecondName, String applicantSurname, String serviceNumber, ApplicationForFoodCreatorType creatorType,
            String idOfDocOrder, Date docOrderDate, Long version, Boolean validDoc, Boolean validGuardianship) {
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
        this.validDoc = validDoc;
        this.validGuardianShip = validGuardianship;
        if (validDoc == null || !validDoc) {
            this.docConfirmed = ApplicationForFoodMezhvedState.NO_INFO;
        } else {
            this.docConfirmed = ApplicationForFoodMezhvedState.CONFIRMED;
        }
        if (validGuardianship == null || !validGuardianship) {
            this.guardianshipConfirmed = ApplicationForFoodMezhvedState.NO_INFO;
        } else {
            this.guardianshipConfirmed = ApplicationForFoodMezhvedState.CONFIRMED;
        }
    }

    public ApplicationForFood() {

    }

    public boolean allDiscountsConfirmed() {
        for (ApplicationForFoodDiscount discount : this.getDtisznCodes()) {
            if (!discount.getConfirmed()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInoe() {
        //Если льгота одна и она Иное, то true
        return (dtisznCodes != null && dtisznCodes.size() == 1
                && ((ApplicationForFoodDiscount)dtisznCodes.toArray()[0]).getDtisznCode() == null);
    }

    public Integer getPriorityDtisznCode(List<CategoryDiscountDSZN> categoryDiscountDSZNList) {
        Integer prio = null;
        Integer code = null;
        Date endDate = new Date(0L);
        Date zeroDate = new Date(0L);
        List<ApplicationForFoodDiscount> list = getConfirmedList();
        if (list.size() == 0) list.addAll(dtisznCodes);

        for (ApplicationForFoodDiscount discount : list) {
            CategoryDiscountDSZN categoryDiscountDSZN = getCategoryDiscountDSZNByCode(categoryDiscountDSZNList, discount.getDtisznCode());
            if (discount.getAppointedMSP()) {
                code = categoryDiscountDSZN.getCode();
                break;
            }
            if (prio == null) {
                prio = categoryDiscountDSZN.getPriority();
                code = categoryDiscountDSZN.getCode();
                if (discount.getEndDate() != null) endDate = discount.getEndDate();
            }
            if (discount.getEndDate() != null && discount.getEndDate().after(endDate)) {
                code = categoryDiscountDSZN.getCode();
                endDate = discount.getEndDate();
            } else if (categoryDiscountDSZN.getPriority() != null && categoryDiscountDSZN.getPriority() > prio && endDate.equals(zeroDate)) {
                prio = categoryDiscountDSZN.getPriority();
                code = categoryDiscountDSZN.getCode();
            }
        }
        return code;
    }

    private List<ApplicationForFoodDiscount> getConfirmedList() {
        List<ApplicationForFoodDiscount> list = new ArrayList<>();
        for (ApplicationForFoodDiscount discount : dtisznCodes) {
            if (discount.getConfirmed() != null && discount.getConfirmed()) {
                list.add(discount);
            }
        }
        return list;
    }

    private CategoryDiscountDSZN getCategoryDiscountDSZNByCode(List<CategoryDiscountDSZN> categoryDiscountDSZNList, Integer code) {
        for (CategoryDiscountDSZN categoryDiscountDSZN : categoryDiscountDSZNList) {
            if (code != null && categoryDiscountDSZN.getCode().equals(code)) return categoryDiscountDSZN;
            if (categoryDiscountDSZN.getCode().equals(Integer.parseInt(RuntimeContext.getAppContext().getBean(ETPMVService.class).BENEFIT_INOE)) && code == null) return categoryDiscountDSZN;
        }
        return null;
    }

    public ApplicationForFoodDiscount getAppointedDiscount() {
        for (ApplicationForFoodDiscount discount : dtisznCodes) {
            if (discount.getAppointedMSP()) return discount;
        }
        return null;
    }

    public ApplicationForFoodDiscount getApplicationDiscountOldFormat() {
        return (ApplicationForFoodDiscount)dtisznCodes.toArray()[0];
    }

    public boolean isNewFormat() {
        return serviceNumber.contains(ETPMVService.NEW_ISPP_ID);
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

    public Boolean getValidDoc() {
        return validDoc;
    }

    public void setValidDoc(Boolean validDoc) {
        this.validDoc = validDoc;
    }

    public Boolean getValidGuardianShip() {
        return validGuardianShip;
    }

    public void setValidGuardianShip(Boolean validGuardianShip) {
        this.validGuardianShip = validGuardianShip;
    }

    public ApplicationForFoodMezhvedState getDocConfirmed() {
        return docConfirmed;
    }

    public void setDocConfirmed(ApplicationForFoodMezhvedState docConfirmed) {
        this.docConfirmed = docConfirmed;
    }

    public ApplicationForFoodMezhvedState getGuardianshipConfirmed() {
        return guardianshipConfirmed;
    }

    public void setGuardianshipConfirmed(ApplicationForFoodMezhvedState guardianshipConfirmed) {
        this.guardianshipConfirmed = guardianshipConfirmed;
    }
}
