/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 14.02.13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationSettingViewPage {

    public static class Item {

        private Long notifyType;
        private String notifyName;
        private boolean enabled;

        public Long getNotifyType() {
            return notifyType;
        }

        public String getNotifyName() {
            return notifyName;
        }

        public boolean getEnabled() {
            return enabled;
        }

        public Item(ClientNotificationSetting.Predefined type, Set<ClientNotificationSetting> settings) {
            this.enabled = false;
            this.notifyType = type.getValue();
            this.notifyName = type.getName();
            for (ClientNotificationSetting setting : settings) {
                if (setting.getNotifyType() == this.notifyType) {
                    this.enabled = true;
                    break;
                }
            }
        }
    }


    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        int cnt = 0;
        for (Item i : items) {
            if (i.enabled) {
                cnt++;
            }
        }
        return cnt;
    }

    public void fill(Client client) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Set<ClientNotificationSetting> settings = client.getNotificationSettings();
        //  Если настройки пустые изначально, это обозначает, что у пользователя настройки по умолчанию, а
        //  следовательно необходимо указать их (в список включенных помещаем все, у которых стоит флаг default)
        if (settings.isEmpty()) {
            for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
                if (!predefined.isEnabledAtDefault()) {
                    continue;
                }
                settings.add(new ClientNotificationSetting(client, predefined.getValue()));
            }
        }
        for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
            if (predefined.getValue() == ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue()) {
                continue;
            }
            items.add(new Item(predefined, settings));
        }
        this.items = items;
    }
}
