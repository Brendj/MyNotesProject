/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResRequestFeedingItem {
    private Long applicationForFeedingNumber;
    private String serviceNumber;
    private Long version;
    private Date regDate;
    private Long idOfClient;
    private String applicantPhone;
    private Integer code;
    private String error;

    public ResRequestFeedingItem() {}

    public ResRequestFeedingItem(ApplicationForFood applicationForFood, Integer resultCode) {
        this.applicationForFeedingNumber = applicationForFood.getIdOfApplicationForFood();
        this.version = applicationForFood.getVersion();
        this.regDate = applicationForFood.getCreatedDate();
        this.idOfClient = applicationForFood.getClient().getIdOfClient();
        this.applicantPhone = applicationForFood.getMobile();
        this.serviceNumber = applicationForFood.getServiceNumber();
        this.code = resultCode;
    }

    public static ResRequestFeedingItem error(Integer errorCode, String errorText)
    {
        ResRequestFeedingItem result = new ResRequestFeedingItem();
        result.code = errorCode;
        result.error = errorText;
        return result;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        if (null != applicationForFeedingNumber)
            XMLUtils.setAttributeIfNotNull(element, "Number", applicationForFeedingNumber);
        if (null != version)
            XMLUtils.setAttributeIfNotNull(element, "V", version);
        if (null != regDate) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            XMLUtils.setAttributeIfNotNull(element, "RegDate", simpleDateFormat.format(regDate));
        }
        if (null != idOfClient) {
            XMLUtils.setAttributeIfNotNull(element, "ClientId", idOfClient);
        }
        if (null != applicantPhone) {
            XMLUtils.setAttributeIfNotNull(element, "ApplicantPhone", applicantPhone);
        }
        if (null != serviceNumber) {
            XMLUtils.setAttributeIfNotNull(element, "ServNumber", serviceNumber);
        }
        if (null != code) {
            XMLUtils.setAttributeIfNotNull(element, "Code", code);
        }
        if (code != null && code != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", error);
        }
        return element;
    }

    public Long getApplicationForFeedingNumber() {
        return applicationForFeedingNumber;
    }

    public void setApplicationForFeedingNumber(Long applicationForFeedingNumber) {
        this.applicationForFeedingNumber = applicationForFeedingNumber;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }
}
