/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestFeedingItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long applicationForFeedingNumber;
    private Integer status;
    private Integer declineReason;
    private Date applicationCreatedDate;
    private Long idOfClient;
    private String applicantSurname;
    private String applicantName;
    private String applicantSecondName;
    private String applicantPhone;
    private Long idOfOrgOnCreate;
    private Long dtisznCode;
    private Boolean isArchive;
    private String errorMessage;
    private Integer resCode;
    private Long version;

    public RequestFeedingItem(Long applicationForFeedingNumber, Integer status, Integer declineReason,
            Date applicationCreatedDate, Long idOfClient, String applicantSurname, String applicantName,
            String applicantSecondName, String applicantPhone, Long idOfOrgOnCreate, Long dtisznCode,
            Boolean isArchive, String errorMessage) {
        this.applicationForFeedingNumber = applicationForFeedingNumber;
        this.status = status;
        this.declineReason = declineReason;
        this.applicationCreatedDate = applicationCreatedDate;
        this.idOfClient = idOfClient;
        this.applicantSurname = applicantSurname;
        this.applicantName = applicantName;
        this.applicantSecondName = applicantSecondName;
        this.applicantPhone = applicantPhone;
        this.idOfOrgOnCreate = idOfOrgOnCreate;
        this.dtisznCode = dtisznCode;
        this.isArchive = isArchive;
        if (errorMessage.isEmpty()) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public RequestFeedingItem(ApplicationForFood applicationForFood) {
        this.applicationForFeedingNumber = applicationForFood.getIdOfApplicationForFood();
        ApplicationForFoodStatus status = applicationForFood.getStatus();
        this.status = status.getApplicationForFoodState().getCode();
        if (null != status.getDeclineReason())
            this.declineReason = status.getDeclineReason().getCode();
        this.applicationCreatedDate = applicationForFood.getCreatedDate();
        this.idOfClient = applicationForFood.getClient().getIdOfClient();
        this.applicantSurname = applicationForFood.getApplicantSurname();
        this.applicantName = applicationForFood.getApplicantName();
        this.applicantSecondName = applicationForFood.getApplicantSecondName();
        this.applicantPhone = applicationForFood.getMobile();
        this.idOfOrgOnCreate = applicationForFood.getIdOfOrgOnCreate();
        this.dtisznCode = applicationForFood.getDtisznCode();
        this.isArchive = applicationForFood.getArchived();
        this.version = applicationForFood.getVersion();
    }

    public static RequestFeedingItem build(Node itemNode, Long idOfOrg) throws Exception {
        Long applicationForFeedingNumber;
        Integer state;
        Integer declineReason;
        Date regDate = null;
        Long idOfClient;
        String applicantSurname;
        String applicantName;
        String applicantSecondName;
        String applicantPhone;
        Long idOfOrgOnCreate;
        Long dtisznDiscount;
        Boolean archived;

        StringBuilder errorMessage = new StringBuilder();

        applicationForFeedingNumber = XMLUtils.getLongAttributeValue(itemNode, "Number");

        state = XMLUtils.getIntegerAttributeValue(itemNode, "State");
        if (!ApplicationForFoodState.TRY_TO_REGISTER.getCode().equals(state) ||
                !ApplicationForFoodState.REGISTERED.getCode().equals(state) ||
                !ApplicationForFoodState.PAUSED.getCode().equals(state) ||
                !ApplicationForFoodState.RESUME.getCode().equals(state) ||
                !ApplicationForFoodState.OK.getCode().equals(state) ||
                !ApplicationForFoodState.DENIED.getCode().equals(state) ||
                !ApplicationForFoodState.INFORMATION_REQUEST_SENDED.getCode().equals(state) |
                !ApplicationForFoodState.INFORMATION_REQUEST_RECEIVED.getCode().equals(state)) {
            errorMessage.append("Attribute State is incorrect ");
        }

        declineReason = XMLUtils.getIntegerAttributeValue(itemNode, "DeclineReason");
        if (null == declineReason && ApplicationForFoodState.DENIED.getCode().equals(state) ||
                ApplicationForFoodState.DENIED.getCode().equals(state) &&
                        ApplicationForFoodDeclineReason.NO_DOCS.getCode().equals(declineReason) ||
                ApplicationForFoodState.DENIED.getCode().equals(state) &&
                        ApplicationForFoodDeclineReason.NO_APPROVAL.getCode().equals(declineReason) ||
                ApplicationForFoodState.DENIED.getCode().equals(state) &&
                        ApplicationForFoodDeclineReason.INFORMATION_CONFLICT.getCode().equals(declineReason)) {
            errorMessage.append("Attribute DeclineReason is incorrect ");
        }

        try {
            regDate = XMLUtils.getDateTimeAttributeValue(itemNode, "RegDate");
        } catch (Exception e) {
            errorMessage.append("Attribute RegDate is incorrect ");
        }
        if (null == regDate) {
            errorMessage.append("Attribute RegDate not found ");
        }

        idOfClient = XMLUtils.getLongAttributeValue(itemNode, "ClientId");
        if (null == idOfClient) {
            errorMessage.append("Attribute ClientId is incorrect ");
        }

        applicantSurname = XMLUtils.getAttributeValue(itemNode, "ApplicantSurname");
        if (null == applicantSurname) {
            errorMessage.append("Attribute ApplicantSurname not found ");
        }

        applicantName = XMLUtils.getAttributeValue(itemNode, "ApplicantName");
        if (null == applicantName) {
            errorMessage.append("Attribute ApplicantName not found ");
        }

        applicantSecondName = XMLUtils.getAttributeValue(itemNode, "ApplicantSecName");

        applicantPhone = XMLUtils.getAttributeValue(itemNode, "ApplicantPhone");
        if (null == applicantPhone) {
            errorMessage.append("Attribute ApplicantPhone not found ");
        }
        applicantPhone = Client.checkAndConvertMobile(applicantPhone);
        if (null == applicantPhone) {
            errorMessage.append("Attribute ApplicantPhone is incorrect ");
        }

        idOfOrgOnCreate = XMLUtils.getLongAttributeValue(itemNode, "OrgIdCreator");

        dtisznDiscount = XMLUtils.getLongAttributeValue(itemNode, "DiscountDtszn");
        if (null == dtisznDiscount) {
            errorMessage.append("Attribute DiscountDtszn not found ");
        }

        archived = XMLUtils.getBooleanAttributeValue(itemNode, "D");

        return new RequestFeedingItem(applicationForFeedingNumber, state, declineReason, regDate, idOfClient,
                applicantSurname, applicantName, applicantSecondName, applicantPhone, idOfOrgOnCreate, dtisznDiscount,
                archived, errorMessage.toString());
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        if (null != applicationForFeedingNumber)
            XMLUtils.setAttributeIfNotNull(element, "Number", applicationForFeedingNumber);
        if (null != version)
            XMLUtils.setAttributeIfNotNull(element, "V", version);
        if (null != status)
            XMLUtils.setAttributeIfNotNull(element, "State", status);
        if (null != declineReason)
            XMLUtils.setAttributeIfNotNull(element, "DeclineReason", declineReason);
        if (null != applicationCreatedDate) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            XMLUtils.setAttributeIfNotNull(element, "RegDate", simpleDateFormat.format(applicationCreatedDate));
        }
        if (null != idOfClient)
            XMLUtils.setAttributeIfNotNull(element, "ClientId", idOfClient);
        if (null != applicantSurname)
            XMLUtils.setAttributeIfNotNull(element, "ApplicantSurname", applicantSurname);
        if (null != applicantName)
            XMLUtils.setAttributeIfNotNull(element, "ApplicantName", applicantName);
        if (null != applicantSecondName)
            XMLUtils.setAttributeIfNotNull(element, "ApplicantSecName", applicantSecondName);
        if (null != applicantPhone)
            XMLUtils.setAttributeIfNotNull(element, "ApplicantPhone", applicantPhone);
        if (null != idOfOrgOnCreate)
            XMLUtils.setAttributeIfNotNull(element, "OrgIdCreator", idOfOrgOnCreate);
        if (null != dtisznCode) {
            XMLUtils.setAttributeIfNotNull(element, "DiscountDtszn", dtisznCode);
        }
        if (null != isArchive) {
            XMLUtils.setAttributeIfNotNull(element, "D", isArchive.toString());
        }
        return element;
    }

    public Long getApplicationForFeedingNumber() {
        return applicationForFeedingNumber;
    }

    public void setApplicationForFeedingNumber(Long applicationForFeedingNumber) {
        this.applicationForFeedingNumber = applicationForFeedingNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(Integer declineReason) {
        this.declineReason = declineReason;
    }

    public Date getApplicationCreatedDate() {
        return applicationCreatedDate;
    }

    public void setApplicationCreatedDate(Date applicationCreatedDate) {
        this.applicationCreatedDate = applicationCreatedDate;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getApplicantSurname() {
        return applicantSurname;
    }

    public void setApplicantSurname(String applicantSurname) {
        this.applicantSurname = applicantSurname;
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

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public Long getIdOfOrgOnCreate() {
        return idOfOrgOnCreate;
    }

    public void setIdOfOrgOnCreate(Long idOfOrgOnCreate) {
        this.idOfOrgOnCreate = idOfOrgOnCreate;
    }

    public Long getDtisznCode() {
        return dtisznCode;
    }

    public void setDtisznCode(Long dtisznCode) {
        this.dtisznCode = dtisznCode;
    }

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
