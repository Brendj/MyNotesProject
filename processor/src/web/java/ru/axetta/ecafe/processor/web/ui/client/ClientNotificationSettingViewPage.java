/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 14.02.13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationSettingViewPage {

    private List<NotificationSettingItem> items = new ArrayList<NotificationSettingItem>();

    public List<NotificationSettingItem> getItems() {
        return items;
    }

    public int getItemCount() {
        int cnt = 0;
        for (NotificationSettingItem i : items) {
            if (i.isEnabled()) {
                cnt++;
            }
        }
        return cnt;
    }

    public void fill(Client client) throws Exception {
        List<NotificationSettingItem> items = new LinkedList<NotificationSettingItem>();
        Set<ClientNotificationSetting> settings = client.getNotificationSettings();
        for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
            if (predefined.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            items.add(new NotificationSettingItem(predefined, settings));
        }
        this.items = items;
    }
}
