/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.GoodsBasicBasket;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import org.apache.cxf.common.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class BasicGoodItem extends AbstractEntityItem<GoodsBasicBasket> {

    @Override
    public void fillForList(EntityManager entityManager, GoodsBasicBasket goodsBasicBasket) {
        idOfBasicGood = goodsBasicBasket.getIdOfBasicGood();
        guid = goodsBasicBasket.getGuid();
        createdDate = goodsBasicBasket.getCreatedDate();
        lastUpdate = goodsBasicBasket.getLastUpdate();
        nameOfGood = goodsBasicBasket.getNameOfGood();
        unitsScale = goodsBasicBasket.getUnitsScale();
        netWeight = goodsBasicBasket.getNetWeight();
        configurationProviders = goodsBasicBasket.getConfigurationProviders();
    }

    @Override
    public void fill(EntityManager entityManager, GoodsBasicBasket goodsBasicBasket) {
        fillForList(entityManager, goodsBasicBasket);
    }

    @Override
    protected void saveTo(EntityManager entityManager, GoodsBasicBasket goodsBasicBasket) {
        goodsBasicBasket.setLastUpdate(new Date());
        goodsBasicBasket.setNameOfGood(nameOfGood);
        goodsBasicBasket.setUnitsScale(unitsScale);
        goodsBasicBasket.setNetWeight(netWeight);
    }

    @Override
    protected void prepareForEntityRemove(EntityManager entityManager, GoodsBasicBasket goodsBasicBasket) {}

    @Override
    public GoodsBasicBasket getEntity(EntityManager entityManager) {
        GoodsBasicBasket basket = entityManager.find(GoodsBasicBasket.class, idOfBasicGood);
        return basket;
    }
    @Override
    public GoodsBasicBasket createEmptyEntity() {
        return new GoodsBasicBasket(UUID.randomUUID().toString());
    }

    /* fields */
    private Long idOfBasicGood;
    private String guid;
    private Date createdDate;
    private Date lastUpdate;
    private String nameOfGood;
    private UnitScale unitsScale;
    private Long netWeight;
    private Set<ConfigurationProvider> configurationProviders;

    /* Getter and Setters */
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

    public Boolean getIsGuidEmpty() {
        return StringUtils.isEmpty(guid);
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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
    public String toString() {
        return  nameOfGood;
    }

    public Set<ConfigurationProvider> getConfigurationProviders() {
        return configurationProviders;
    }

    public void setConfigurationProviders(Set<ConfigurationProvider> configurationProviders) {
        this.configurationProviders = configurationProviders;
    }
}
