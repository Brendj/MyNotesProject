/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.org;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingValueParser;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

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
    private static final Logger logger = LoggerFactory.getLogger(SettingService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public ECafeSettings save(ECafeSettings settings) throws Exception {
        Long version = updateAndGetDOVersion();
        settings.setGlobalVersion(version);
        settings.setLastUpdate(new Date());
        processOrgSetting(settings);
        return entityManager.merge(settings);
    }

    private void processOrgSetting(ECafeSettings settings) {
        try {
            Session persistenceSession = entityManager.unwrap(Session.class);
            Date now = new Date();
            Long lastVersionOfOrgSetting = OrgSettingDAOUtils.getLastVersionOfOrgSettings(persistenceSession);
            Long lastVersionOfOrgSettingItem = OrgSettingDAOUtils.getLastVersionOfOrgSettingsItem(persistenceSession);

            Long nextVersionOfOrgSetting = (lastVersionOfOrgSetting == null ? 0L : lastVersionOfOrgSetting) + 1L;
            Long nextVersionOfOrgSettingItem =
                    (lastVersionOfOrgSettingItem == null ? 0L : lastVersionOfOrgSettingItem) + 1L;

            OrgSetting setting = OrgSettingDAOUtils.getOrgSettingByGroupIdAndOrg(persistenceSession,
                    settings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING,
                    settings.getOrgOwner().intValue());
            if (setting == null) {
                setting = new OrgSetting();
                setting.setCreatedDate(now);
                setting.setIdOfOrg(settings.getOrgOwner());
                setting.setSettingGroup(OrgSettingGroup.getGroupById(settings.getSettingsId().getId() + OrgSettingGroup.OFFSET_IN_RELATION_TO_ECAFESETTING));
                setting.setLastUpdate(now);
                setting.setVersion(nextVersionOfOrgSetting);
                persistenceSession.save(setting);
            } else {
                setting.setLastUpdate(now);
                setting.setVersion(nextVersionOfOrgSetting);
            }

            SettingValueParser valueParser = new SettingValueParser(settings.getSettingValue(), settings.getSettingsId());
            Set<OrgSettingItem> itemsFromECafeSetting = valueParser.getParserBySettingValue()
                    .buildSetOfOrgSettingItem(setting, nextVersionOfOrgSetting);
            Map<Integer, OrgSettingItem> itemsFromOrgSetting = buildHashMap(setting.getOrgSettingItems());

            for (OrgSettingItem item : itemsFromECafeSetting) {
                if (!itemsFromOrgSetting.containsKey(item.getSettingType())) {
                    setting.getOrgSettingItems().add(item);
                    persistenceSession.persist(item);
                } else {
                    OrgSettingItem orgSettingItem = itemsFromOrgSetting.get(item.getSettingType());
                    orgSettingItem.setLastUpdate(now);
                    orgSettingItem.setVersion(nextVersionOfOrgSettingItem);
                    orgSettingItem.setSettingValue(item.getSettingValue());
                    persistenceSession.persist(orgSettingItem);
                }
            }
            persistenceSession.persist(setting);
        } catch (Exception e){
            logger.error("Can't process OrgSetting by ECafeSetting: ", e);
        }
    }

    private Map<Integer, OrgSettingItem> buildHashMap(Set<OrgSettingItem> orgSettingItems) {
        Map<Integer, OrgSettingItem> map = new HashMap<>();
        for(OrgSettingItem item : orgSettingItems){
            map.put(item.getSettingType(), item);
        }
        return map;
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
        processOrgSetting(settings);
        entityManager.persist(settings);
    }

    public Long updateAndGetDOVersion() {
        return DAOService.getInstance().getDistributedObjectVersion("ECafeSettings");
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
