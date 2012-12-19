/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.act;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents.ActOfInventarization;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */

public class ActOfInventarizationItem extends AbstractEntityItem<ActOfInventarization> {

    /* Идентификатор объекта */
    private Long idOfActOfInventarization;
    /* версия объекта */
    private Long version;
    /* дата создания объекта */
    private Date createdDate;
    /* дата мзминения объекта */
    private Date lastUpdate;
    /* дата удаления объекта */
    private Date deleteDate;
    /* статус объекта (активен/удален) */
    private Boolean deletedState;
    /* GUID объекта */
    private String guid;
    /* Идентификатор организации */
    private Org orgOwner;
    private SendToAssociatedOrgs sendAll;
    private Date dateOfAct;
    private String number;
    private String commission;

    @Override
    public void fillForList(EntityManager entityManager, ActOfInventarization entity) {
        idOfActOfInventarization = entity.getGlobalId();
        version = entity.getGlobalVersion();
        createdDate = entity.getCreatedDate();
        lastUpdate = entity.getLastUpdate();
        deleteDate = entity.getDeleteDate();
        deletedState = entity.getDeletedState();
        guid = entity.getGuid();
        Org org = entityManager.find(Org.class, entity.getOrgOwner());
        orgOwner = org;
        sendAll = entity.getSendAll();
        dateOfAct = entity.getDateOfAct();
        number = entity.getNumber();
        commission = entity.getCommission();
    }

    @Override
    protected void fill(EntityManager entityManager, ActOfInventarization entity) {
        fillForList(entityManager, entity);
    }

    @Override
    protected void saveTo(EntityManager entityManager, ActOfInventarization entity) {}

    @Override
    public ActOfInventarization getEntity(EntityManager entityManager) {
        return entityManager.find(ActOfInventarization.class, idOfActOfInventarization);
    }

    @Override
    protected ActOfInventarization createEmptyEntity() {
        return null;
    }

    public Long getIdOfActOfInventarization() {
        return idOfActOfInventarization;
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

    public Boolean getDeletedState() {
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
