/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 27.09.2017.
 */
public class CardSign {
    private Integer idOfCardSign;
    private Integer signType;
    private byte[] signData;
    private Integer manufacturerCode;
    private String manufacturerName;

    public static final String CARDSIGN_SCRIPT_TYPE = "Scrypt";
    public static final String CARDSIGN_ECDSA_TYPE = "ECDSA";
    public static final String CARDSIGN_GOST2012_TYPE = "Gost2012_256";
    public static final String[] CARDSIGN_TYPES = {CARDSIGN_SCRIPT_TYPE, CARDSIGN_ECDSA_TYPE, CARDSIGN_GOST2012_TYPE};

    public CardSign() {

    }

    public CardSign(Integer signType, byte[] signData, Integer manufacturerCode, String manufacturerName) {
        this.signType = signType;
        this.signData = signData;
        this.manufacturerCode = manufacturerCode;
        this.manufacturerName = manufacturerName;
    }

    public Integer getIdOfCardSign() {
        return idOfCardSign;
    }

    public void setIdOfCardSign(Integer idOfCardSign) {
        this.idOfCardSign = idOfCardSign;
    }

    public Integer getSignType() {
        return signType;
    }

    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
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
}
