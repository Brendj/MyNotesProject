/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.persistence.CardSign;

/**
 * Created by i.semenov on 28.09.2017.
 */
public class CardSignItem {
    private Integer idOfCardSign;
    private Integer manufacturerCode;
    private String manufacturerName;
    private String signType;
    private byte[] signData;

    public CardSignItem(CardSign cardSign) {
        this.idOfCardSign = cardSign.getIdOfCardSign();
        this.manufacturerCode = cardSign.getManufacturerCode();
        this.manufacturerName = cardSign.getManufacturerName();
        signType = CardSign.CARDSIGN_TYPES[cardSign.getSignType()];
        signData = cardSign.getSignData();
    }

    public static String getSignTypeFromString(String sType) {
        if (sType.equals(CardSign.CARDSIGN_SCRIPT_TYPE)) return "0";
        if (sType.equals(CardSign.CARDSIGN_ECDSA_TYPE)) return "1";
        return "0";
    }

    public String getPrintedName() {
        return manufacturerCode + " - " + manufacturerName;
    }

    public Integer getIdOfCardSign() {
        return idOfCardSign;
    }

    public void setIdOfCardSign(Integer idOfCardSign) {
        this.idOfCardSign = idOfCardSign;
    }

    public Integer getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(Integer manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
    }
}
