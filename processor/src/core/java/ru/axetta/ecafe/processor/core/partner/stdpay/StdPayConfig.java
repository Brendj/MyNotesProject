/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.stdpay;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class StdPayConfig {
    private static final Logger logger = LoggerFactory.getLogger(StdPayConfig.class);

    private static final String PARAM_BASE = ".stdpay.";
    private static final String PARAM_NAME=".name";
    private static final String PARAM_REMOTE_ADDRESS = ".remoteAddress";
    private static final String PARAM_ID_OF_CONGRAGENT = ".idOfContragent";
    private static final String PARAM_PARTNER_PUBLIC_KEY = ".partnerPubKey";
    private static final String PARAM_OUR_PRIVATE_KEY = ".ourPrivKey";
    private static final String PARAM_CHECK_SIGN = ".checkSignature";
    private static final String PARAM_ALLOWED_CLIENT_ORGS = ".allowedClientOrgs";
    private static final String ENABLE_SOAP = ".enableSoap";
    
    public static class LinkConfig {
        public String name;
        public long idOfContragent;
        public LinkedList<Long> idOfAllowedClientOrgsList;
        public String remoteAddressMask;
        public PublicKey partnerPublicKey;
        public boolean checkSignature;
        public boolean soapEnabled;
    }
    LinkedList<LinkConfig> linkConfigs = new LinkedList<LinkConfig>();


    public StdPayConfig(Properties properties, String paramBaseName) throws Exception {
        paramBaseName+=PARAM_BASE;
        for (int n=0;;++n) {
            String nameParam = paramBaseName+n+PARAM_NAME;
            if (!properties.containsKey(nameParam)) break;
            String remoteAddressParam = paramBaseName + n + PARAM_REMOTE_ADDRESS;
            String idOfContragentParam = paramBaseName + n + PARAM_ID_OF_CONGRAGENT;
            String checkSignatureParam = paramBaseName + n + PARAM_CHECK_SIGN;
            String idOfAllowedClientOrgsParam = paramBaseName + n + PARAM_ALLOWED_CLIENT_ORGS;
            String soapEnabledParam = paramBaseName + n + ENABLE_SOAP;
            
            LinkConfig linkConfig = new LinkConfig();
            linkConfig.name = getRequiredParam(nameParam, properties);
            linkConfig.idOfContragent = Long.parseLong(getRequiredParam(idOfContragentParam, properties));
            linkConfig.remoteAddressMask = getRequiredParam(remoteAddressParam, properties);
            linkConfig.checkSignature = Boolean.parseBoolean(getRequiredParam(checkSignatureParam, properties));
            linkConfig.soapEnabled = Boolean.parseBoolean(getRequiredParam(soapEnabledParam, properties));
            if (properties.containsKey(idOfAllowedClientOrgsParam)) {
                String[] v = properties.getProperty(idOfAllowedClientOrgsParam).replaceAll("\\s", "").split(",");
                linkConfig.idOfAllowedClientOrgsList = new LinkedList<Long>();
                for (int i=0;i<v.length;++i) {
                    if (v[i].length()>0) linkConfig.idOfAllowedClientOrgsList.add(Long.parseLong(v[i]));
                }
            }
            linkConfigs.add(linkConfig);
            logger.info("Registered standart payment interface partner link: "+linkConfig.name);
        }
    }

    public LinkConfig getLinkConfig(String name) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.name.equals(name)) return lc;
        }
        return null;
    }

    private static String getRequiredParam(String param, Properties properties) throws Exception {
        String value = properties.getProperty(param);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" not found", param));
        }
        return value;
    }
}

