/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.help.request;

import ru.axetta.ecafe.processor.core.persistence.HelpRequest;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestStatusEnumType;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestThemeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResHelpRequestItem {

    private String guid;
    private Org org;
    private Date requestDate;
    private String number;
    private HelpRequestThemeEnumType theme;
    private String message;
    private String declarer;
    private String phone;
    private HelpRequestStatusEnumType requestState;
    private Integer resultCode;
    private Long version;
    private String errorMessage;

    public ResHelpRequestItem() {

    }

    public ResHelpRequestItem(HelpRequest helpRequest) {
        this.guid = helpRequest.getGuid();
        this.org = helpRequest.getOrg();
        this.requestDate = helpRequest.getRequestDate();
        this.number = helpRequest.getRequestNumber();
        this.theme = helpRequest.getTheme();
        this.message = helpRequest.getMessage();
        this.declarer = helpRequest.getDeclarer();
        this.phone = helpRequest.getPhone();
        this.requestState = helpRequest.getStatus();
    }

    public ResHelpRequestItem(HelpRequest helpRequest, Integer resultCode) {
        this.guid = helpRequest.getGuid();
        this.org = helpRequest.getOrg();
        this.requestDate = helpRequest.getRequestDate();
        this.number = helpRequest.getRequestNumber();
        this.theme = helpRequest.getTheme();
        this.message = helpRequest.getMessage();
        this.declarer = helpRequest.getDeclarer();
        this.phone = helpRequest.getPhone();
        this.requestState = helpRequest.getStatus();
        this.version = helpRequest.getVersion();
        this.resultCode = resultCode;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        if (null != org)
            XMLUtils.setAttributeIfNotNull(element, "OrgId", org.getIdOfOrg());
        if (null != requestDate)
            XMLUtils.setAttributeIfNotNull(element, "RequestDate", CalendarUtils.dateTimeToString(requestDate));
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        if (null != theme)
            XMLUtils.setAttributeIfNotNull(element, "Theme", theme.toString());
        XMLUtils.setAttributeIfNotNull(element, "Message", message);
        XMLUtils.setAttributeIfNotNull(element, "ContactFIO", declarer);
        XMLUtils.setAttributeIfNotNull(element, "ContactPhone", phone);
        if (null != requestState)
            XMLUtils.setAttributeIfNotNull(element, "RequestState", String.valueOf(requestState.ordinal()));
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public HelpRequestThemeEnumType getTheme() {
        return theme;
    }

    public void setTheme(HelpRequestThemeEnumType theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeclarer() {
        return declarer;
    }

    public void setDeclarer(String declarer) {
        this.declarer = declarer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HelpRequestStatusEnumType getRequestState() {
        return requestState;
    }

    public void setRequestState(HelpRequestStatusEnumType requestState) {
        this.requestState = requestState;
    }
}
