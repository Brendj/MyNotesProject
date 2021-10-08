/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum Gender {
    FEMALE("Ж"),
    MALE("М");

    final String letter;

    Gender(String letter){
        this.letter = letter;
    }

    @Override
    public String toString(){
        return letter;
    }
}
