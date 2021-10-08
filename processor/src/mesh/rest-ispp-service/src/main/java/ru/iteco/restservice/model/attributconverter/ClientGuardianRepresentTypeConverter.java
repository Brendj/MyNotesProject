/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.attributconverter;

import ru.iteco.restservice.model.enums.ClientGuardianRepresentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ClientGuardianRepresentTypeConverter implements AttributeConverter<ClientGuardianRepresentType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ClientGuardianRepresentType clientGuardianRepresentType) {
        if(clientGuardianRepresentType == null){
            return null;
        }
        return clientGuardianRepresentType.getVal();
    }

    @Override
    public ClientGuardianRepresentType convertToEntityAttribute(Integer integer) {
        if(integer == null){
            return null;
        }
        for(ClientGuardianRepresentType type : ClientGuardianRepresentType.values()){
            if(type.getVal().equals(integer)){
                return type;
            }
        }

        return null;
    }
}
