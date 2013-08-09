/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;
import ru.axetta.ecafe.processor.web.ui.client.items.NotificationSettingItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   static Logger logger = LoggerFactory.getLogger(ClientRoomNotificationSettingsUtils.class);


    public static void setNotificationSettings (Session session, Client cl, HttpServletRequest request, String paramPrefix, String onHTMLPattern) {
        /* Удаление всех существующих настроек оповещения смс */
        cl.getNotificationSettings().clear();
        // Причина вызова flush() описана здесь https://forum.hibernate.org/viewtopic.php?t=934483 :)
        session.flush();
        for (ClientNotificationSetting.Predefined def : ClientNotificationSetting.Predefined.values()) {
            if (def.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            String paramName = paramPrefix + "-" + def.getValue();
            boolean isEnabled = StringUtils.equals(request.getParameter(paramName), onHTMLPattern);
            if (isEnabled) {
                ClientNotificationSetting newSetting = new ClientNotificationSetting(cl, def.getValue());
                cl.getNotificationSettings().add(newSetting);
            }
        }
        ClientNotificationSetting newSetting = new ClientNotificationSetting(cl,
                ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue());
        cl.getNotificationSettings().add(newSetting);
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
