/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public abstract class ISmsService {
    public static class Config {

        private final String serviceUrl;
        private final String userName;
        private final String password;
        private final String defaultSender;
        private final String serviceTimeZone;
        private final String token;
        private final String systemId;
        private final String catalogName;
        private final String subscriptionServiceUrl;
        private final String storageServiceUrl;

        public Config(String serviceUrl, String userName, String password, String defaultSender, String serviceTimeZone)
                throws Exception {
            this.serviceUrl = serviceUrl;
            this.userName = userName;
            this.password = password;
            this.defaultSender = StringUtils.substring(defaultSender, 0, 11);
            this.serviceTimeZone = serviceTimeZone;
            this.subscriptionServiceUrl = "";
            this.storageServiceUrl = "";
            this.token = "";
            this.systemId = "";
            this.catalogName = "";
        }

        public Config(String serviceUrl, String userName, String password, String defaultSender, String serviceTimeZone,
                String subscriptionServiceUrl, String storageServiceUrl, String token,String systemId,
                String catalogName)
        throws Exception {
            this.serviceUrl = serviceUrl;
            this.userName = userName;
            this.password = password;
            this.defaultSender = StringUtils.substring(defaultSender, 0, 11);
            this.serviceTimeZone = serviceTimeZone;
            this.subscriptionServiceUrl = subscriptionServiceUrl;
            this.storageServiceUrl = storageServiceUrl;
            this.token = token;
            this.systemId = systemId;
            this.catalogName = catalogName;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getDefaultSender() {
            return defaultSender;
        }

        public String getServiceTimeZone() {
            return serviceTimeZone;
        }

        public String getSubscriptionServiceUrl() {
            return subscriptionServiceUrl;
        }

        public String getStorageServiceUrl() {
            return storageServiceUrl;
        }

        public String getToken() {
            return token;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getCatalogName() {
            return catalogName;
        }
    }
    protected Config config;
    protected static Logger logger;
    static {
        try {  logger = LoggerFactory.getLogger(ISmsService.class); } catch (Throwable ignored) {}
    }

    public ISmsService() {
    }
    public ISmsService(Config config) {
        this.config = config;
    }
    public abstract SendResponse sendTextMessage(String sender, String phoneNumber, Object textObject)
            throws Exception;
    public abstract DeliveryResponse getDeliveryStatus(String messageId) throws Exception;
}
