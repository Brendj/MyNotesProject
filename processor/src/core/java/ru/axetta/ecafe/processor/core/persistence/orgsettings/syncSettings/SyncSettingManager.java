/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions.*;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;
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
    private static final Logger logger = LoggerFactory.getLogger(SyncSettingManager.class);

    public static Long getNextVersion(Session session){
        Long result = getMaxVersion(session);
        return result + 1;
    }

    public static Long getMaxVersion(Session session) {
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM cf_syncsettings");
        Long maxVersion = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
        return maxVersion == null ? 0 : maxVersion;
    }

    public ResSyncSettingsItem changeFromSync(Session session, SyncSettings currentSetting, SyncSettingsSectionItem item, Date syncData, Long nextVersion) {
        ResSyncSettingsItem result = new ResSyncSettingsItem();
        result.setContentTypeInt(item.getContentType());
        try {
            updateSyncSettings(
                    currentSetting, item.getConcreteTime(), item.getEverySeconds(), item.getLimitStartHour(),
                    item.getLimitEndHour(), item.getMonday(), item.getTuesday(), item.getWednesday(), item.getThursday(),
                    item.getFriday(), item.getSaturday(), item.getSunday(), item.getDeleteState(), nextVersion, syncData, session);

            result.setResult(ProcessResultEnum.OK);
        } catch (SyncProcessException e){
            logger.error(String.format("Can't change in DB SyncSetting with ContentType %d for IdOfOrg %d",
                    item.getContentType(), currentSetting.getOrg().getIdOfOrg()), e);
            result.setResult(e.getExceptionRes());
        } catch (Exception e){
            logger.error(String.format("Can't change in DB SyncSetting with ContentType %d for IdOfOrg %d",
                    item.getContentType(), currentSetting.getOrg().getIdOfOrg()), e);
            result.setResult(ProcessResultEnum.INTERNAL_ERROR);
        }
        return result;
    }

    private void updateSyncSettings(SyncSettings currentSetting, List<String> concreteTime, Integer everySeconds,
            Integer limitStartHour, Integer limitEndHour, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday, Boolean sunday,
            Boolean deleteState, Long nextVersion, Date lastUpdate, Session session) throws Exception {
        if(currentSetting == null) {
            throw new IllegalArgumentException("Org is NULL, nothing change");
        }
        validateParam(concreteTime, everySeconds, limitStartHour, limitEndHour);

        if(lastUpdate == null){
            lastUpdate = new Date();
        }
        currentSetting.setLastUpdate(lastUpdate);
        currentSetting.setVersion(nextVersion);
        currentSetting.setEverySecond(everySeconds);
        currentSetting.setLimitStartHour(limitStartHour);
        currentSetting.setLimitEndHour(limitEndHour);
        currentSetting.setMonday(monday);
        currentSetting.setTuesday(tuesday);
        currentSetting.setWednesday(wednesday);
        currentSetting.setThursday(thursday);
        currentSetting.setFriday(friday);
        currentSetting.setSaturday(saturday);
        currentSetting.setSunday(sunday);
        currentSetting.setDeleteState(deleteState);
        if(everySeconds == null){

        } else {
            currentSetting.getConcreteTime();
        }

    }

    private void validateParam(List<String> concreteTime, Integer everySeconds, Integer limitStartHour,
            Integer limitEndHour) {
        if(concreteTime != null && everySeconds != null) {
            throw new AmbiguityConcreteTimeAndEverySecondsValues();
        } else if(everySeconds != null && !everySeconds.equals(0)) {
            throw new IncorrectEverySecondsException();
        } else if((limitStartHour == null && limitEndHour != null) || (limitStartHour != null && limitEndHour == null)) {
            throw new AmbiguityLimitHourException();
        } else if(limitStartHour != null && limitEndHour != null) {
            if((limitStartHour < 0 || limitStartHour > 24) || (limitEndHour < 0 || limitEndHour > 24)) {
                throw new IncorrectLimitHourException();
            }
        }
    }

    public ResSyncSettingsItem saveFromSync(Session session, SyncSettingsSectionItem item, Long idOfOrg, Date syncData, Long nextVersion){
        ResSyncSettingsItem result = new ResSyncSettingsItem();
        result.setContentTypeInt(item.getContentType());
        Org org = (Org) session.get(Org.class, idOfOrg);
        try {
            createSyncSettings(
                    item.getContentType(), item.getConcreteTime(), item.getEverySeconds(), item.getLimitStartHour(),
                    item.getLimitEndHour(), item.getMonday(), item.getTuesday(), item.getWednesday(), item.getThursday(),
                    item.getFriday(), item.getSaturday(), item.getSunday(), item.getDeleteState(), nextVersion, org, syncData, session);
        } catch (SyncProcessException e){
            logger.error(String.format("Can't save in DB SyncSetting with ContentType %d for IdOfOrg %d",
                    item.getContentType(), idOfOrg), e);
            result.setResult(e.getExceptionRes());
        } catch (Exception e){
            logger.error(String.format("Can't save in DB SyncSetting with ContentType %d for IdOfOrg %d",
                    item.getContentType(), idOfOrg), e);
            result.setResult(ProcessResultEnum.INTERNAL_ERROR);
        }
         return result;
    }

    public void createSyncSettings(Integer contentType, List<String> concreteTime, Integer everySeconds,
            Integer limitStartHour, Integer limitEndHour, Boolean monday, Boolean tuesday, Boolean wednesday,
            Boolean thursday, Boolean friday, Boolean saturday, Boolean sunday, Boolean deleteState, Long nextVersion,
            Org org, Date createDate, Session session) throws Exception {
        if(org == null) {
            throw new IllegalArgumentException("Org is NULL");
        } else if(contentType == null) {
            throw new ContentTypeIsNullException();
        }
        validateParam(concreteTime, everySeconds, limitStartHour, limitEndHour);

        if(createDate == null){
            createDate = new Date();
        }
        ContentType type = ContentType.getContentTypeByCode(contentType);
        if(type == null){
            throw new ContentTypeNotExistException();
        }

        SyncSettings syncSettings = new SyncSettings();
        syncSettings.setOrg(org);
        syncSettings.setContentType(type);
        syncSettings.setCreatedDate(createDate);
        syncSettings.setLastUpdate(createDate);
        if(concreteTime != null){
            for(String val : concreteTime){
                ConcreteTime concreteTimeObj = new ConcreteTime();
                concreteTimeObj.setConcreteTime(val);
                concreteTimeObj.setSyncSettings(syncSettings);
                syncSettings.getConcreteTime().add(concreteTimeObj);
            }
        }
        syncSettings.setEverySecond(everySeconds);
        syncSettings.setDeleteState(deleteState);
        syncSettings.setLimitStartHour(limitStartHour);
        syncSettings.setLimitEndHour(limitEndHour);
        syncSettings.setMonday(monday);
        syncSettings.setTuesday(tuesday);
        syncSettings.setWednesday(wednesday);
        syncSettings.setThursday(thursday);
        syncSettings.setFriday(friday);
        syncSettings.setSaturday(saturday);
        syncSettings.setSunday(sunday);
        syncSettings.setVersion(nextVersion);

        session.persist(syncSettings);
    }

    public List<SyncSettings> getSettingByIdOfOrgAndVersion(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(SyncSettings.class);
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("contentType"));

        return criteria.list();
    }
}
