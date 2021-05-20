/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum ClientGuardianRelationType {
    MOTHER("Мать"),
    FATHER("Отец"),
    REPRESENTATIVE("Доверенный представитель"),
    GUARDIAN("Опекун"),
    FOSTER_PARENT("Приемный родитель"),
    ADOPTIVE_PARENT("Усыновитель"),
    FOSTER_CARER("Патронатный воспитатель"),
    OTHER("Иное"),
    UNCLE("Дядя"),
    AUNT("Тётя"),
    BROTHER("Брат"),
    SISTER("Сестра"),
    GRANDMOTHER("Бабушка"),
    GRANDFATHER("Дедушка");

    final String description;

    ClientGuardianRelationType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ClientGuardianRelationType of(Integer i){
        if(i == null){
            return null;
        }
        for(ClientGuardianRelationType type : ClientGuardianRelationType.values()){
            if(i.equals(type.ordinal())){
                return type;
            }
        }
        return OTHER;
    }
}
