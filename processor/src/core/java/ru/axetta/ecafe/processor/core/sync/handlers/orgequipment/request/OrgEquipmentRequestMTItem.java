/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrgEquipmentRequestMTItem extends OrgEquipmentRequestItem {

    private Integer installStatus;
    private Integer value;

    public OrgEquipmentRequestMTItem(Integer value, Integer installStatus, Date lastUpdate, String type,
            String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.installStatus = installStatus;
        this.value = value;
    }

    public static OrgEquipmentRequestMTItem build(Node itemNode) {
        Integer value = null;
        Integer installStatus = null;
        Date lastUpdate = null;
        String type = "MT";

        StringBuilder errorMessage = new StringBuilder();

        value = XMLUtils.getIntegerAttributeValue(itemNode, "Value");
        if (null == value) {
            errorMessage.append("Attribute Value not found");
        }

        installStatus = XMLUtils.getIntegerAttributeValue(itemNode, "InstallStatus");
        if (null == installStatus) {
            errorMessage.append("Attribute InstallStatus not found");
        }

        String requestDateString = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                lastUpdate = simpleDateFormat.parse(requestDateString);
            } catch (Exception e) {
                errorMessage.append("Attribute LastUpdate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute RequestDate not found");
        }
        return new OrgEquipmentRequestMTItem(value, installStatus, lastUpdate, type, errorMessage.toString());
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
}

