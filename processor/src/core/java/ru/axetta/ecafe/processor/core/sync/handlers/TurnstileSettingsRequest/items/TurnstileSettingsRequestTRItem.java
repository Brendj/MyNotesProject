/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TurnstileSettingsRequestTRItem extends TurnstileSettingsRequestItem {

    private String turnstileId;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Integer isWorkWithLongIds;
    private Date lastUpdateForTurnstileSetting;

    public TurnstileSettingsRequestTRItem(String type, String errorMessage, String turnstileId, String controllerModel,
            String controllerFirmwareVersion, Integer isWorkWithLongIds, Date lastUpdateForTurnstile) {
        super(type, errorMessage);
        this.turnstileId = turnstileId;
        this.controllerModel = controllerModel;
        this.controllerFirmwareVersion = controllerFirmwareVersion;
        this.isWorkWithLongIds = isWorkWithLongIds;
        this.lastUpdateForTurnstileSetting = lastUpdateForTurnstile;
    }

    public static TurnstileSettingsRequestTRItem build(Node itemNode) {
        String turnstileId = null;
        String controllerModel = null;
        String controllerFirmwareVersion = null;
        Integer isWorkWithLongIds = null;
        Date lastUpdateForTurnstileSetting = null;
        String type = "TR";

        StringBuilder errorMessage = new StringBuilder();

        turnstileId = XMLUtils.getAttributeValue(itemNode, "TurnstileId");
        if (null == turnstileId || StringUtils.isEmpty(turnstileId)) {
            errorMessage.append("Attribute TurnstileId not found");
        }
        controllerModel = XMLUtils.getAttributeValue(itemNode, "ControllerModel");
        if (null == controllerModel || StringUtils.isEmpty(controllerModel)) {
            errorMessage.append("Attribute ControllerModel not found");
        }
        controllerFirmwareVersion = XMLUtils.getAttributeValue(itemNode, "ControllerFirmwareVersion");
        if (null == controllerFirmwareVersion || StringUtils.isEmpty(controllerFirmwareVersion)) {
            errorMessage.append("Attribute controllerFirmwareVersion not found");
        }

        isWorkWithLongIds = XMLUtils.getIntegerAttributeValue(itemNode, "IsWorkWithLongIds");
        if (null == isWorkWithLongIds) {
            errorMessage.append("Attribute IsWorkWithLongIds not found");
        }

        String requestDateString = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                lastUpdateForTurnstileSetting = simpleDateFormat.parse(requestDateString);
            } catch (Exception e) {
                errorMessage.append("Attribute LastUpdate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute LastUpdate not found");
        }
        return new TurnstileSettingsRequestTRItem(type, errorMessage.toString(), turnstileId, controllerModel,
                controllerFirmwareVersion, isWorkWithLongIds, lastUpdateForTurnstileSetting);
    }

    public String getTurnstileId() {
        return turnstileId;
    }

    public void setTurnstileId(String turnstileId) {
        this.turnstileId = turnstileId;
    }

    public String getControllerModel() {
        return controllerModel;
    }

    public void setControllerModel(String controllerModel) {
        this.controllerModel = controllerModel;
    }

    public String getControllerFirmwareVersion() {
        return controllerFirmwareVersion;
    }

    public void setControllerFirmwareVersion(String controllerFirmwareVersion) {
        this.controllerFirmwareVersion = controllerFirmwareVersion;
    }

    public Integer getIsWorkWithLongIds() {
        return isWorkWithLongIds;
    }

    public void setIsWorkWithLongIds(Integer isWorkWithLongIds) {
        this.isWorkWithLongIds = isWorkWithLongIds;
    }

    public Date getLastUpdateForTurnstileSetting() {
        return lastUpdateForTurnstileSetting;
    }

    public void setLastUpdateForTurnstileSetting(Date lastUpdateForTurnstileSetting) {
        this.lastUpdateForTurnstileSetting = lastUpdateForTurnstileSetting;
    }
}
