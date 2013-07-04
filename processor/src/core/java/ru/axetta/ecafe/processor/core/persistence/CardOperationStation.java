package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.06.13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public enum CardOperationStation {
    REGISTRATION,ISSUE,RETURN_OF,BLOCKING;

    public static CardOperationStation value(int ordinal){
        if(ordinal>-1 && ordinal<CardOperationStation.values().length){
            return CardOperationStation.values()[ordinal];
        } else {
            return null;
        }
    }
}
