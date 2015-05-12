/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * User: shamil
 * Date: 12.05.15
 * Time: 9:37
 */
public enum CardOperationType {
    REGISTER("Регистрация", 0),                            //0
    ISSUE("Выдача клиенту",1),                            //1
    ISSUE_TEMP("Выдача карты клиенту как временную",2),   //2
    ISSUE_VISITOR("Выдача карты посетителю",3),           //3
    RETURN("Возврат карты",4),                            //4
    BLOCK("Блокирование карты",5),                        //5
    BLOCK_AND_RETURN("Блокирование карты с возвратом",6), //6
    UNBLOCK("Разблокирование карты",7);                   //7


    private final String value;
    public final int order;

    private CardOperationType(String value, int order) {
        this.value = value;
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public int getOrder() {
        return order;
    }
}
