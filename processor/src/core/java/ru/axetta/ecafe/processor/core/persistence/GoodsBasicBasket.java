/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodBasicBasketPrice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class GoodsBasicBasket {

    private Long idOfBasicGood;
    private String guid;
    private Date createdDate;
    private Date lastUpdate;
    private String nameOfGood;
    private UnitScale unitsScale;
    private Long netWeight;
    private GoodBasicBasketPrice goodBasicBasketPrice;
    private Set<Good> good;
    private Set<GoodBasicBasketPrice> goodBasicBasketPriceInternal;
    private Set<ConfigurationProvider> configurationProviders;

    public Set<GoodBasicBasketPrice> getGoodBasicBasketPriceInternal() {
        return goodBasicBasketPriceInternal;
    }

    public void setGoodBasicBasketPriceInternal(Set<GoodBasicBasketPrice> goodBasicBasketPriceInternal) {
        this.goodBasicBasketPriceInternal = goodBasicBasketPriceInternal;
    }

    public Set<Good> getGood() {
        return good;
    }

    public void setGood(Set<Good> good) {
        this.good = good;
    }

    public GoodBasicBasketPrice getGoodBasicBasketPrice() {
        return goodBasicBasketPrice;
    }

    public void setGoodBasicBasketPrice(GoodBasicBasketPrice goodBasicBasketPrice) {
        this.goodBasicBasketPrice = goodBasicBasketPrice;
    }

    protected GoodsBasicBasket() {
    }

    public GoodsBasicBasket(String guid) {
        this.guid = guid;
        this.createdDate = new Date();
        this.lastUpdate = new Date();
    }

    public GoodsBasicBasket(Long idOfBasicGood, String guid, Date createdDate, Date lastUpdate, String nameOfGood,
            UnitScale unitsScale, Long netWeight) {
        this.idOfBasicGood = idOfBasicGood;
        this.guid = guid;
        this.createdDate = createdDate;
        this.lastUpdate = lastUpdate;
        this.nameOfGood = nameOfGood;
        this.unitsScale = unitsScale;
        this.netWeight = netWeight;
    }

    public String getUnitsScaleString() {
        //if (unitsScale >= 0 && unitsScale < Good.UNIT_SCALES.length) {
        //    return Good.UNIT_SCALES[unitsScale];
        //}
        //return "";
        return unitsScale.toString();
    }

    public Long getIdOfBasicGood() {
        return idOfBasicGood;
    }

    public void setIdOfBasicGood(Long idOfBasicGood) {
        this.idOfBasicGood = idOfBasicGood;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdateFormatted() {
        return formatDate(lastUpdate);
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreatedDateFormatted() {
        return formatDate(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public UnitScale getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(UnitScale unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoodsBasicBasket that = (GoodsBasicBasket) o;

        if ((idOfBasicGood != that.idOfBasicGood) || (nameOfGood != that.nameOfGood)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (idOfBasicGood != null) {
            return (int) (idOfBasicGood ^ (idOfBasicGood >>> 32));
        }
        return 0;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (date != null) {
            String result = sdf.format(date);
            return result;
        } else {
            return null;
        }
    }

    public Set<ConfigurationProvider> getConfigurationProviders() {
        return configurationProviders;
    }

    public void setConfigurationProviders(Set<ConfigurationProvider> configurationProviders) {
        this.configurationProviders = configurationProviders;
    }
}
