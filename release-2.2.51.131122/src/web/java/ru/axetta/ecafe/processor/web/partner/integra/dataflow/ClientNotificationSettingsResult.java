/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.02.13
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationSettingsResult {

    private List<ClientNotificationSettingsItem> settings = new ArrayList<ClientNotificationSettingsItem>();
    private Long resultCode;
    private String description;

    public ClientNotificationSettingsResult(Long resultCode, String desc) {
        this.resultCode = resultCode;
        this.description = desc;
    }

    public ClientNotificationSettingsResult() {
    }

    public List<ClientNotificationSettingsItem> getSettings() {
        return settings;
    }

    public void setSettings(List<ClientNotificationSettingsItem> settings) {
        this.settings = settings;
    }

    public Long getResultCode() {
        return resultCode;
    }

    public void setResultCode(Long resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
