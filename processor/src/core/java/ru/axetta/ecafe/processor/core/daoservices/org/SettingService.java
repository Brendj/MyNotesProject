/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.org;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
@Service
@Transactional
public class SettingService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public ECafeSettings save(ECafeSettings settings) throws Exception{
        Long version = updateAndGetDOVersion();
        settings.setGlobalVersion(version);
        settings.setLastUpdate(new Date());
        return entityManager.merge(settings);
    }

    public void create(ECafeSettings settings) throws Exception {
        settings.setSendAll(SendToAssociatedOrgs.SendToSelf);
        settings.setCreatedDate(new Date());
        Long version = updateAndGetDOVersion();
        String updateString = "update ECafeSettings set deletedState=true, deleteDate=:deleteDate where orgOwner=:idoforg and settingsId=:settingsId";
        Query updateQ = entityManager.createQuery(updateString);
        updateQ.setParameter("deleteDate",new Date());
        updateQ.setParameter("idoforg",settings.getOrgOwner());
        updateQ.setParameter("settingsId",settings.getSettingsId());
        updateQ.executeUpdate();
        settings.setGlobalVersion(version);
        settings.setGlobalVersionOnCreate(version);
        entityManager.persist(settings);
    }

    private Long updateAndGetDOVersion() {
        final String qlString = "from DOVersion where distributedObjectClassName=:distributedObjectClassName";
        TypedQuery<DOVersion> query = entityManager.createQuery(qlString, DOVersion.class);
        query.setParameter("distributedObjectClassName", "ECafeSettings");
        List<DOVersion> doVersionList = query.getResultList();
        DOVersion doVersion;
        Long version;
        if (doVersionList.size() == 0) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            version = 0L;
        } else {
            doVersion = entityManager.find(DOVersion.class, doVersionList.get(0).getIdOfDOObject());
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doVersion.setDistributedObjectClassName("ECafeSettings");
        entityManager.persist(doVersion);
        return version;
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
