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
    public List<ClientNotificationSettingsItem> settings;
    public Long resultCode;
    public String description;

    public ClientNotificationSettingsResult(Long resultCode, String desc) {

        this.resultCode = resultCode;
        this.description = desc;
    }

    public ClientNotificationSettingsResult (){}


    public List<ClientNotificationSettingsItem> getSettings() {
        if (settings == null) {
            settings = new ArrayList<ClientNotificationSettingsItem>();
        }
        return this.settings;
    }
}
