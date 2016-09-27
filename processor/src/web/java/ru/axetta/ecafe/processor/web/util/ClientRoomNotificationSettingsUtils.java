/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.03.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class ClientRoomNotificationSettingsUtils {

    public static void setNotificationSettings(Client cl, HttpServletRequest request, String paramPrefix,
            String onHTMLPattern) {
        for (ClientNotificationSetting.Predefined def : ClientNotificationSetting.Predefined.values()) {
            String paramName = paramPrefix + "-" + def.getValue();
            boolean isEnabled = StringUtils.equals(request.getParameter(paramName), onHTMLPattern);
            ClientNotificationSetting setting = new ClientNotificationSetting(cl, def.getValue());
            if (isEnabled || def.getValue()
                    .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                cl.getNotificationSettings().add(setting);
            } else {
                cl.getNotificationSettings().remove(setting);
            }
        }
    }

    public static List<NotificationSettingItem> getNotificationSettings(Client cl) {
        Set<ClientNotificationSetting> settings = cl.getNotificationSettings();
        List<NotificationSettingItem> result = new ArrayList<NotificationSettingItem>();
        for (ClientNotificationSetting.Predefined def : ClientNotificationSetting.Predefined.values()) {
            if (def.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            NotificationSettingItem nItem = new NotificationSettingItem(def, settings);
            result.add(nItem);
        }
        return result;
    }
}
