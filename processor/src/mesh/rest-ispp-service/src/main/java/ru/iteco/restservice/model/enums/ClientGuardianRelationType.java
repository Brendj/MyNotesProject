/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum ClientGuardianRelationType {
    Mother("Мать"),
    Father("Отец"),
    Representative("Представитель"),
    Guardian("Опекун/попечитель"),
    Foster_Parent("Приемный родитель"),
    Adoptive_Parent("Усыновитель"),
    Foster_Carer("Патронатный воспитатель"),
    Other("Иное");

    final String description;

    ClientGuardianRelationType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
