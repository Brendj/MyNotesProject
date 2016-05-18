/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.VisitReqResolutionHist;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 14:35
 */

public class ResIncomeMigrationRequestsHistoryItem {
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

    public ResIncomeMigrationRequestsHistoryItem() {
    }

    public ResIncomeMigrationRequestsHistoryItem(VisitReqResolutionHist visitReqResolutionHist) {
        this.idOfRecord = visitReqResolutionHist.getCompositeIdOfVisitReqResolutionHist().getIdOfRecord();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRecord", idOfRecord);
        XMLUtils.setAttributeIfNotNull(element, "idOfOrgIssuer", idOfOrgIssuer);
        XMLUtils.setAttributeIfNotNull(element, "idOfRequest", idOfRequest);
        XMLUtils.setAttributeIfNotNull(element, "resolutionDateTime", resolutionDateTime != null ? CalendarUtils.dateTimeToString(resolutionDateTime) : null);
        XMLUtils.setAttributeIfNotNull(element, "resolutionCause", resolutionCause);
        XMLUtils.setAttributeIfNotNull(element, "idOfClientResol", idOfClientResol);
        XMLUtils.setAttributeIfNotNull(element, "contactInfo", contactInfo);
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

}
