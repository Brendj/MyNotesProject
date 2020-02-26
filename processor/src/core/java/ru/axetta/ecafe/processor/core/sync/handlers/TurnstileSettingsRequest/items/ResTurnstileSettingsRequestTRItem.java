/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items;

import ru.axetta.ecafe.processor.core.persistence.TurnstileSettings;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResTurnstileSettingsRequestTRItem extends ResTurnstileSettingsRequestItem {

    private String turnstileId;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Integer isWorkWithLongIds;
    private Date lastUpdateForTurnstileSetting;

    public ResTurnstileSettingsRequestTRItem(TurnstileSettings turnstileSettings, Integer resCode) {
        this.turnstileId = turnstileSettings.getTurnstileId();
        this.controllerModel = turnstileSettings.getControllerModel();
        this.controllerFirmwareVersion = turnstileSettings.getControllerFirmwareVersion();
        this.isWorkWithLongIds = turnstileSettings.getIsWorkWithLongIds();
        setResCode(resCode);
        setLastUpdateForTurnstileSetting(turnstileSettings.getLastUpdateForTurnstile());
    }

    public ResTurnstileSettingsRequestTRItem(Integer resCode, String errorMessage) {
        super(errorMessage, resCode);
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("TR");
        Integer resCode = getResCode();
        String errorMessage = null;
        if (null != turnstileId) {
            XMLUtils.setAttributeIfNotNull(element, "TurnstileId", turnstileId);
        }
        if (null != controllerModel) {
            XMLUtils.setAttributeIfNotNull(element, "ControllerModel", controllerModel);
        }
        if (null != controllerFirmwareVersion) {
            XMLUtils.setAttributeIfNotNull(element, "ControllerFirmwareVersion", controllerFirmwareVersion);
        }
        if (null != isWorkWithLongIds) {
            XMLUtils.setAttributeIfNotNull(element, "IsWorkWithLongIds", isWorkWithLongIds);
        }
        Date lastUpdate = getLastUpdateForTurnstileSetting();
        if (null != lastUpdate) {
            XMLUtils.setAttributeIfNotNull(element, "LastUpdate", lastUpdateForTurnstileSetting);
        }
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        return element;
    }

    public Date getLastUpdateForTurnstileSetting() {
        return lastUpdateForTurnstileSetting;
    }

    public void setLastUpdateForTurnstileSetting(Date lastUpdateForTurnstileSetting) {
        this.lastUpdateForTurnstileSetting = lastUpdateForTurnstileSetting;
    }
}
