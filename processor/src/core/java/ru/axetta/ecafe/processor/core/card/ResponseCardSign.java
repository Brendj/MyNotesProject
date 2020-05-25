/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

public class ResponseCardSign extends ErrorCartSign {
    private long uid;   //Физический номер
    private short memSize; //Более или менее 128 байт
    private String allDate; //Исходные данные + ЭЦП

    public String getAllDate() {
        return allDate;
    }

    public void setAllDate(String allDate) {
        this.allDate = allDate;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public short getMemSize() {
        return memSize;
    }

    public void setMemSize(short memSize) {
        this.memSize = memSize;
    }
}