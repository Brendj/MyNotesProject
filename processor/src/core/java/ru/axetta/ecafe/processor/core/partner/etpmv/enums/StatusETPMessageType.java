package ru.axetta.ecafe.processor.core.partner.etpmv.enums;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum StatusETPMessageType {
    /*0*/NOT_DELIVERED_TO_ADDRESSEE("1030", "Не доставлено адресанту", "Сообщение не доставлено до ЛК", "Портал", "ИС ПП"),
    /*1*/DELIVERED_TO_ADDRESSEE("1040", "Доставлено адресанту", "Сообщение доставлено до ЛК", "Портал", "ИС ПП"),
    /*2*/HANDED_TO_THE_ADDRESSEE("1077", "Вручено адресанту", "Сообщение прочтено получателем в ЛК", "Портал", "ИС ПП"),
    /*3*/REFUSAL("8011", "Отказ", "Отказ от услуги", "Портал", "ИС ПП"),
    REFUSE_TIMEOUT("8031", "Срок отказа истек", "Срок отказа от услуги истек", "ИС ПП", "Портал"),
    REFUSE_USER("1080.1", "Отказ в предоставлении услуги", "Пользователь отказался от услуги", "ИС ПП", "Портал"),
    REFUSE_SYSTEM("1080.2", "Отказ в предоставлении услуги", "Отказ в предоставлении услуги по инициативе ведомства", "ИС ПП", "Портал");

    private final String code;
    private final String description;
    private final String note;
    private final String sender;
    private final String receiver;

    StatusETPMessageType(String code, String description, String note, String sender, String receiver) {
        this.code = code;
        this.description = description;
        this.note = note;
        this.sender = sender;
        this.receiver = receiver;
    }

    private static Map<String, StatusETPMessageType> mapCode = new HashMap<String,StatusETPMessageType>();
    static {
        for (StatusETPMessageType value : StatusETPMessageType.values()) {
            mapCode.put(value.getCode(), value);
        }
    }

    public static StatusETPMessageType findStatusETPMessageType (String code)
    {
        return mapCode.get(code);
    }

    public Integer getPureCode() {
        if (code.contains(".")) {
            return Integer.valueOf(StringUtils.substringBefore(code, "."));
        } else {
            return Integer.valueOf(code);
        }
    }

    public String getReason() {
        if (code.contains(".")) {
            return StringUtils.substringAfter(code, ".");
        } else {
            return null;
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
