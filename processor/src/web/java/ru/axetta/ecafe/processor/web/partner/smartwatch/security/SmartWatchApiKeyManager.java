/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.security;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.ejb.DependsOn;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@DependsOn("runtimeContext")
public class SmartWatchApiKeyManager {

    private static final String KEY_PARAM = ".apikey";
    private static final String ENABLE_PARAM = ".enable";
    private final Map<String, String> enableVendors;

    public SmartWatchApiKeyManager() {
        RuntimeContext context = RuntimeContext.getInstance();
        Map<String, String> vendorsMap = new HashMap<>();

        for (SmartWatchVendorsEnum vendor : SmartWatchVendorsEnum.values()) {
            boolean enable = Boolean.parseBoolean(context.getPropertiesValue(
                    RuntimeContext.PROCESSOR_PARAM_BASE + vendor.configParamName + ENABLE_PARAM, "true")
            );
            if (enable) {
                String apiKey = context.getPropertiesValue(
                        RuntimeContext.PROCESSOR_PARAM_BASE + vendor.configParamName + KEY_PARAM, "");
                if(StringUtils.isEmpty(apiKey)) continue;
                vendorsMap.put(apiKey, vendor.toString());
            }
        }

        this.enableVendors = Collections.unmodifiableMap(vendorsMap);
    }

    public String getVendorByApiKey(String apiKey){
        if(!isUUID(apiKey)){
            return null;
        }
        return enableVendors.get(apiKey);
    }

    private boolean isUUID(String uuidStr){
        try {
            UUID uuid = UUID.fromString(uuidStr);
            return uuid.version() == 4;
        } catch (Exception ignored){
            return false;
        }
    }
}
