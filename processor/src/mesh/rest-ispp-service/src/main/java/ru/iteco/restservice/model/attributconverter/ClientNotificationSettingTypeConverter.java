/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.attributconverter;


import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ClientNotificationSettingTypeConverter implements AttributeConverter<ClientNotificationSettingType, Long> {

    @Override
    public Long convertToDatabaseColumn(ClientNotificationSettingType clientNotificationSettingType) {
        if(clientNotificationSettingType == null){
            return null;
        }
        return clientNotificationSettingType.getCode();
    }

    @Override
    public ClientNotificationSettingType convertToEntityAttribute(Long code) {
        if(code == null){
            return null;
        }
        for(ClientNotificationSettingType type : ClientNotificationSettingType.values()){
            if(type.getCode().equals(code)){
               return type;
            }
        }
        return null;
    }
}
