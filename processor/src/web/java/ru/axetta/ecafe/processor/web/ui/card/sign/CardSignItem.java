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
    private String signType;//Это тип подписи карты
    private String signTypeProvider;//Это тип подписи поставщика
    private byte[] signData;
    protected Boolean newProvider;

    private static final String NEW_PROVIDER = "По ключу производителя";//Поставщик по новому типу
    private static final String OLD_PROVIDER = "По ключу карты";//Поставщик по старому типу

    public CardSignItem(CardSign cardSign) {
        this.idOfCardSign = cardSign.getIdOfCardSign();
        this.manufacturerCode = cardSign.getManufacturerCode();
        this.manufacturerName = cardSign.getManufacturerName();
        signType = CardSign.CARDSIGN_TYPES[cardSign.getSignType()];
        signData = cardSign.getSignData();
        if (cardSign.getSigntypeprov() != null)
            signTypeProvider = CardSign.CARDSIGN_TYPES[cardSign.getSigntypeprov()];
        else
            signTypeProvider = null;
        newProvider = cardSign.getNewtypeprovider();
        if (newProvider == null)
            newProvider = false;
    }

    public static String getSignTypeFromString(String sType) {
        if (sType.equals(CardSign.CARDSIGN_SCRIPT_TYPE)) return "0";
        if (sType.equals(CardSign.CARDSIGN_ECDSA_TYPE)) return "1";
        if (sType.equals(CardSign.CARDSIGN_GOST2012_TYPE)) return "2";
        return "0";
    }

    public String getProviderType()
    {
        if (newProvider)
            return NEW_PROVIDER;
        else
            return OLD_PROVIDER;
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

    public String getSignTypeProvider() {
        return signTypeProvider;
    }

    public void setSignTypeProvider(String signTypeProvider) {
        this.signTypeProvider = signTypeProvider;
    }

    public Boolean getNewProvider() {
        if (newProvider == null)
            return false;
        return newProvider;
    }

    public void setNewProvider(Boolean newProvider) {
        this.newProvider = newProvider;
    }
}
