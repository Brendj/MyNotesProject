/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 23.11.2017.
 */
public class GoodBBMenuPrice {
    private Long idOfGoodBBMenuPrice;
    private Long idOfBasicGood;
    private Long idOfConfigurationProvider;
    private Date menuDate;
    private Long price;
    private String menuDetailName;

    public GoodBBMenuPrice() {

    }

    public Long getIdOfGoodBBMenuPrice() {
        return idOfGoodBBMenuPrice;
    }

    public void setIdOfGoodBBMenuPrice(Long idOfGoodBBMenuPrice) {
        this.idOfGoodBBMenuPrice = idOfGoodBBMenuPrice;
    }

    public Long getIdOfBasicGood() {
        return idOfBasicGood;
    }

    public void setIdOfBasicGood(Long idOfBasicGood) {
        this.idOfBasicGood = idOfBasicGood;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }
}
