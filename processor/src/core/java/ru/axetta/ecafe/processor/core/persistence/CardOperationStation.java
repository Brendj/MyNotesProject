package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public enum CardOperationStation {

    REGISTRATION("Зарегистрирована"),
    ISSUE("Выдана"),
    RETURN_OF("Возвращена"),
    DELETED("Удалена"),
    BLOCKING("Заблокирована");

    private final String description;

    private CardOperationStation(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static CardOperationStation value(int ordinal){
        if(ordinal>-1 && ordinal<CardOperationStation.values().length){
            return CardOperationStation.values()[ordinal];
        } else {
            return null;
        }
    }
}
