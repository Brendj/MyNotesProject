/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.service.EventNotificationService;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.02.13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationSetting {

    public enum Predefined {
        SMS_SETTING_CHANGED(1L, "Настройки были изменены", true), // наличие данной записи у клиента означает что настройки уведомлений отличаются от дефолтных
        SMS_NOTIFY_REFILLS(1000000000L, "Оповещать о пополнениях", EventNotificationService.NOTIFICATION_BALANCE_TOPUP, true),
        SMS_NOTIFY_EVENTS(1100000000L, "Оповещать о проходах", EventNotificationService.NOTIFICATION_ENTER_EVENT, true),
        SMS_NOTIFY_ORDERS(1200000000L, "Оповещать о покупках", EventNotificationService.MESSAGE_PAYMENT);


        private Long value;
        private String name;
        private String binding;
        private boolean enabledAtDefault;

        private Predefined(Long value, String name) {
            this.value = value;
            this.name = name;
            this.enabledAtDefault = false;
        }

        private Predefined(Long value, String name, boolean enabledAtDefault) {
            this.value = value;
            this.name = name;
            this.enabledAtDefault = false;
        }

        private Predefined(Long value, String name, String binding) {
            this.value = value;
            this.name = name;
            this.binding = binding;
            this.enabledAtDefault = false;
        }

        private Predefined(Long value, String name, String binding, boolean enabledAtDefault) {
            this.value = value;
            this.name = name;
            this.binding = binding;
            this.enabledAtDefault = enabledAtDefault;
        }

        public static Predefined parse(Long value) {
            Predefined currentPredefined = null;
            for (Predefined predefined : Predefined.values()) {
                if (predefined.value.equals(value)) {
                    currentPredefined = predefined;
                    break;
                }
            }
            return currentPredefined;
        }

        public static Predefined parse(String value) {
            Predefined currentPredefined = null;
            for (Predefined predefined : Predefined.values()) {
                if (predefined.name.equals(value)) {
                    currentPredefined = predefined;
                    break;
                }
            }
            return currentPredefined;
        }

        public static Predefined parseByBinding(String binding) {
            Predefined currentPredefined = null;
            for (Predefined predefined : Predefined.values()) {
                if (predefined.binding != null && predefined.binding.equals(binding)) {
                    currentPredefined = predefined;
                    break;
                }
            }
            return currentPredefined;
        }

        public String getName() {
            return name;
        }

        public Long getValue() {
            return value;
        }

        public String getBinding() {
            return binding;
        }

        public boolean isEnabledAtDefault() {
            return enabledAtDefault;
        }
    }

    private Long idOfSetting;
    private Long notifyType;
    private Client client;
    private Date createdDate;


    protected ClientNotificationSetting() {
    }

    public ClientNotificationSetting(Client client, Long notifyType) {
        this.client = client;
        this.notifyType = notifyType;
        createdDate = new Date();
    }

    public Long getIdOfSetting() {
        return idOfSetting;
    }

    public void setIdOfSetting(Long idOfSetting) {
        this.idOfSetting = idOfSetting;
    }

    public Long getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Long notifyType) {
        this.notifyType = notifyType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "ClientSms{client=" + client + ", notifyType=" + notifyType + "}";
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientNotificationSetting setting = (ClientNotificationSetting) o;
        return notifyType.equals(setting.notifyType) && client.equals(setting.client);
    }

    @Override
    public int hashCode() {
        int result = notifyType.hashCode();
        result = 31 * result + client.hashCode();
        return result;
    }
}