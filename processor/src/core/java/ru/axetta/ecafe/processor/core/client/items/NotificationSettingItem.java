/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardianNotificationSetting;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 09.08.13
 * Time: 12:30
 */

public class NotificationSettingItem {

    private Long notifyType;
    private String notifyName;
    private boolean enabled;

    public NotificationSettingItem(ClientNotificationSetting.Predefined type, Set<ClientNotificationSetting> settings) {
        this.enabled = false;
        this.notifyType = type.getValue();
        this.notifyName = type.getName();
        for (ClientNotificationSetting setting : settings) {
            if (setting.getNotifyType().equals(this.notifyType)) {
                this.enabled = true;
                break;
            }
        }
    }

    public NotificationSettingItem(ClientGuardianNotificationSetting.Predefined type, Set<ClientGuardianNotificationSetting> settings) {
        this.enabled = false;
        this.notifyType = type.getValue();
        this.notifyName = type.getName();
        for (ClientGuardianNotificationSetting setting : settings) {
            if (setting.getNotifyType().equals(this.notifyType)) {
                this.enabled = true;
                break;
            }
        }
    }

    public NotificationSettingItem(ClientGuardianNotificationSetting.Predefined type) {
        this.notifyType = type.getValue();
        this.notifyName = type.getName();
        this.enabled = type.isEnabledAtDefault();
    }

    public NotificationSettingItem(ClientGuardianNotificationSetting.Predefined type, boolean enabled) {
        this.notifyType = type.getValue();
        this.notifyName = type.getName();
        this.enabled = enabled;
    }

    public Long getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Long notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyName() {
        return notifyName;
    }

    public void setNotifyName(String notifyName) {
        this.notifyName = notifyName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
