/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@DependsOn("runtimeContext")
public class AutoDistributionSyncSettingsService {
    private static final Integer FULL_SYNC_COEFFICIENT = 10;
    private static final Integer ORG_SETTING_SYNC_COEFFICIENT = 15;
    private static final Integer CLIENT_DATA_SYNC_COEFFICIENT = 20;
    private static final Integer MENU_SYNC_COEFFICIENT = 12;
    private static final Integer PHOTO_SYNC_COEFFICIENT = 33;
    private static final Integer LIB_SYNC_COEFFICIENT = 17;
    private static final Map<ContentType, Integer> SYNC_CONTENT_TYPE_MAP = buildMap();
    private static final String FULL_SYNC_DEFAULT_EXPRESSION = "!22:00-05:00;!07:00-16:00";
    private static final String ORG_SETTING_SYNC_DEFAULT_EXPRESSION = "04:00-11:00;11:00-18:00";
    private static final String CLIENT_DATA_SYNC_DEFAULT_EXPRESSION = "04:00-09:00;09:00-14:0014:00-19:00";
    private static final String MENU_SYNC_DEFAULT_EXPRESSION = "04:00-13:00;13:00-22:00";
    private static final String PHOTO_SYNC_DEFAULT_EXPRESSION = "04:00-07:00";
    private static final String LIB_SYNC_DEFAULT_EXPRESSION = "16:00-22:00";

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
        Long idOfOrg = setting.getOrg().getIdOfOrg();
        Long calculationMinutesForSync = calculateTime(idOfOrg, SYNC_CONTENT_TYPE_MAP.get(setting.getContentType()));
        return null;
    }

    private Long calculateTime(Long idOfOrg, Integer coefficient) {
        return idOfOrg / coefficient;
    }
}
