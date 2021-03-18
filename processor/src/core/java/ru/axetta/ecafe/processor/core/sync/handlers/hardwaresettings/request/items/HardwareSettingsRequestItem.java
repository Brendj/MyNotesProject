/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class HardwareSettingsRequestItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Date lastUpdate;
    private HardwareSettingsRequest.ModuleType type;
    private String errorMessage;
    private Integer resCode;

    public HardwareSettingsRequestItem (Date lastUpdate, HardwareSettingsRequest.ModuleType type, String errorMessage) {
        this.lastUpdate = lastUpdate;
        this.type = type;
        this.errorMessage = errorMessage;
        if(errorMessage.isEmpty()) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public HardwareSettingsRequestItem(HardwareSettingsRequest.ModuleType type, String errorMessage) {
        this.type = type;
        this.errorMessage = errorMessage;
        if(errorMessage.isEmpty()) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public static Date getLastUpdate(Node itemNode, StringBuilder errorMessage) {
        Date lastUpdate = null;
        String requestDateString = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                lastUpdate = simpleDateFormat.parse(requestDateString);
            } catch (Exception e) {
                errorMessage.append("Attribute LastUpdate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute LastUpdate not found");
        }
        return lastUpdate;
    }

    public static String getValue(Node itemNode, StringBuilder errorMessage) {

        String value = XMLUtils.getAttributeValue(itemNode, "Value");
        if (StringUtils.isEmpty(value)) {
            errorMessage.append("Attribute Value not found");
        }
        return value;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public HardwareSettingsRequest.ModuleType getType() {
        return type;
    }

    public void setType(HardwareSettingsRequest.ModuleType type) {
        this.type = type;
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
}
