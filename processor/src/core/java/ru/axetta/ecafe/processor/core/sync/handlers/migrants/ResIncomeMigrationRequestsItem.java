/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 16:05
 */

public class ResIncomeMigrationRequestsItem {
    private Long idOfRequest;
    private Long idOfOrgReg;
    private Long idOfVendorOrgReg;
    private String nameOrgReg;
    private Long idOfMigrClient;
    private String nameOfMigrClient;
    private String groupOfMigrClient;
    private Date visitStartDate;
    private Date visitEndDate;
    private Integer resCode;
    private String errorMessage;

    public ResIncomeMigrationRequestsItem() {
    }

    public ResIncomeMigrationRequestsItem(Migrant migrant) {
        this.idOfRequest = migrant.getCompositeIdOfMigrant().getIdOfRequest();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfRequest", idOfRequest);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrgReg", idOfOrgReg);
        XMLUtils.setAttributeIfNotNull(element, "IdOfVendorOrgReg", idOfVendorOrgReg);
        XMLUtils.setAttributeIfNotNull(element, "NameOrgReg", nameOrgReg);
        XMLUtils.setAttributeIfNotNull(element, "IdOfMigrClient", idOfMigrClient);
        XMLUtils.setAttributeIfNotNull(element, "NameOfMigrClient", nameOfMigrClient);
        XMLUtils.setAttributeIfNotNull(element, "GroupOfMigrClient", groupOfMigrClient);
        XMLUtils.setAttributeIfNotNull(element, "VisitStartDate", visitStartDate != null ? CalendarUtils.dateTimeToString(visitStartDate): null);
        XMLUtils.setAttributeIfNotNull(element, "VisitEndDate", visitEndDate != null ? CalendarUtils.dateTimeToString(visitEndDate) : null);
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

    public Long getIdOfOrgReg() {
        return idOfOrgReg;
    }

    public void setIdOfOrgReg(Long idOfOrgReg) {
        this.idOfOrgReg = idOfOrgReg;
    }

    public Long getIdOfVendorOrgReg() {
        return idOfVendorOrgReg;
    }

    public void setIdOfVendorOrgReg(Long idOfVendorOrgReg) {
        this.idOfVendorOrgReg = idOfVendorOrgReg;
    }

    public String getNameOrgReg() {
        return nameOrgReg;
    }

    public void setNameOrgReg(String nameOrgReg) {
        this.nameOrgReg = nameOrgReg;
    }

    public Long getIdOfMigrClient() {
        return idOfMigrClient;
    }

    public void setIdOfMigrClient(Long idOfMigrClient) {
        this.idOfMigrClient = idOfMigrClient;
    }

    public String getNameOfMigrClient() {
        return nameOfMigrClient;
    }

    public void setNameOfMigrClient(String nameOfMigrClient) {
        this.nameOfMigrClient = nameOfMigrClient;
    }

    public String getGroupOfMigrClient() {
        return groupOfMigrClient;
    }

    public void setGroupOfMigrClient(String groupOfMigrClient) {
        this.groupOfMigrClient = groupOfMigrClient;
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
}
