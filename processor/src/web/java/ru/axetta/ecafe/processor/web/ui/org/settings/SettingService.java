/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.springframework.beans.factory.annotation.Autowired;
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
@Service
@Transactional
public class SettingService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;

    public ECafeSettings save(ECafeSettings settings) throws Exception{
        settings.setGlobalVersion(daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName()));
        settings.setLastUpdate(new Date());
        return entityManager.merge(settings);
    }

    public void create(ECafeSettings settings) throws Exception{
        settings.setSendAll(SendToAssociatedOrgs.SendToSelf);
        settings.setCreatedDate(new Date());
        Long version = daoService.updateVersionByDistributedObjects(ECafeSettings.class.getSimpleName());
        settings.setGlobalVersion(version);
        settings.setGlobalVersionOnCreate(version);
        entityManager.persist(settings);
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
