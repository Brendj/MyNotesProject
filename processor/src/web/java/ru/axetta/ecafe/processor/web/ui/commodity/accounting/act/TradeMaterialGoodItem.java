/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TradeMaterialGood;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */

public class TradeMaterialGoodItem extends AbstractEntityItem<TradeMaterialGood> {

    /* Идентификатор объекта */
    private Long idOfTradeMaterialGood;
    /* версия объекта */
    private Long version;
    /* дата создания объекта */
    private Date createdDate;
    /* дата мзминения объекта */
    private Date lastUpdate;
    /* дата удаления объекта */
    private Date deleteDate;
    /* статус объекта (активен/удален) */
    private String deletedState;
    /* GUID объекта */
    private String guid;
    /* Идентификатор организации */
    private Org orgOwner;
    private SendToAssociatedOrgs sendAll;
    private Good good;
    private Date goodsCreationDate;
    private Long lifeTime;
    private String unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long nds;
    private Long selfPrice;

    @Override
    public void fillForList(EntityManager entityManager, TradeMaterialGood entity) {
        idOfTradeMaterialGood = entity.getGlobalId();
        version = entity.getGlobalVersion();
        createdDate = entity.getCreatedDate();
        lastUpdate = entity.getLastUpdate();
        deleteDate = entity.getDeleteDate();
        if (entity.getDeletedState()){
            deletedState = "Удалено";
        } else  {
            deletedState = "Активно";
        }
        guid = entity.getGuid();
        orgOwner = entityManager.find(Org.class, entity.getOrgOwner());
        sendAll = entity.getSendAll();
        good = entity.getGood();
        goodsCreationDate = entity.getGoodsCreationDate();
        lifeTime = entity.getLifeTime();
        unitsScale = entity.getUnitScale().toString();//TradeMaterialGood.UNIT_SCALES[entity.getUnitScale()];
        totalCount = entity.getTotalCount();
        netWeight = entity.getNetWeight();
        nds = entity.getNds();
        selfPrice= entity.getSelfPrice();
    }

    @Override
    protected void fill(EntityManager entityManager, TradeMaterialGood entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, TradeMaterialGood entity) {}

    @Override
    public TradeMaterialGood getEntity(EntityManager entityManager) {
        return entityManager.find(TradeMaterialGood.class, idOfTradeMaterialGood);
    }

    @Override
    protected TradeMaterialGood createEmptyEntity() {
        return null;
    }

    public Long getIdOfTradeMaterialGood() {
        return idOfTradeMaterialGood;
    }

    public Long getVersion() {
        return version;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public String getDeletedState() {
        return deletedState;
    }

    public String getGuid() {
        return guid;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public Good getGood() {
        return good;
    }

    public Date getGoodsCreationDate() {
        return goodsCreationDate;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public String getUnitsScale() {
        return unitsScale;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public Long getNds() {
        return nds;
    }

    public Long getSelfPrice() {
        return selfPrice;
    }
}
