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
    private static final String PARAM_SCREENING = ".screening";
    private static final String PARAM_ALLOWED_CLIENT_ORGS = ".allowedClientOrgs";
    private static final String PARAM_AUTH_TYPE = ".authType";
    private static final String PARAM_ADAPTER = ".adapter";
    //private static final String PARAM_CHECK_ONLY = ".checkOnly";
    private static final String BLOCKED_TERMINALS = ".blockTerminal";
    private static final String PARAM_USERNAME= ".username";
    private static final String PARAM_PASSWORD= ".password";
    private static final String PARAM_ALLOWEDTSPIDS = ".allowedTSPIDS";


    public static final int AUTH_TYPE_NONE=0, AUTH_TYPE_SIGNATURE=1, AUTH_TYPE_CLIENT_CERT=2, AUTH_TYPE_BASIC=3;

    public static class LinkConfig {
        public String name;
        public long idOfContragent;
        public LinkedList<Long> idOfAllowedClientOrgsList;
        public String remoteAddressMask;
        public PublicKey partnerPublicKey;
        public int authType;
        public boolean checkSignature;
        public boolean screening;
        public String adapter;
        public String[] blockedTerminals;
        public String username, password;
        public ArrayList<Long> allowedTSPIds;
        //public boolean checkOnly;
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
            String screeningParam = paramBaseName + n + PARAM_SCREENING;
            String allowedTSPIDSParam = paramBaseName + n + PARAM_ALLOWEDTSPIDS;

            String idOfAllowedClientOrgsParam = paramBaseName + n + PARAM_ALLOWED_CLIENT_ORGS;
            String authTypeParam = paramBaseName + n + PARAM_AUTH_TYPE;
            //String checkOnlyParam = paramBaseName + n + PARAM_CHECK_ONLY;
            String blockedTerminalsParam = paramBaseName + n + BLOCKED_TERMINALS;
            String adapterParam = paramBaseName + n + PARAM_ADAPTER;
            String usernameParam = paramBaseName+n+ PARAM_USERNAME;
            String passwordParam = paramBaseName+n+ PARAM_PASSWORD;

            LinkConfig linkConfig = new LinkConfig();
            linkConfig.name = getRequiredParam(nameParam, properties);
            linkConfig.idOfContragent = Long.parseLong(getRequiredParam(idOfContragentParam, properties));
            linkConfig.remoteAddressMask = getRequiredParam(remoteAddressParam, properties);
            if (properties.containsKey(checkSignatureParam)) {
                linkConfig.checkSignature = Boolean.parseBoolean(getRequiredParam(checkSignatureParam, properties));
            }
            if (properties.containsKey(screeningParam)) {
                linkConfig.screening = Boolean.parseBoolean(getRequiredParam(screeningParam, properties));
            }
            //allowedTSPIDS - список контрагентов, в чей адрес разрешен платеж через терминал от агента, найденного по PID
            if (properties.containsKey(allowedTSPIDSParam)) {
                String[] v = properties.getProperty(allowedTSPIDSParam).replaceAll("\\s", "").split(",");
                linkConfig.allowedTSPIds = new ArrayList<Long>();
                for (int i=0;i<v.length;++i) {
                    if (v[i].length()>0) linkConfig.allowedTSPIds.add(Long.parseLong(v[i]));
                }
            }
            /*if (properties.containsKey(checkOnlyParam)) {
                linkConfig.checkOnly = Boolean.parseBoolean(getRequiredParam(checkOnlyParam, properties));
            }*/
            if(properties.containsKey(blockedTerminalsParam)) {
                String blockedTerminalsStr = getRequiredParam(blockedTerminalsParam, properties);
                if(blockedTerminalsStr != null && blockedTerminalsStr.trim().length() > 0) {
                    blockedTerminalsStr = blockedTerminalsStr.trim();
                    String[] list = blockedTerminalsStr.split(",");
                    if(list != null) {
                        linkConfig.blockedTerminals = list;
                    }
                } else {
                    linkConfig.blockedTerminals = new String [] {};
                }
            }

            if (properties.containsKey(authTypeParam)) {
                String authType = properties.getProperty(authTypeParam);
                if (0==authType.compareToIgnoreCase("none")) linkConfig.authType = AUTH_TYPE_NONE;
                else if (0==authType.compareToIgnoreCase("signature")) { linkConfig.authType = AUTH_TYPE_SIGNATURE; linkConfig.checkSignature = true; }
                else if (0==authType.compareToIgnoreCase("sslcert")) linkConfig.authType = AUTH_TYPE_CLIENT_CERT;
                else if (0==authType.compareToIgnoreCase("basic")) linkConfig.authType = AUTH_TYPE_BASIC;
                else throw new Exception("Invalid authType: "+authType);

                if (linkConfig.authType == AUTH_TYPE_BASIC) {
                    linkConfig.username = getRequiredParam(usernameParam, properties);
                    linkConfig.password = getRequiredParam(passwordParam, properties);
                }
            }
            if (properties.containsKey(idOfAllowedClientOrgsParam)) {
                String[] v = properties.getProperty(idOfAllowedClientOrgsParam).replaceAll("\\s", "").split(",");
                linkConfig.idOfAllowedClientOrgsList = new LinkedList<Long>();
                for (int i=0;i<v.length;++i) {
                    if (v[i].length()>0) linkConfig.idOfAllowedClientOrgsList.add(Long.parseLong(v[i]));
                }
            }
            if(properties.containsKey(adapterParam)){
                linkConfig.adapter = properties.getProperty(adapterParam);
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

    public LinkConfig getLinkConfigByAdapter(String name) {
        for (StdPayConfig.LinkConfig config: linkConfigs){
            if(config.adapter!=null && config.adapter.equals(name)){
                return config;
            }
        }
        return null;
    }

    public LinkConfig getLinkConfigByCertDN(String dn) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.authType==AUTH_TYPE_CLIENT_CERT && lc.name.equals(dn)) return lc;
        }
        return null;
    }

    public LinkConfig getLinkConfigWithAuthTypeBasicMatching(String userName, String password) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.authType==AUTH_TYPE_BASIC && (lc.username.equals(userName) && lc.password.equals(password))) {
                return lc;
            }
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

