/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 04.10.2010
 * Time: 15:50:30
 * To change this template use File | Settings | File Templates.
 */
public class PayPointConfig {

    private static final String PARAM_BASE = ".paypoint";
    private static final String REMOTE_ADDRESS_PARAM = PARAM_BASE + ".remoteAddress";
    private static final String ID_OF_CONGRAGENT_PARAM = PARAM_BASE + ".idOfContragent";
    private final List<String> remoteAddressMasks;
    private final long idOfContragent;

    public PayPointConfig(Properties properties, String paramBaseName) throws Exception {
        String remoteAddressParam = paramBaseName + REMOTE_ADDRESS_PARAM;
        String idOfCongragentParam = paramBaseName + ID_OF_CONGRAGENT_PARAM;

        this.remoteAddressMasks = new ArrayList<String>(1);
        this.remoteAddressMasks.add(getRequiredParam(remoteAddressParam, properties));
        this.idOfContragent = Long.parseLong(getRequiredParam(idOfCongragentParam, properties));
    }

    public List<String> getRemoteAddressMasks() {
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
