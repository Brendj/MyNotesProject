/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.org;

import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.04.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class SettingRepository {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public ECafeSettings save(ECafeSettings settings) throws Exception{
        Long version = DOVersionRepository.updateClassVersion(ECafeSettings.class.getSimpleName(), entityManager.unwrap(Session.class));
        settings.setGlobalVersion(version);
        //settings.setGlobalVersion(daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName()));
        settings.setLastUpdate(new Date());
        return entityManager.merge(settings);
    }

    public void create(ECafeSettings settings) throws Exception{
        settings.setSendAll(SendToAssociatedOrgs.SendToSelf);
        settings.setCreatedDate(new Date());
        final Session session = entityManager.unwrap(Session.class);
        Long version = DOVersionRepository.updateClassVersion(ECafeSettings.class.getSimpleName(), session);
        //Long version = daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName());
        settings.setGlobalVersion(version);
        settings.setGlobalVersionOnCreate(version);

        //settings.beforePersist(entityManager.unwrap(Session.class), settings.getOrgOwner());
        entityManager.persist(settings);
        final String updateString = "update ECafeSettings set deletedState=true, deleteDate=current_date where orgOwner=:idoforg and settingsId=:settingsId";
        final Query updateQ = session.createQuery(updateString);
        updateQ.setParameter("idoforg",settings.getOrgOwner());
        updateQ.setParameter("settingsId",settings.getSettingsId());
        updateQ.executeUpdate();
    }

    /* Вывести список всех возможных принтеров организации */
    public List<String> findAllPrinterNames(){
        String sql = "select distinct SUBSTRING(setting.settingValue, 0,locate(';',setting.settingValue)) from ECafeSettings setting where setting.settingsId in :settingsId";
        List<SettingsIds> settingsIdsCollection = new ArrayList<SettingsIds>(3);
        settingsIdsCollection.add(SettingsIds.CardBalanceReportPrinter);
        settingsIdsCollection.add(SettingsIds.CashierCheckPrinter);
        settingsIdsCollection.add(SettingsIds.SalesReportPrinter);
        TypedQuery<String> query = entityManager.createQuery(sql, String.class);
        query.setParameter("settingsId",settingsIdsCollection);
        return query.getResultList();
    }

}
