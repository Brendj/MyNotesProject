/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.autodistribution;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSetting;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@DependsOn("runtimeContext")
public class AutoDistributionSyncSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(AutoDistributionSyncSettingsService.class);
    private static final Integer FULL_SYNC_COEFFICIENT = 10;
    private static final Integer ORG_SETTING_SYNC_COEFFICIENT = 15;
    private static final Integer CLIENT_DATA_SYNC_COEFFICIENT = 20;
    private static final Integer MENU_SYNC_COEFFICIENT = 12;
    private static final Integer PHOTO_SYNC_COEFFICIENT = 33;
    private static final Integer LIB_SYNC_COEFFICIENT = 17;
    private static final Integer NUMBER_OF_ATTEMPTS = 50;
    private static final Integer MAX_MINUTES_IN_DAY = 24 * 60;
    private static final Map<ContentType, Integer> SYNC_CONTENT_TYPE_MAP = buildMap();
    private static final String FULL_SYNC_DEFAULT_EXPRESSION = "!22:00-05:00;!07:00-16:00";
    private static final String ORG_SETTING_SYNC_DEFAULT_EXPRESSION = "04:00-11:00;11:00-18:00";
    private static final String CLIENT_DATA_SYNC_DEFAULT_EXPRESSION = "04:00-09:00;09:00-14:0014:00-19:00";
    private static final String MENU_SYNC_DEFAULT_EXPRESSION = "04:00-13:00;13:00-22:00";
    private static final String PHOTO_SYNC_DEFAULT_EXPRESSION = "04:00-07:00";
    private static final String LIB_SYNC_DEFAULT_EXPRESSION = "16:00-22:00";

    private List<TimePeriod> allowedFullSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenFullSyncPeriod = new LinkedList<>();
    private List<TimePeriod> allowedOrgSettingSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenOrgSettingSyncPeriod = new LinkedList<>();
    private List<TimePeriod> allowedClientDataSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenClientDataSyncPeriod = new LinkedList<>();
    private List<TimePeriod> allowedMenuSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenMenuSyncPeriod = new LinkedList<>();
    private List<TimePeriod> allowedPhotoSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenPhotoSyncPeriod = new LinkedList<>();
    private List<TimePeriod> allowedLibSyncPeriods = new LinkedList<>();
    private List<TimePeriod> forbiddenLibSyncPeriod = new LinkedList<>();

    @PostConstruct
    private void buildTimePeriodLists(){
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            String fullSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_FULL_SYNC_EXPRESSION);
            if (StringUtils.isBlank(fullSyncExpression)) {
                fullSyncExpression = FULL_SYNC_DEFAULT_EXPRESSION;
            }
            String orgSettingSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_ORG_SETTING_SYNC_EXPRESSION);
            if (StringUtils.isBlank(orgSettingSyncExpression)) {
                orgSettingSyncExpression = ORG_SETTING_SYNC_DEFAULT_EXPRESSION;
            }
            String clientDataSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_CLIENT_DATA_SYNC_EXPRESSION);
            if (StringUtils.isBlank(clientDataSyncExpression)) {
                clientDataSyncExpression = CLIENT_DATA_SYNC_DEFAULT_EXPRESSION;
            }
            String menuSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_MENU_SYNC_EXPRESSION);
            if (StringUtils.isBlank(menuSyncExpression)) {
                menuSyncExpression = MENU_SYNC_DEFAULT_EXPRESSION;
            }
            String photoSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_PHOTO_SYNC_EXPRESSION);
            String libSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_LIB_SYNC_EXPRESSION);

            buildAllowedAndForbiddenList(fullSyncExpression, allowedFullSyncPeriods, forbiddenFullSyncPeriod);
            buildAllowedAndForbiddenList(orgSettingSyncExpression, allowedOrgSettingSyncPeriods,
                    forbiddenOrgSettingSyncPeriod);
            buildAllowedAndForbiddenList(clientDataSyncExpression, allowedClientDataSyncPeriods,
                    forbiddenClientDataSyncPeriod);
            buildAllowedAndForbiddenList(menuSyncExpression, allowedMenuSyncPeriods, forbiddenMenuSyncPeriod);
            buildAllowedAndForbiddenList(photoSyncExpression, allowedPhotoSyncPeriods, forbiddenPhotoSyncPeriod);
            buildAllowedAndForbiddenList(libSyncExpression, allowedLibSyncPeriods, forbiddenLibSyncPeriod);

            logger.info(String.format("Service created witch configs:\n"
                            + "FullSync: %s \n"
                            + "OrgSettingSync: %s \n"
                            + "ClientDataSync: %s \n"
                            + "MenuSync: %s \n"
                            + "PhotoSync: %s \n"
                            + "LibSync: %s \n",
                    fullSyncExpression, orgSettingSyncExpression, clientDataSyncExpression,
                    menuSyncExpression, photoSyncExpression, libSyncExpression));
        } catch (Exception e){
            logger.error("Can't build configuration for AutoDistribution: ", e);
        }
    }

    private void buildAllowedAndForbiddenList(String expression, List<TimePeriod> allowedList,
            List<TimePeriod> forbiddenList) {
        allowedList.clear();
        forbiddenList.clear();
        if(StringUtils.isBlank(expression)){
            return;
        }
        String[] periods = expression.split(";");
        for (String period : periods) {
            if (period.contains("!")) {
                forbiddenList.add(new TimePeriod(period.replace("!", "")));
            } else {
                allowedList.add(new TimePeriod(period));
            }
        }
    }

    private static Map<ContentType, Integer> buildMap() {
        Map<ContentType, Integer> map = new HashMap<>();
        map.put(ContentType.FULL_SYNC, FULL_SYNC_COEFFICIENT);
        map.put(ContentType.ORGSETTINGS, ORG_SETTING_SYNC_COEFFICIENT);
        map.put(ContentType.CLIENTS_DATA, CLIENT_DATA_SYNC_COEFFICIENT);
        map.put(ContentType.MENU, MENU_SYNC_COEFFICIENT);
        map.put(ContentType.PHOTOS, PHOTO_SYNC_COEFFICIENT);
        map.put(ContentType.LIBRARY, LIB_SYNC_COEFFICIENT);

        return Collections.unmodifiableMap(map);
    }

    public SyncSetting distributionSyncSettings(SyncSetting setting) throws Exception {
        if(!SYNC_CONTENT_TYPE_MAP.containsKey(setting.getContentType())){
            logger.warn(String.format("Unsupported type of SyncSetting: %s", setting.getContentType()));
            return setting; // if wrong ContentType, then do nothing
        }

        boolean isChanges = false;
        Long idOfOrg = setting.getOrg().getIdOfOrg();
        Integer calculationMinutesForSync = calculateTime(idOfOrg, SYNC_CONTENT_TYPE_MAP.get(setting.getContentType()));
        List<String> calculationPeriods = Collections.emptyList();
        switch (setting.getContentType()){
            case FULL_SYNC:
                try {
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedFullSyncPeriods, forbiddenFullSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for full sync: ", e);
                    logger.info("FULL_SYNC User expression dropped, try regenerate by default expression: "
                            + FULL_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(FULL_SYNC_DEFAULT_EXPRESSION,
                            allowedFullSyncPeriods, forbiddenFullSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedFullSyncPeriods, forbiddenFullSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
            case ORGSETTINGS:
                try {
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedOrgSettingSyncPeriods, forbiddenOrgSettingSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for OrgSetting sync: ", e);
                    logger.info("ORGSETTINGS User expression dropped, try regenerate by default expression: "
                            + ORG_SETTING_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(ORG_SETTING_SYNC_DEFAULT_EXPRESSION,
                            allowedOrgSettingSyncPeriods, forbiddenOrgSettingSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedOrgSettingSyncPeriods, forbiddenOrgSettingSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
            case CLIENTS_DATA:
                try {
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                        allowedClientDataSyncPeriods, forbiddenClientDataSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for ClientData sync: ", e);
                    logger.info("CLIENTS_DATA User expression dropped, try regenerate by default expression: "
                            + CLIENT_DATA_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(CLIENT_DATA_SYNC_DEFAULT_EXPRESSION,
                            allowedClientDataSyncPeriods, forbiddenClientDataSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedClientDataSyncPeriods, forbiddenClientDataSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
            case MENU:
                try {
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                        allowedMenuSyncPeriods, forbiddenMenuSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for Menu sync: ", e);
                    logger.info("MENU User expression dropped, try regenerate by default expression: "
                            + MENU_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(MENU_SYNC_DEFAULT_EXPRESSION,
                            allowedMenuSyncPeriods, forbiddenMenuSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedMenuSyncPeriods, forbiddenMenuSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
            case PHOTOS:
                try {
                calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                        allowedPhotoSyncPeriods, forbiddenPhotoSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for Photo sync: ", e);
                    logger.info("PHOTOS User expression dropped, try regenerate by default expression: "
                            + PHOTO_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(PHOTO_SYNC_DEFAULT_EXPRESSION,
                            allowedPhotoSyncPeriods, forbiddenPhotoSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedPhotoSyncPeriods, forbiddenPhotoSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
            case LIBRARY:
                try {
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                        allowedLibSyncPeriods, forbiddenLibSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                } catch (Exception e) {
                    logger.error("Can't calculate sync Time for Lib sync: ", e);
                    logger.info("LIBRARY User expression dropped, try regenerate by default expression: "
                            + LIB_SYNC_DEFAULT_EXPRESSION);
                    buildAllowedAndForbiddenList(LIB_SYNC_DEFAULT_EXPRESSION,
                            allowedLibSyncPeriods, forbiddenLibSyncPeriod);
                    calculationPeriods = calculatePeriodsForSyncSetting(calculationMinutesForSync,
                            allowedLibSyncPeriods, forbiddenLibSyncPeriod);
                    isChanges = !CollectionUtils.isEmpty(calculationPeriods);
                }
                break;
        }

        if(isChanges) {
            setting.setConcreteTime(StringUtils.join(calculationPeriods, SyncSetting.SEPARATOR));
            setting.setMonday(true);
            setting.setTuesday(true);
            setting.setWednesday(true);
            setting.setThursday(true);
            setting.setFriday(true);
            setting.setSaturday(true);
            setting.setSunday(true);
        }

        return setting;
    }

    private List<String> calculatePeriodsForSyncSetting(final Integer calculationMinutesForSync,  List<TimePeriod> allowedList,
            List<TimePeriod> forbiddenList) throws Exception {
        if(allowedList.isEmpty() && forbiddenList.isEmpty()){
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();

        int calculationMinutesForSyncVariable = 0;
        if(!allowedList.isEmpty()){
            for(TimePeriod allowedPeriod : allowedList){
                calculationMinutesForSyncVariable = allowedPeriod.getStartTimeInMinutes() + calculationMinutesForSync % allowedPeriod.getEndTimeInMinutes();
                for(int i = 0; i != NUMBER_OF_ATTEMPTS + 1; i++) {
                    if(i > NUMBER_OF_ATTEMPTS){
                        throw new IllegalArgumentException("Attempts to calculate the time ended");
                    }
                    if (allowedPeriod.between(calculationMinutesForSyncVariable)) {
                        result.add(buildSessionTime(calculationMinutesForSyncVariable));
                        break;
                    } else {
                        if (calculationMinutesForSyncVariable > allowedPeriod.getEndTimeInMinutes()) {
                            calculationMinutesForSyncVariable =
                                    (calculationMinutesForSyncVariable % allowedPeriod.getEndTimeInMinutes()) + allowedPeriod.getStartTimeInMinutes();
                        } else {
                            calculationMinutesForSyncVariable =
                                    calculationMinutesForSyncVariable + allowedPeriod.getStartTimeInMinutes();
                        }
                    }
                }
            }
        } else {
            calculationMinutesForSyncVariable = calculationMinutesForSync % MAX_MINUTES_IN_DAY;
            for (int i = 0; i != NUMBER_OF_ATTEMPTS + 1; i++) {
                if (i > NUMBER_OF_ATTEMPTS) {
                    throw new IllegalArgumentException("Attempts to calculate the time ended");
                }
                for (TimePeriod forbiddenPeriod : forbiddenList) {
                    if(forbiddenPeriod.between(calculationMinutesForSyncVariable)){
                        calculationMinutesForSyncVariable =
                                (calculationMinutesForSyncVariable + forbiddenPeriod.getEndTimeInMinutes()) % MAX_MINUTES_IN_DAY;
                    }
                }
                if(!intersectionWithForbiddenPeriods(calculationMinutesForSyncVariable, forbiddenList)){
                    result.add(buildSessionTime(calculationMinutesForSyncVariable));
                    break;
                }
            }
        }
        return result;
    }

    private String buildSessionTime(int calculationMinutesForSyncVariable) {
        int hour;
        int minutes;
        hour = calculationMinutesForSyncVariable / 60;
        minutes = calculationMinutesForSyncVariable % 60;

        return String.format("%02d:%02d", hour, minutes);
    }

    private boolean intersectionWithForbiddenPeriods(int calculationMinutesForSyncVariable, List<TimePeriod> forbiddenList) {
        boolean isIntersection = false;
        for(TimePeriod period : forbiddenList){
            if(period.between(calculationMinutesForSyncVariable)){
                isIntersection = true;
            }
        }
        return isIntersection;
    }

    private Integer calculateTime(Long idOfOrg, Integer coefficient) {
        return idOfOrg.intValue() / coefficient;
    }
}
