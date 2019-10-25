/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.autodistribution;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettings;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@DependsOn("runtimeContext")
public class AutoDistributionSyncSettingsService {
    private static final Integer FULL_SYNC_COEFFICIENT = 10;
    private static final Integer ORG_SETTING_SYNC_COEFFICIENT = 15;
    private static final Integer CLIENT_DATA_SYNC_COEFFICIENT = 20;
    private static final Integer MENU_SYNC_COEFFICIENT = 12;
    private static final Integer PHOTO_SYNC_COEFFICIENT = 33;
    private static final Integer LIB_SYNC_COEFFICIENT = 17;
    private static final Integer NUMBER_OF_ATTEMPTS = 15;
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
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String fullSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_FULL_SYNC_EXPRESSION);
        if(StringUtils.isBlank(fullSyncExpression)){
            fullSyncExpression = FULL_SYNC_DEFAULT_EXPRESSION;
        }
        String orgSettingSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_ORG_SETTING_SYNC_EXPRESSION);
        if(StringUtils.isBlank(orgSettingSyncExpression)){
            orgSettingSyncExpression = ORG_SETTING_SYNC_DEFAULT_EXPRESSION;
        }
        String clientDataSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_CLIENT_DATA_SYNC_EXPRESSION);
        if(StringUtils.isBlank(clientDataSyncExpression)){
            clientDataSyncExpression = CLIENT_DATA_SYNC_DEFAULT_EXPRESSION;
        }
        String menuSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_MENU_SYNC_EXPRESSION);
        if(StringUtils.isBlank(menuSyncExpression)){
            menuSyncExpression = MENU_SYNC_DEFAULT_EXPRESSION;
        }
        String photoSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_PHOTO_SYNC_EXPRESSION);
        if(StringUtils.isBlank(photoSyncExpression)){
            photoSyncExpression = PHOTO_SYNC_DEFAULT_EXPRESSION;
        }
        String libSyncExpression = runtimeContext.getOptionValueString(Option.OPTION_LIB_SYNC_EXPRESSION);
        if(StringUtils.isBlank(libSyncExpression)){
            libSyncExpression = LIB_SYNC_DEFAULT_EXPRESSION;
        }
        buildAllowedAndForbiddenList(fullSyncExpression, allowedFullSyncPeriods, forbiddenFullSyncPeriod);
        buildAllowedAndForbiddenList(orgSettingSyncExpression, allowedOrgSettingSyncPeriods, forbiddenOrgSettingSyncPeriod);
        buildAllowedAndForbiddenList(clientDataSyncExpression, allowedClientDataSyncPeriods, forbiddenClientDataSyncPeriod);
        buildAllowedAndForbiddenList(menuSyncExpression, allowedMenuSyncPeriods, forbiddenMenuSyncPeriod);
        buildAllowedAndForbiddenList(photoSyncExpression, allowedPhotoSyncPeriods, forbiddenPhotoSyncPeriod);
        buildAllowedAndForbiddenList(libSyncExpression, allowedLibSyncPeriods, forbiddenLibSyncPeriod);
    }

    private void buildAllowedAndForbiddenList(String expression, List<TimePeriod> allowedList,
            List<TimePeriod> forbiddenList) {
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

    public SyncSettings distributionSyncSettingsService(SyncSettings setting){
        if(!SYNC_CONTENT_TYPE_MAP.containsKey(setting.getContentType())){
            return setting; // if wrong ContentType, then do nothing
        }
        setting.setMonday(true);
        setting.setTuesday(true);
        setting.setWednesday(true);
        setting.setThursday(true);
        setting.setFriday(true);
        setting.setSaturday(true);
        setting.setSunday(true);

        Long idOfOrg = setting.getOrg().getIdOfOrg();
        Integer calculationMinutesForSync = calculateTime(idOfOrg, SYNC_CONTENT_TYPE_MAP.get(setting.getContentType()));
        String s;
        switch (setting.getContentType()){
            case FULL_SYNC:
            //todo
        }
        return setting;
    }

    private Integer calculateTime(Long idOfOrg, Integer coefficient) {
        return idOfOrg.intValue() / coefficient;
    }
}
