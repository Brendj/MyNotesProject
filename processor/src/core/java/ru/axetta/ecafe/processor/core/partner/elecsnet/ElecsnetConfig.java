package ru.axetta.ecafe.processor.core.partner.elecsnet;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Игорь
 * Date: 18.12.2010
 * Time: 1:16:38
 * To change this template use File | Settings | File Templates.
 */
public class ElecsnetConfig {
    private static final String PARAM_BASE = ".elecsnet";
    private static final String REMOTE_ADDRESS_PARAM = PARAM_BASE + ".remoteAddress";
    private static final String ID_OF_CONGRAGENT_PARAM = PARAM_BASE + ".idOfContragent";
    private final String remoteAddressMasks;
    private final long idOfContragent;

    public ElecsnetConfig(Properties properties, String paramBaseName) throws Exception {
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
