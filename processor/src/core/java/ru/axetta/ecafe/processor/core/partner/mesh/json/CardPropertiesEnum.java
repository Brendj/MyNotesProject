/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.json;

public enum CardPropertiesEnum implements PropertyField {
    CARD_UID("card_uid"),
    CARD_TYPE("card_type"),
    PROVIDER_CERTIFICATE_NUMBER("provider_certificate_number"),
    BOARD_CARD_NUMBER("board_card_number"),
    ISSUE_DATE("issue_date"),
    ACTION_PERIOD("action_period"),
    CARD_STATUS("card_status");

    private String fieldName;

    CardPropertiesEnum(String fieldName){
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
