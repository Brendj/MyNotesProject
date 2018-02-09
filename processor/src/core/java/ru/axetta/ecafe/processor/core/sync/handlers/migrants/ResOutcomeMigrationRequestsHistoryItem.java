/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHistInitiatorEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 14:28
 */

public class ResOutcomeMigrationRequestsHistoryItem {
    private Long idOfRecord;
    private Long idOfOrgIssuer;
    private Long idOfRequest;
    private Integer resolution;
    private Date resolutionDateTime;
    private String resolutionCause;
    private Long idOfClientResol;
    private String contactInfo;
    private Integer resCode;
    private String errorMessage;
    private VisitReqResolutionHistInitiatorEnum initiator;

    public ResOutcomeMigrationRequestsHistoryItem() {
    }

    public ResOutcomeMigrationRequestsHistoryItem(VisitReqResolutionHist visitReqResolutionHist) {
        this.idOfRecord = visitReqResolutionHist.getCompositeIdOfVisitReqResolutionHist().getIdOfRecord();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRecord", idOfRecord);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrgIssuer", idOfOrgIssuer);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRequest", idOfRequest);
        XMLUtils.setAttributeIfNotNull(element, "Resolution", resolution);
        XMLUtils.setAttributeIfNotNull(element, "ResolutionDateTime", resolutionDateTime != null ? CalendarUtils.dateTimeToString(resolutionDateTime) : null);
        XMLUtils.setAttributeIfNotNull(element, "ResolutionCause", resolutionCause);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClientResol", idOfClientResol);
        XMLUtils.setAttributeIfNotNull(element, "ContactInfo", contactInfo);
        XMLUtils.setAttributeIfNotNull(element, "Initiator", initiator != null ? initiator.ordinal() : null);
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public Long getIdOfOrgIssuer() {
        return idOfOrgIssuer;
    }

    public void setIdOfOrgIssuer(Long idOfOrgIssuer) {
        this.idOfOrgIssuer = idOfOrgIssuer;
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Date getResolutionDateTime() {
        return resolutionDateTime;
    }

    public void setResolutionDateTime(Date resolutionDateTime) {
        this.resolutionDateTime = resolutionDateTime;
    }

    public String getResolutionCause() {
        return resolutionCause;
    }

    public void setResolutionCause(String resolutionCause) {
        this.resolutionCause = resolutionCause;
    }

    public Long getIdOfClientResol() {
        return idOfClientResol;
    }

    public void setIdOfClientResol(Long idOfClientResol) {
        this.idOfClientResol = idOfClientResol;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    public VisitReqResolutionHistInitiatorEnum getInitiator() {
        return initiator;
    }

    public void setInitiator(VisitReqResolutionHistInitiatorEnum initiator) {
        this.initiator = initiator;
    }
}
