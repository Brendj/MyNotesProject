/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfWayBillDifference;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfWayBillDifferencePosition;
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
public class ActOfWayBillDifferencePositionItem extends AbstractEntityItem<ActOfWayBillDifferencePosition> {

    private Long idOfActOfWayBillDifferencePosition;
    private Good good;
    private Date goodsCreationDate;
    private Long lifeTime;
    private String unitsScale;
    private Long totalCount;
    private Long netWeight;
    private Long grossWeight;
    private Long price;
    private Long nds;
    private ActOfWayBillDifference actOfWayBillDifference;
    private Org orgOwner;
    private String deletedState;

    @Override
    public void fillForList(EntityManager entityManager, ActOfWayBillDifferencePosition entity) {
        idOfActOfWayBillDifferencePosition = entity.getGlobalId();
        good = entity.getGood();
        actOfWayBillDifference = entity.getActOfWayBillDifference();
        goodsCreationDate = entity.getGoodsCreationDate();
        lifeTime = entity.getLifeTime();
        //unitsScale = ActOfWayBillDifferencePosition.UNIT_SCALES[entity.getUnitsScale()];
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
    protected void fill(EntityManager entityManager, ActOfWayBillDifferencePosition entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, ActOfWayBillDifferencePosition entity) {}

    @Override
    public ActOfWayBillDifferencePosition getEntity(EntityManager entityManager) {
        return entityManager.find(ActOfWayBillDifferencePosition.class,idOfActOfWayBillDifferencePosition);
    }

    @Override
    protected ActOfWayBillDifferencePosition createEmptyEntity() {
        return null;
    }

    public Long getIdOfActOfWayBillDifferencePosition() {
        return idOfActOfWayBillDifferencePosition;
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

    public Long getGrossWeight() {
        return grossWeight;
    }

    public Long getPrice() {
        return price;
    }

    public Long getNds() {
        return nds;
    }

    public ActOfWayBillDifference getActOfWayBillDifference() {
        return actOfWayBillDifference;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public String getDeletedState() {
        return deletedState;
    }
}
