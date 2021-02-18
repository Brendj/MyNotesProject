/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.02.13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianNotificationSetting {

    public enum Predefined {
        SMS_SETTING_CHANGED(1L, "Настройки были изменены", true), // наличие данной записи у связки клиент-представитель означает что настройки уведомлений отличаются от дефолтных
        SMS_NOTIFY_REFILLS(1000000000L, "Оповещать о пополнениях", EventNotificationService.NOTIFICATION_BALANCE_TOPUP),
        SMS_NOTIFY_EVENTS(1100000000L, "Оповещать о проходах", EventNotificationService.NOTIFICATION_ENTER_EVENT),
        SMS_NOTIFY_ORDERS_BAR(1200000000L, "Оповещать о покупках в буфете", EventNotificationService.MESSAGE_PAYMENT_BAR),
        SMS_NOTIFY_ORDERS_PAY(1220000000L, "Оповещать о покупках обедов", EventNotificationService.MESSAGE_PAYMENT_PAY),
        SMS_NOTIFY_ORDERS_FREE(1230000000L, "Оповещать о покупках льготного питания", EventNotificationService.MESSAGE_PAYMENT_FREE),
        SMS_NOTIFY_SUMMARY_DAY(1300000000L, "Оповещать по итогам дня", EventNotificationService.NOTIFICATION_SUMMARY_BY_DAY),
        SMS_NOTIFY_SUMMARY_WEEK(1400000000L, "Оповещать по итогам недели", EventNotificationService.NOTIFICATION_SUMMARY_BY_WEEK),
        SMS_NOTIFY_LOW_BALANCE(1500000000L, "Оповещать о снижении баланса", EventNotificationService.NOTIFICATION_LOW_BALANCE),
        SMS_NOTIFY_MUSEUM(1600000000L, "Оповещение о получении и аннулировании билета в музей", false),
        //SMS_NOTIFY_CULTURE(1700000000L, "Оповещение о посещении зданий учреждений Минкультуры", false),
        SMS_NOTIFY_SPECIAL(1800000000L, "Служебные оповещения", false);

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

        public static Predefined parse(Long value) throws Exception {
            Predefined currentPredefined = null;
            for (Predefined predefined : Predefined.values()) {
                if (predefined.value.equals(value)) {
                    currentPredefined = predefined;
                    break;
                }
            }
            if (currentPredefined == null) {
                throw new IllegalArgumentException(String.format("Неизвестный тип уведомления %s", value));
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
            if(binding.equals(EventNotificationService.NOTIFICATION_ENTER_MUSEUM)
                    || binding.equals(EventNotificationService.NOTIFICATION_NOENTER_MUSEUM)){
                return SMS_NOTIFY_MUSEUM;
            }
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
            if (value.equals(SMS_NOTIFY_SUMMARY_DAY.getValue()) || value.equals(SMS_NOTIFY_SUMMARY_WEEK.getValue())) {
                return false;
            }
            return RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.notification.forceSend", "0").equals("1") ? true : false;
        }
    }

    public static List<Long> ORDER_NOTIFY_TYPES = Arrays.asList(Predefined.SMS_NOTIFY_ORDERS_BAR.getValue(),
                                                                Predefined.SMS_NOTIFY_ORDERS_PAY.getValue(),
                                                                Predefined.SMS_NOTIFY_ORDERS_FREE.getValue());

    private Long idOfSetting;
    private Long notifyType;
    private ClientGuardian clientGuardian;
    private Date createdDate;

    protected ClientGuardianNotificationSetting() {
    }

    public ClientGuardianNotificationSetting(ClientGuardian clientGuardian, Long notifyType) {
        this.clientGuardian = clientGuardian;
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

    public ClientGuardian getClientGuardian() {
        return clientGuardian;
    }

    public void setClientGuardian(ClientGuardian clientGuardian) {
        this.clientGuardian = clientGuardian;
    }

    @Override
    public String toString() {
        return "ClientGuardianNotificationSetting{clientGuardian=" + clientGuardian + ", notifyType=" + notifyType + "}";
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
        ClientGuardianNotificationSetting setting = (ClientGuardianNotificationSetting) o;
        return notifyType.equals(setting.notifyType) && clientGuardian.equals(setting.clientGuardian);
    }

}