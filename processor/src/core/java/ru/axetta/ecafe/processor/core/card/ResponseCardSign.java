/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

public class ResponseCardSign extends ErrorCartSign {
    private long cardno;  //Номер на идентификаторе
    private short sizeDate;
    private short sizeSign;
    private byte[] allDate; //Исходные данные + ЭЦП

    public long getCardno() {
        return cardno;
    }

    public void setCardno(long cardno) {
        this.cardno = cardno;
    }

    public byte[] getAllDate() {
        return allDate;
    }

    public void setAllDate(byte[] allDate) {
        this.allDate = allDate;
    }

    public short getSizeDate() {
        return sizeDate;
    }

    public void setSizeDate(short sizeDate) {
        this.sizeDate = sizeDate;
    }

    public short getSizeSign() {
        return sizeSign;
    }

    public void setSizeSign(short sizeSign) {
        this.sizeSign = sizeSign;
    }
}
