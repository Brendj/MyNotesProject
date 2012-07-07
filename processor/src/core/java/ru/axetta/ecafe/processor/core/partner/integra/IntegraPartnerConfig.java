/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.integra;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Pattern;

public class IntegraPartnerConfig {
    private static final String PARAM_BASE = ".integra.";
    private static final String PARAM_SSL_CERT_DN =".sslCertDN";
    private static final String PARAM_REMOTE_ADDRESS = ".remoteAddress";
    private static final String PARAM_ID = ".id";
    private static final String PARAM_AUTH_TYPE = ".authType";
    private static final String PARAM_PERMISSION = ".permissionType";
    private static final String PARAM_USERNAME= ".username";
    private static final String PARAM_PASSWORD= ".password";

    public static final int AUTH_TYPE_NONE=0, AUTH_TYPE_CLIENT_CERT=2, AUTH_TYPE_BASIC =3;
    public static final int PERMISSION_TYPE_ALL=0, PERMISSION_TYPE_CLIENT_AUTH=1;

    private static final Logger logger = LoggerFactory.getLogger(IntegraPartnerConfig.class);

    public LinkConfig getLinkConfigWithAuthTypeNoneAndMatchingAddress(String clientAddress) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.authType==AUTH_TYPE_NONE && lc.matchAddress(clientAddress)) return lc;
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

    public static class LinkConfig {
        public String sslDN;
        public String id;
        public Pattern remoteAddressMask;
        public int authType;
        public int permissionType;
        public String username, password;

        public boolean matchAddress(String clientAddress) {
            return remoteAddressMask.matcher(clientAddress).matches();
        }
    }
    LinkedList<LinkConfig> linkConfigs = new LinkedList<LinkConfig>();
    ///
    public IntegraPartnerConfig(Properties properties, String paramBaseName) throws Exception {
        paramBaseName+=PARAM_BASE;
        for (int n=0;;++n) {
            String idParam = paramBaseName + n + PARAM_ID;
            if (!properties.containsKey(idParam)) break;
            String sslDN = paramBaseName+n+ PARAM_SSL_CERT_DN;
            String usernameParam = paramBaseName+n+ PARAM_USERNAME;
            String passwordParam = paramBaseName+n+ PARAM_PASSWORD;
            String remoteAddressParam = paramBaseName + n + PARAM_REMOTE_ADDRESS;
            String permissionTypeParam = paramBaseName + n + PARAM_PERMISSION;
            String authTypeParam = paramBaseName + n + PARAM_AUTH_TYPE;

            LinkConfig linkConfig = new LinkConfig();
            linkConfig.id = getRequiredParam(idParam, properties);
            linkConfig.remoteAddressMask = Pattern.compile(getRequiredParam(remoteAddressParam, properties));
            String authType = getRequiredParam(authTypeParam, properties);
            if (0==authType.compareToIgnoreCase("none")) linkConfig.authType = AUTH_TYPE_NONE;
            else if (0==authType.compareToIgnoreCase("sslcert")) linkConfig.authType = AUTH_TYPE_CLIENT_CERT;
            else if (0==authType.compareToIgnoreCase("basic")) linkConfig.authType = AUTH_TYPE_BASIC;
            else throw new Exception("Invalid authType: "+authType);
            
            if (linkConfig.authType==AUTH_TYPE_CLIENT_CERT) {
                linkConfig.sslDN = getRequiredParam(sslDN, properties);
            }
            if (linkConfig.authType== AUTH_TYPE_BASIC) {
                linkConfig.username = getRequiredParam(usernameParam, properties);
                linkConfig.password = getRequiredParam(passwordParam, properties);
            }

            String permissionType = getRequiredParam(permissionTypeParam, properties);
            if (0==permissionType.compareToIgnoreCase("all")) linkConfig.permissionType = PERMISSION_TYPE_ALL;
            else if (0==permissionType.compareToIgnoreCase("client_auth")) linkConfig.permissionType = PERMISSION_TYPE_CLIENT_AUTH;
            else throw new Exception("Invalid permissionType: "+permissionType);
            
            linkConfigs.add(linkConfig);

            logger.info("Registered integration interface partner link: "+linkConfig.id);
        }
    }

    public LinkConfig getLinkConfig(String id) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.id.equals(id)) return lc;
        }
        return null;
    }

    public LinkConfig getLinkConfigByCertDN(String dn) {
        for (LinkConfig lc : linkConfigs) {
            if (lc.sslDN!=null && lc.sslDN.equals(dn)) return lc;
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
