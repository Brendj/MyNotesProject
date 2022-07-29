/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RequestFeedingItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;
    public static final Integer ERROR_CODE_INTERNAL_ERROR = 110;

    private Long applicationForFeedingNumber;
    private String servNumber;
    private Integer status;
    private Integer declineReason;
    private Date applicationCreatedDate;
    private Long idOfClient;
    private String applicantSurname;
    private String applicantName;
    private String applicantSecondName;
    private String applicantPhone;
    private ApplicationForFoodCreatorType creatorType;
    private List<Integer> dtisznCodes;
    private String idOfDocOrder;
    private Date docOrderDate;
    private Boolean isArchive;
    private Boolean hasSocialDiscount;
    private String errorMessage;
    private Integer resCode;
    private Long version;
    private Date statusCreatedDate;
    private Date otherDiscountStartDate;
    private Date otherDiscountEndDate;

    public RequestFeedingItem(Long applicationForFeedingNumber, String servNumber, Integer status,
            Integer declineReason, Date applicationCreatedDate, Long idOfClient, String applicantSurname,
            String applicantName, String applicantSecondName, String applicantPhone,
            ApplicationForFoodCreatorType creatorType, List<Integer> dtisznCodes, String idOfDocOrder, Date docOrderDate,
            Boolean isArchive, Date otherDiscountStartDate, Date otherDiscountEndDate, String errorMessage) {
        this.applicationForFeedingNumber = applicationForFeedingNumber;
        this.servNumber = servNumber;
        this.status = status;
        this.declineReason = declineReason;
        this.applicationCreatedDate = applicationCreatedDate;
        this.idOfClient = idOfClient;
        this.applicantSurname = applicantSurname;
        this.applicantName = applicantName;
        this.applicantSecondName = applicantSecondName;
        this.applicantPhone = applicantPhone;
        this.creatorType = creatorType;
        this.dtisznCodes = dtisznCodes;
        this.idOfDocOrder = idOfDocOrder;
        this.docOrderDate = docOrderDate;
        this.isArchive = isArchive;
        this.otherDiscountStartDate = otherDiscountStartDate;
        this.otherDiscountEndDate = otherDiscountEndDate;
        if (errorMessage.isEmpty()) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
            this.errorMessage = errorMessage;
        }
    }

    public boolean isInoe() {
        //Если льгота одна и она Иное, то true
        return (dtisznCodes != null && dtisznCodes.size() == 1 && dtisznCodes.get(0) == null);
    }

    public RequestFeedingItem(ApplicationForFood applicationForFood, Date statusCreatedDate) {
        this.applicationForFeedingNumber = applicationForFood.getIdOfApplicationForFood();
        ApplicationForFoodStatus status = applicationForFood.getStatus();
        this.status = status.getApplicationForFoodState().getCode();
        if (null != status.getDeclineReason()) {
            this.declineReason = status.getDeclineReason().getCode();
        }
        this.applicationCreatedDate = applicationForFood.getCreatedDate();
        this.idOfClient = applicationForFood.getClient().getIdOfClient();
        this.applicantSurname = applicationForFood.getApplicantSurname();
        this.applicantName = applicationForFood.getApplicantName();
        this.applicantSecondName = applicationForFood.getApplicantSecondName();
        this.applicantPhone = applicationForFood.getMobile();
        this.dtisznCodes = applicationForFood.getDtisznCodes().stream().map(d -> d.getDtisznCode()).collect(Collectors.toList());
        this.isArchive = applicationForFood.getArchived();
        this.version = applicationForFood.getVersion();
        this.servNumber = applicationForFood.getServiceNumber();
        this.creatorType = applicationForFood.getCreatorType();
        this.idOfDocOrder = applicationForFood.getIdOfDocOrder();
        this.docOrderDate = applicationForFood.getDocOrderDate();
        this.hasSocialDiscount = (applicationForFood.getDtisznCodes().stream().map(d -> d.getDtisznCode()).collect(Collectors.toList())).contains(null);
        this.statusCreatedDate = statusCreatedDate;
    }

    public RequestFeedingItem(ApplicationForFood applicationForFood, Date statusCreatedDate, ClientDtisznDiscountInfo discountInfo) {
        this(applicationForFood, statusCreatedDate);
        // Только иное
        if (!DTSZNDiscountsReviseService.OTHER_DISCOUNT_CODE.equals(discountInfo.getDtisznCode())) {
            return;
        }
        this.otherDiscountStartDate = applicationForFood.getDiscountDateStart();
        this.otherDiscountEndDate = applicationForFood.getDiscountDateEnd();
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
        Integer dtisznDiscount;
        List<Integer> newDiscounts = new ArrayList<>();
        Boolean archived;
        String serviceNumber;
        ApplicationForFoodCreatorType creatorType;
        String idOfDocOrder;
        Date docOrderDate;
        Date otherDiscountStartDate;
        Date otherDiscountEndDate;

        StringBuilder errorMessage = new StringBuilder();

        applicationForFeedingNumber = XMLUtils.getLongAttributeValue(itemNode, "Number");

        state = XMLUtils.getIntegerAttributeValue(itemNode, "State");
        if (!ApplicationForFoodState.TRY_TO_REGISTER.getCode().equals(state) && !ApplicationForFoodState.REGISTERED
                .getCode().equals(state) && !ApplicationForFoodState.PAUSED.getCode().equals(state)
                && !ApplicationForFoodState.RESUME.getCode().equals(state) && !ApplicationForFoodState.OK.getCode()
                .equals(state) && !ApplicationForFoodState.DENIED.getCode().equals(state)
                && !ApplicationForFoodState.INFORMATION_REQUEST_SENDED.getCode().equals(state)
                && !ApplicationForFoodState.INFORMATION_REQUEST_RECEIVED.getCode().equals(state)) {
            errorMessage.append("Attribute State is incorrect ");
        }

        declineReason = XMLUtils.getIntegerAttributeValue(itemNode, "DeclineReason");
        if (null == declineReason && ApplicationForFoodState.DENIED.getCode().equals(state)
                || ApplicationForFoodState.DENIED.getCode().equals(state) && !ApplicationForFoodDeclineReason.NO_DOCS
                .getCode().equals(declineReason) && !ApplicationForFoodDeclineReason.NO_APPROVAL.getCode()
                .equals(declineReason) && !ApplicationForFoodDeclineReason.INFORMATION_CONFLICT.getCode()
                .equals(declineReason)) {
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

        dtisznDiscount = XMLUtils.getIntegerAttributeValue(itemNode, "DiscountDtszn");

        archived = XMLUtils.getBooleanAttributeValue(itemNode, "D");

        serviceNumber = XMLUtils.getStringAttributeValue(itemNode, "ServNumber", 128);

        creatorType = ApplicationForFoodCreatorType
                .fromCode(XMLUtils.getIntegerAttributeValue(itemNode, "CreatorType"));
        if (null == creatorType) {
            errorMessage.append("Attribute CreatorType not found ");
        }

        idOfDocOrder = XMLUtils.getStringAttributeValue(itemNode, "DocOrderId", 128);

        docOrderDate = XMLUtils.getDateTimeAttributeValue(itemNode, "DocOrderDate");

        otherDiscountStartDate = XMLUtils.getDateAttributeValue(itemNode, "FDate");
        otherDiscountEndDate = XMLUtils.getDateAttributeValue(itemNode, "LDate");

        Node discountNode = itemNode.getFirstChild();
        while (null != discountNode) {
            if (Node.ELEMENT_NODE == discountNode.getNodeType() && discountNode.getNodeName().equals("RFD")) {
                newDiscounts.add(XMLUtils.getIntegerAttributeValue(discountNode, "DiscountDtszn"));
            }
            discountNode = discountNode.getNextSibling();
        }
        if (newDiscounts.size() == 0) newDiscounts.add(dtisznDiscount);

        return new RequestFeedingItem(applicationForFeedingNumber, serviceNumber, state, declineReason, regDate,
                idOfClient, applicantSurname, applicantName, applicantSecondName, applicantPhone, creatorType,
                newDiscounts, idOfDocOrder, docOrderDate, archived, otherDiscountStartDate, otherDiscountEndDate,
                errorMessage.toString());
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat simpleDateWithoutTimestampFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (null != applicationForFeedingNumber) {
            XMLUtils.setAttributeIfNotNull(element, "Number", applicationForFeedingNumber);
        }
        if (null != version) {
            XMLUtils.setAttributeIfNotNull(element, "V", version);
        }
        if (null != status) {
            XMLUtils.setAttributeIfNotNull(element, "State", status);
        }
        if (null != declineReason) {
            XMLUtils.setAttributeIfNotNull(element, "DeclineReason", declineReason);
        }
        if (null != applicationCreatedDate) {
            XMLUtils.setAttributeIfNotNull(element, "RegDate", simpleDateFormat.format(applicationCreatedDate));
        }
        if (null != idOfClient) {
            XMLUtils.setAttributeIfNotNull(element, "ClientId", idOfClient);
        }
        if (null != applicantSurname) {
            XMLUtils.setAttributeIfNotNull(element, "ApplicantSurname", applicantSurname);
        }
        if (null != applicantName) {
            XMLUtils.setAttributeIfNotNull(element, "ApplicantName", applicantName);
        }
        if (null != applicantSecondName) {
            XMLUtils.setAttributeIfNotNull(element, "ApplicantSecName", applicantSecondName);
        }
        if (null != applicantPhone) {
            XMLUtils.setAttributeIfNotNull(element, "ApplicantPhone", applicantPhone);
        }
        for (Integer code : dtisznCodes) {
            Element discountElement = document.createElement("RFD");
            XMLUtils.setAttributeIfNotNull(discountElement, "DiscountDtszn", code);
            element.appendChild(discountElement);
            if (null != code) {
                XMLUtils.setAttributeIfNotNull(element, "DiscountDtszn", code);
            }
        }
        if (null != isArchive) {
            XMLUtils.setAttributeIfNotNull(element, "D", isArchive.toString());
        }
        if (null != servNumber) {
            XMLUtils.setAttributeIfNotNull(element, "ServNumber", servNumber);
        }
        if (null != creatorType) {
            XMLUtils.setAttributeIfNotNull(element, "CreatorType", creatorType.getCode());
        }
        if (null != idOfDocOrder) {
            XMLUtils.setAttributeIfNotNull(element, "DocOrderId", idOfDocOrder);
        }
        if (null != docOrderDate) {
            XMLUtils.setAttributeIfNotNull(element, "DocOrderDate", simpleDateFormat.format(docOrderDate));
        }
        if (null != hasSocialDiscount) {
            XMLUtils.setAttributeIfNotNull(element, "InSocOrgans", hasSocialDiscount);
        }
        if (null != statusCreatedDate) {
            XMLUtils.setAttributeIfNotNull(element, "StatusDate", simpleDateFormat.format(statusCreatedDate));
        }
        if (null != otherDiscountStartDate) {
            XMLUtils.setAttributeIfNotNull(element, "FDate", simpleDateWithoutTimestampFormat.format(otherDiscountStartDate));
        }
        if (null != otherDiscountEndDate) {
            XMLUtils.setAttributeIfNotNull(element, "LDate", simpleDateWithoutTimestampFormat.format(otherDiscountEndDate));
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

    public List<Integer> getDtisznCodes() {
        return dtisznCodes;
    }

    public void setDtisznCodes(List<Integer> dtisznCodes) {
        this.dtisznCodes = dtisznCodes;
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

    public String getServNumber() {
        return servNumber;
    }

    public void setServNumber(String servNumber) {
        this.servNumber = servNumber;
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

    public Boolean getHasSocialDiscount() {
        return hasSocialDiscount;
    }

    public void setHasSocialDiscount(Boolean hasSocialDiscount) {
        this.hasSocialDiscount = hasSocialDiscount;
    }

    public Date getStatusCreatedDate() {
        return statusCreatedDate;
    }

    public void setStatusCreatedDate(Date statusCreatedDate) {
        this.statusCreatedDate = statusCreatedDate;
    }

    public Date getOtherDiscountStartDate() {
        return otherDiscountStartDate;
    }

    public void setOtherDiscountStartDate(Date otherDiscountStartDate) {
        this.otherDiscountStartDate = otherDiscountStartDate;
    }

    public Date getOtherDiscountEndDate() {
        return otherDiscountEndDate;
    }

    public void setOtherDiscountEndDate(Date otherDiscountEndDate) {
        this.otherDiscountEndDate = otherDiscountEndDate;
    }
}
