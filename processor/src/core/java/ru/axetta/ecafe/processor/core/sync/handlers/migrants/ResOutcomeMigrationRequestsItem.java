/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.MigrantInitiatorEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 14:01
 */

public class ResOutcomeMigrationRequestsItem {
    private Long idOfRequest;
    private String requestNumber;
    private Long idOfClient;
    private Long idOfOrgVisit;
    private Date visitStartDate;
    private Date visitEndDate;
    private Integer resCode;
    private String errorMessage;
    private MigrantInitiatorEnum inititator;
    private String section;
    private Long resolutionCodeGroup;

    public ResOutcomeMigrationRequestsItem() {
    }

    public ResOutcomeMigrationRequestsItem(Migrant migrant) {
        this.idOfRequest = migrant.getCompositeIdOfMigrant().getIdOfRequest();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRequest", idOfRequest);
        XMLUtils.setAttributeIfNotNull(element, "RequestNumber", requestNumber);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrgVisit", idOfOrgVisit);
        XMLUtils.setAttributeIfNotNull(element, "VisitStartDate", visitStartDate != null ? CalendarUtils.dateTimeToString(visitStartDate): null);
        XMLUtils.setAttributeIfNotNull(element, "VisitEndDate", visitEndDate != null ? CalendarUtils.dateTimeToString(visitEndDate) : null);
        XMLUtils.setAttributeIfNotNull(element, "Initiator", inititator != null ? inititator.ordinal() : null);
        XMLUtils.setAttributeIfNotNull(element, "Section", section);
        XMLUtils.setAttributeIfNotNull(element, "ResolutionCodeGroup", resolutionCodeGroup);
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrgVisit() {
        return idOfOrgVisit;
    }

    public void setIdOfOrgVisit(Long idOfOrgVisit) {
        this.idOfOrgVisit = idOfOrgVisit;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Date getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(Date visitEndDate) {
        this.visitEndDate = visitEndDate;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MigrantInitiatorEnum getInititator() {
        return inititator;
    }

    public void setInititator(MigrantInitiatorEnum inititator) {
        this.inititator = inititator;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Long getResolutionCodeGroup() {
        return resolutionCodeGroup;
    }

    public void setResolutionCodeGroup(Long resolutionCodeGroup) {
        this.resolutionCodeGroup = resolutionCodeGroup;
    }
}
