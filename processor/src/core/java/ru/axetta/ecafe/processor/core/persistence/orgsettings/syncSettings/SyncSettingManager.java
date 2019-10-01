/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ResSyncSettingsItem;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.SyncSettingsSectionItem;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SyncSettingManager {
    public static Long getNextVersion(Session session){
        Logger logger = LoggerFactory.getLogger(SyncSettingManager.class);
        Long result = getMaxVersion(session);
        return result + 1;
    }

    public static Long getMaxVersion(Session session) {
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM CF_OrgSettings");
        Long maxVersion = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
        return maxVersion == null ? 0 : maxVersion;
    }

    public ResSyncSettingsItem saveFromSync(Session session, SyncSettingsSectionItem item, Long idOfOrg, Date syncData,
            Long maxVersionFromARM, Long nextVersion){
        ResSyncSettingsItem result = new ResSyncSettingsItem();
        result.setContentTypeInt(item.getContentType());
        try {
            createSyncSettings(
                    item.getContentType(), item.getConcreteTime(), item.getEverySeconds(), item.getLimitStartHour(),
                    item.getLimitEndHour(), item.getMonday(), item.getTuesday(), item.getWednesday(), item.getThursday(),
                    item.getFriday(), item.getSaturday(), item.getSunday(), item.getDeleteState(), nextVersion);
        } catch (Exception e){

        }
         return result;
    }


    public void createSyncSettings(Integer contentType, List<String> concreteTime, Integer everySeconds,
            Integer limitStartHour, Integer limitEndHour, Boolean monday, Boolean tuesday, Boolean wednesday,
            Boolean thursday, Boolean friday, Boolean saturday, Boolean sunday, Boolean deleteState, Long nextVersion){
        //TODO
    }

    public List<SyncSettings> getSettingByIdOfOrgAndVersion(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(SyncSettings.class);
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("contentType"));

        return criteria.list();
    }
}
