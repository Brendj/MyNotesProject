/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.json;

public enum CardFieldsEnum {
    CARD_UID("card_uid", "UID карты"),
    CARD_TYPE("card_type", "Тип карты"),
    PROVIDER_CERTIFICATE_NUMBER("provider_certificate_number", "Номер сертификата поставщика карты"),
    BOARD_CARD_NUMBER("board_card_number", "Номер на карте"),
    ISSUE_DATE("issue_date", "Дата выдачи"),
    ACTION_PERIOD("action_period","Срок действия"),
    CARD_STATUS("card_status","Статус карты");

    private String fieldName;
    private String description;

    CardFieldsEnum(String fieldName, String description){
        this.fieldName = fieldName;
        this.description = description;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescription() {
        return description;
    }
}
