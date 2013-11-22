package ru.axetta.ecafe.processor.core.partner.sbrt;

import org.apache.commons.lang.StringUtils;

import java.util.Properties;

public class SBRTConfig {
    private static final String PARAM_BASE = ".sbrt";
    private static final String REMOTE_ADDRESS_PARAM = PARAM_BASE + ".remoteAddress";
    private static final String ID_OF_CONGRAGENT_PARAM = PARAM_BASE + ".idOfContragent";
    private final String remoteAddressMasks;
    private final long idOfContragent;

    public SBRTConfig(Properties properties, String paramBaseName) throws Exception {
        String remoteAddressParam = paramBaseName + REMOTE_ADDRESS_PARAM;
        String idOfCongragentParam = paramBaseName + ID_OF_CONGRAGENT_PARAM;

        this.remoteAddressMasks = getRequiredParam(remoteAddressParam, properties);
        this.idOfContragent = Long.parseLong(getRequiredParam(idOfCongragentParam, properties));
    }

    public String getRemoteAddressMasks() {
        return remoteAddressMasks;
    }

    public long getIdOfContragent() {
        return idOfContragent;
    }

    private static String getRequiredParam(String param, Properties properties) throws Exception {
        String value = properties.getProperty(param);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" not found", param));
        }
        return value;
    }
}
