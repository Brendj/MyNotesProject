/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.waybill;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfWayBillDifference;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfWayBillDifferencePosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.WayBill;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.WayBillPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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
public class WayBillItem extends AbstractEntityItem<WayBill> {

    private Long idOfWayBill;

    private String number;
    private Date dateOfWayBill;
    private String state;
    private Org shipperOrg;
    private Org receiverOrg;
    private ActOfWayBillDifference actOfWayBillDifference;

    private Org orgOwner;
    private String deletedState;
    private static String[] states = {"Активно", "Удалено"};

    @Override
    public void fillForList(EntityManager entityManager, WayBill entity) {
        idOfWayBill = entity.getGlobalId();
        number = entity.getNumber();
        dateOfWayBill = entity.getDateOfWayBill();
        state = WayBill.STATES[entity.getState()];
        if(entity.getShipper()!=null){
            shipperOrg = entityManager.find(Org.class, Long.parseLong(entity.getShipper()));
        }
        if(entity.getOrgOwner()!=null){
            receiverOrg = entityManager.find(Org.class, Long.parseLong(entity.getReceiver()));
        }
        actOfWayBillDifference = entity.getActOfWayBillDifference();
        if(entity.getOrgOwner()!=null){
            orgOwner = entityManager.find(Org.class, entity.getOrgOwner());
        }
        if (entity.getDeletedState()){
            deletedState = "Удалено";
        } else  {
            deletedState = "Активно";
        }
    }

    @Override
    protected void fill(EntityManager entityManager, WayBill entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, WayBill entity) {}

    @Override
    public WayBill getEntity(EntityManager entityManager) {
        return entityManager.find(WayBill.class,idOfWayBill);
    }

    @Override
    protected WayBill createEmptyEntity() {
        return null;
    }

    public Long getIdOfWayBill() {
        return idOfWayBill;
    }

    public String getNumber() {
        return number;
    }

    public Date getDateOfWayBill() {
        return dateOfWayBill;
    }

    public String getState() {
        return state;
    }

    public Org getShipperOrg() {
        return shipperOrg;
    }

    public Org getReceiverOrg() {
        return receiverOrg;
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
