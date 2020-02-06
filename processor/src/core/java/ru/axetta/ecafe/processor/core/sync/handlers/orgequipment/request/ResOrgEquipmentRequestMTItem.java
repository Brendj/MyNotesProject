/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.persistence.OrgEquipment;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResOrgEquipmentRequestMTItem extends ResOrgEquipmentRequestItem {

    private Integer installStatus;
    private Integer value;
    private String guid;
    private Integer resCode;
    private String errorMessage;
    private String type;

    public ResOrgEquipmentRequestMTItem() {

    }

    public ResOrgEquipmentRequestMTItem(OrgEquipment orgEquipment) {
        this.value = orgEquipment.getModuleType();
        this.installStatus = orgEquipment.getInstallStatus();
        setLastUpdate(orgEquipment.getLastUpdateForModuleType());
    }

    public ResOrgEquipmentRequestMTItem (Integer value, Integer installStatus, Date lastUpdate, String type) {
        super(lastUpdate, type);
        this.installStatus = installStatus;
        this.value = value;
    }

    public ResOrgEquipmentRequestMTItem(OrgEquipment orgEquipment, Integer resCode) {
        this.value = orgEquipment.getModuleType();
        this.installStatus = orgEquipment.getInstallStatus();
        setLastUpdate(orgEquipment.getLastUpdateForModuleType());
        this.resCode = resCode;
    }

    public ResOrgEquipmentRequestMTItem(Integer resCode, String errorMessage) {
        this.guid = guid;
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }


    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("MT");
        if (null != installStatus) {
            XMLUtils.setAttributeIfNotNull(element, "installStatus", installStatus);
        }
        if (null != value) {
            XMLUtils.setAttributeIfNotNull(element, "value", value);
        }
        Date lastUpdate = getLastUpdate();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "lastUpdate", lastUpdate);
        }
        return element;
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

}
