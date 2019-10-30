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

import org.apache.commons.lang.StringUtils;
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
        return result + 1L;
    }

    public static Long getMaxVersion(Session session) {
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM cf_syncsettings");
        Long maxVersion = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
        return maxVersion == null ? 0 : maxVersion;
    }

    public ResSyncSettingsItem changeFromSync(Session session, SyncSetting currentSetting, SyncSettingsSectionItem item, Date syncData, Long nextVersion) {
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

    private void updateSyncSettings(SyncSetting currentSetting, String concreteTime, Integer everySeconds,
            Integer limitStartHour, Integer limitEndHour, Boolean monday, Boolean tuesday, Boolean wednesday,
            Boolean thursday, Boolean friday, Boolean saturday, Boolean sunday, Boolean deleteState, Long nextVersion,
            Date lastUpdate, Session session) throws Exception {
        if (currentSetting == null) {
            throw new IllegalArgumentException("Org is NULL, nothing change");
        }
        validateParam(concreteTime, everySeconds, limitStartHour, limitEndHour);

        if (lastUpdate == null) {
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
        if (everySeconds == null ) {
            currentSetting.setConcreteTime(concreteTime);
        }
        session.merge(currentSetting);
    }

    private void validateParam(String concreteTime, Integer everySeconds, Integer limitStartHour,
            Integer limitEndHour) {
        if(StringUtils.isNotEmpty(concreteTime) && everySeconds != null) {
            throw new AmbiguityConcreteTimeAndEverySecondsValues();
        } else if(everySeconds != null && everySeconds.equals(0)) {
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
        result.setResult(ProcessResultEnum.OK);
         return result;
    }

    public void createSyncSettings(Integer contentType, String concreteTime, Integer everySeconds,
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

        SyncSetting syncSetting = new SyncSetting();
        syncSetting.setOrg(org);
        syncSetting.setContentType(type);
        syncSetting.setCreatedDate(createDate);
        syncSetting.setLastUpdate(createDate);
        syncSetting.setConcreteTime(concreteTime);
        syncSetting.setEverySecond(everySeconds);
        syncSetting.setDeleteState(deleteState);
        syncSetting.setLimitStartHour(limitStartHour);
        syncSetting.setLimitEndHour(limitEndHour);
        syncSetting.setMonday(monday);
        syncSetting.setTuesday(tuesday);
        syncSetting.setWednesday(wednesday);
        syncSetting.setThursday(thursday);
        syncSetting.setFriday(friday);
        syncSetting.setSaturday(saturday);
        syncSetting.setSunday(sunday);
        syncSetting.setVersion(nextVersion);

        session.persist(syncSetting);
    }

    public List<SyncSetting> getSettingByIdOfOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(SyncSetting.class);
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.addOrder(Order.asc("contentType"));

        return criteria.list();
    }

    public List<SyncSetting> getSettingByIdOfOrgAndGreaterThenVersion(Session session, Long idOfOrg, Long maxVersionFromARM) {
        Criteria criteria = session.createCriteria(SyncSetting.class);
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.add(Restrictions.gt("version", maxVersionFromARM));
        criteria.addOrder(Order.asc("contentType"));
        return  criteria.list();
    }

    public void saveOrUpdateSettingFromReportPage(Session session, SyncSetting setting, Long nextVersion) throws Exception {
        if(setting == null){
            throw new IllegalArgumentException("Get instance of SyncSetting as NULL");
        }
        Date updateDate = new Date();
        if(setting.getIdOfSyncSetting() == null){
            createSyncSettings(setting.getContentType().getTypeCode(), setting.getConcreteTime(), setting.getEverySecond(),
                    setting.getLimitStartHour(), setting.getLimitEndHour(), setting.getMonday(), setting.getTuesday(), setting.getWednesday(),
                    setting.getThursday(), setting.getFriday(), setting.getSaturday(), setting.getSunday(),
                    false, nextVersion, setting.getOrg(), updateDate, session);
        } else {
            session.merge(setting);
            updateSyncSettings(setting, setting.getConcreteTime(), setting.getEverySecond(), setting.getLimitStartHour(),
                    setting.getLimitEndHour(), setting.getMonday(), setting.getTuesday(), setting.getWednesday(),
                    setting.getThursday(), setting.getFriday(), setting.getSaturday(), setting.getSunday(),
                    false, nextVersion, updateDate, session);
        }
    }
}
