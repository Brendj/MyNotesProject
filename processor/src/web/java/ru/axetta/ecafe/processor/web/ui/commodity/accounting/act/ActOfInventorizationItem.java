/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.supplier.ActOfInventorization;
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

public class ActOfInventorizationItem extends AbstractEntityItem<ActOfInventorization> {

    /* Идентификатор объекта */
    private Long idOfActOfInventorization;
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
    private Date dateOfAct;
    private String number;
    private String commission;

    @Override
    public void fillForList(EntityManager entityManager, ActOfInventorization entity) {
        idOfActOfInventorization = entity.getGlobalId();
        version = entity.getGlobalVersion();
        createdDate = entity.getCreatedDate();
        lastUpdate = entity.getLastUpdate();
        deleteDate = entity.getDeleteDate();
        guid = entity.getGuid();
        Org org = entityManager.find(Org.class, entity.getOrgOwner());
        orgOwner = org;
        sendAll = entity.getSendAll();
        dateOfAct = entity.getDateOfAct();
        number = entity.getNumber();
        commission = entity.getCommission();
        if (entity.getDeletedState()){
            deletedState = "Удалено";
        } else  {
            deletedState = "Активно";
        }
    }

    @Override
    protected void fill(EntityManager entityManager, ActOfInventorization entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, ActOfInventorization entity) {}

    @Override
    public ActOfInventorization getEntity(EntityManager entityManager) {
        return entityManager.find(ActOfInventorization.class, idOfActOfInventorization);
    }

    @Override
    protected ActOfInventorization createEmptyEntity() {
        return null;
    }

    public Long getIdOfActOfInventorization() {
        return idOfActOfInventorization;
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

    public SendToAssociatedOrgs getSendAll() {
        return sendAll;
    }

    public Date getDateOfAct() {
        return dateOfAct;
    }

    public String getNumber() {
        return number;
    }

    public String getCommission() {
        return commission;
    }
}
