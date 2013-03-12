/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientNotificationSetting;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
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


    public static void setNotificationSettings (Client cl, HttpServletRequest request, String paramPrefix, String onHTMLPattern) {
        /* Удаление всех существующих настроек оповещения смс */
        removeSMSSettings(cl.getNotificationSettings());


        Set<ClientNotificationSetting> notificationSettingsSet = new HashSet<ClientNotificationSetting>();

        for (ClientNotificationSetting.Predefined def : ClientNotificationSetting.Predefined.values()) {
            if (def.getValue().longValue() == ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue().longValue()) {
                continue;
            }

            String paramName = paramPrefix + "-" + def.getValue().longValue();
            boolean isEnabled = StringUtils.equals(request.getParameter(paramName), onHTMLPattern);
            if (isEnabled) {
                ClientNotificationSetting newSetting = new ClientNotificationSetting(cl, def.getValue().longValue());
                notificationSettingsSet.add(newSetting);
            }
        }
        ClientNotificationSetting newSetting = new ClientNotificationSetting(cl,
                ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue());
        notificationSettingsSet.add(newSetting);
        cl.setNotificationSettings(notificationSettingsSet);
    }

    public static List<Item> getNotificationSettings (Client cl) {
        Set<ClientNotificationSetting> settings = cl.getNotificationSettings();
        List<Item> result = new ArrayList<Item>();

        for (ClientNotificationSetting.Predefined def : ClientNotificationSetting.Predefined.values()) {
            if (def.getValue().longValue() == ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue().longValue()) {
                continue;
            }


            Item nItem = new Item (def.getValue(), def.getName());

            if (settings != null && settings.size() > 0) {
                for (ClientNotificationSetting s : settings) {
                    if (s.getNotifyType() == def.getValue().longValue()) {
                        nItem.setEnabled(true);
                        break;
                    }
                }
            } else {
                if (def.isEnabledAtDefault()) {
                    nItem.setEnabled(true);
                }
            }
            result.add(nItem);
        }
    return result;
    }


    public static class Item {
        private long notificationType;
        private String notificationTypeName;
        private boolean enabled;

        public Item (long notificationType, String notificationTypeName) {
            this.notificationType = notificationType;
            this.notificationTypeName = notificationTypeName;
            enabled = false;
        }

        public Item (long notificationType, String notificationTypeName, boolean enabled) {
            this.notificationType = notificationType;
            this.notificationTypeName = notificationTypeName;
            this.enabled = enabled;
        }

        public long getNotificationType() {
            return notificationType;
        }

        public String getNotificationTypeName() {
            return notificationTypeName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Transactional
    public static void removeSMSSettings(Set<ClientNotificationSetting> notificationSettings) {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            for (ClientNotificationSetting currentSetting : notificationSettings) {
                currentSetting = (ClientNotificationSetting) persistenceSession.merge(currentSetting);
                persistenceSession.delete(currentSetting);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to remove active settings", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
    }
}
