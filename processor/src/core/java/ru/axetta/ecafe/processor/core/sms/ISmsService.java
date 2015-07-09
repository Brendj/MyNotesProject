/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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

        public Config(String serviceUrl, String userName, String password, String defaultSender, String serviceTimeZone)
                throws Exception {
            this.serviceUrl = serviceUrl;
            this.userName = userName;
            this.password = password;
            this.defaultSender = StringUtils.substring(defaultSender, 0, 11);
            this.serviceTimeZone = serviceTimeZone;
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

    }
    protected Config config;
    protected static Logger logger;
    static {
        try {  logger = LoggerFactory.getLogger(ISmsService.class); } catch (Throwable ignored) {}
    }

    public Boolean ignoreNotifyFlags(){
        ISmsService emp = RuntimeContext.getInstance().getSmsService();
        return emp instanceof EMPSmsServiceImpl;
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
