/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.smpp;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.smpp.client.Client;
import ru.axetta.smpp.client.SendResult;
import ru.axetta.smpp.client.USSD_MAPPINGS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class SMPPClient extends ISmsService {

    private String smscIPAddress;
    private int smscPort;
    private String systemId;
    private String password;
    private String systemType;
    private String serviceType;
    private String sourceAddress;
    private Boolean serviceStatus;
    private Client client;
    private final static Logger logger = LoggerFactory.getLogger(SMPPClient.class);
    private final SMPPListener smppListener;


    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, String text) throws Exception {
        logger.info("start sending: " + text);
        long destination = Long.parseLong(phoneNumber);
        SendResult sr = client.send(text, destination);
        if (sr.err != 0) {
            throw new Exception("sending error");
        } else {
            logger.info("message sent="+sr.id);
        }
        return new SendResponse(sr.err, sr.err != 0?null:"message sent="+sr.id, sr.id);
    }

    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        if(client.getStatus()==Client.STATE_ONLINE){
            Boolean isDeliveryReport = smppListener.getMessageStack().get(messageId);
            if(isDeliveryReport==null) isDeliveryReport=false;
            return new DeliveryResponse(isDeliveryReport?DeliveryResponse.DELIVERED:DeliveryResponse.SENT, null, null);
        } else {
            return new DeliveryResponse(DeliveryResponse.UNKNOWN, null, null);
        }
    }

    public SMPPClient(Config config, Properties properties, String PATH) {
        super(config);
        PATH = ".smpp.";
        smppListener = new SMPPListener();
        client = new Client(smppListener, "CMD Processor");
        client.ussd_mapping = USSD_MAPPINGS.NOWSMS_SCHEME_ITS;
        smscIPAddress = properties.getProperty(PATH+"ip-address", "127.0.0.1");
        smscPort = Integer.parseInt(properties.getProperty(PATH+"port", "9500"));
        systemId = properties.getProperty(PATH+"system-id", "user1");
        password = properties.getProperty(PATH+"password", "1234");
        serviceType = properties.getProperty(PATH+"service-type", "");
        systemType = properties.getProperty(PATH+"system-type", "test");
        sourceAddress = properties.getProperty(PATH+"source-address", "8080");
        serviceStatus = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SMPP_CLIENT_STATUS);
        startService();
    }

    public Status startService(){
        if(serviceStatus){
            int err = client.start(sourceAddress, smscIPAddress, smscPort, systemId, systemType, serviceType, password);
            if (err != 0) {
                logger.error("connecting error");
                return new Status(false, "connecting error");
            } else {
                logger.info("connected");
                return new Status(true, "connected");
            }
        } else {
            logger.error("connecting error");
            return new Status(false, "connecting error");
        }
    }

    public Status stopService(){
        if(serviceStatus){
            if (client.getStatus() != Client.STATE_OFFLINE) {
                client.stop();
                return new Status(false,"Client stopped");
            } else {
                logger.info("Client already stopped");
                return new Status(false, "Client already stopped");
            }
        } else {
            logger.error("connecting error");
            return new Status(false, "connecting error");
        }
    }

    public Boolean getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Boolean serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public static class Status{
        private Boolean status;
        private String message;

        public Status(Boolean status, String message) {
            this.status = status;
            this.message = message;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
