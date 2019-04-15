/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

public enum SmartWatchTransactionTypes {
    IS_ALL_OPERATIONS("Всё"),
    IS_REPLENISHMENT("Пополнение"),
    IS_DESCRIPTION_OF_CASH("Списание");

    private String description;

    SmartWatchTransactionTypes(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }

    /*public getJsonTransactionTypesCodeBySourceTypeCode(Integer sourceTypeCode){

    }*/
}
