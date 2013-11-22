/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill_old;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBill;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.WayBillPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.12.12
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class WayBillPositionItem extends AbstractEntityItem<WayBillPosition> {

    private Long idOfWayBillPosition;

    private String unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Date goodsCreationDate;
    private Long lifeTime;
    private Long price;
    private Long nds;
    private Good good;
    private WayBill wayBill;

    private Org orgOwner;
    private String deletedState;

    @Override
    public void fillForList(EntityManager entityManager, WayBillPosition entity) {
        idOfWayBillPosition = entity.getGlobalId();
        good = entity.getGood();
        wayBill = entity.getWayBill();
        goodsCreationDate = entity.getGoodsCreationDate();
        lifeTime = entity.getLifeTime();
        //unitsScale = WayBillPosition.UNIT_SCALES[entity.getUnitsScale()];
        unitsScale = entity.getUnitsScale().toString();
        totalCount = entity.getTotalCount();
        netWeight = entity.getNetWeight();
        grossWeight = entity.getGrossWeight();
        price= entity.getPrice();
        nds = entity.getNds();
        orgOwner = entityManager.find(Org.class, entity.getOrgOwner());
        if (entity.getDeletedState()){
            deletedState = "Удалено";
        } else  {
            deletedState = "Активно";
        }
    }

    @Override
    protected void fill(EntityManager entityManager, WayBillPosition entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, WayBillPosition entity) {}

    @Override
    public WayBillPosition getEntity(EntityManager entityManager) {
        return entityManager.find(WayBillPosition.class,idOfWayBillPosition);
    }

    @Override
    protected WayBillPosition createEmptyEntity() {
        return null;
    }

    public Long getIdOfWayBillPosition() {
        return idOfWayBillPosition;
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

    public Long getGrossWeight() {
        return grossWeight;
    }

    public Date getGoodsCreationDate() {
        return goodsCreationDate;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public Long getPrice() {
        return price;
    }

    public Long getNds() {
        return nds;
    }

    public Good getGood() {
        return good;
    }

    public WayBill getWayBill() {
        return wayBill;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public String getDeletedState() {
        return deletedState;
    }
}
