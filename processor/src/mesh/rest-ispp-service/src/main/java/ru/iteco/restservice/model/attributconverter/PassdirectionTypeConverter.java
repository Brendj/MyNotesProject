/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.attributconverter;

import ru.iteco.restservice.model.enums.PassdirectionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PassdirectionTypeConverter implements AttributeConverter<PassdirectionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PassdirectionType passdirectionType) {
        if(passdirectionType == null) {
            return null;
        }
        return passdirectionType.getDirection();
    }

    @Override
    public PassdirectionType convertToEntityAttribute(Integer integer) {
        if(integer == null) {
            return null;
        }
        for(PassdirectionType type : PassdirectionType.values()){
            if(type.getDirection().equals(integer)){
                return type;
            }
        }
        return null;
    }
}
