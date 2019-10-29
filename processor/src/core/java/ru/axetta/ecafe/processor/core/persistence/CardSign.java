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
    private byte[] privatekeycard;
    private byte[] publickeyprovider;
    private Integer signtypeprov;
    private Boolean newtypeprovider;
    private Boolean deleted;

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

    public byte[] getPrivatekeycard() {
        return privatekeycard;
    }

    public void setPrivatekeycard(byte[] privatekeycard) {
        this.privatekeycard = privatekeycard;
    }

    public byte[] getPublickeyprovider() {
        return publickeyprovider;
    }

    public void setPublickeyprovider(byte[] publickeyprovider) {
        this.publickeyprovider = publickeyprovider;
    }

    public Integer getSigntypeprov() {
        return signtypeprov;
    }

    public void setSigntypeprov(Integer signtypeprov) {
        this.signtypeprov = signtypeprov;
    }

    public Boolean getNewtypeprovider() {
        return newtypeprovider;
    }

    public void setNewtypeprovider(Boolean newtypeprovider) {
        this.newtypeprovider = newtypeprovider;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
