/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.smpp;

import ru.oksoft.myOTA.Security0348;

import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.smpp.client.Client;
import ru.axetta.smpp.client.SendResult;
import ru.axetta.smpp.client.Ticket;
import ru.axetta.smpp.client.USSD_MAPPINGS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class SMPPClientRun extends ISmsService {

    private String smscIPAddress;
    private int smscPort;
    private String systemId;
    private String password;
    private String systemType;
    private String serviceType;
    private String sourceAddress;
    private Client client;
    private final static Logger logger = LoggerFactory.getLogger(SMPPClientRun.class);

    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, String text) throws Exception {
        String s = new String(new byte[10],"x-hex");
        int err = client.start(sourceAddress, smscIPAddress, smscPort, systemId, systemType, serviceType, password);
        if (err != 0) {
            logger.error("connecting error");
        } else {
            logger.info("connected");
        }
        logger.info("start sending: " + text);
        long destination = Long.parseLong(phoneNumber);
        SendResult sr = client.send(text, destination);
        if (sr.err != 0) {
            logger.error("sending error");
        } else {
            logger.info("message sent="+sr.id);
        }
        if (client.getStatus() != Client.STATE_OFFLINE) {
            client.stop();
        } else {
            logger.info("Client already stopped");
        }
        return null;
    }

    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
         return null;
    }

    public SMPPClientRun(Config config, Properties properties, String PATH) {
        super(config);
        PATH = ".smpp.";
        Client.setLowLevelDebugToFile("C:\\path","smpp.log");
        client = new Client(new SMPPListener(), "CMD Processor");
        client.ussd_mapping = USSD_MAPPINGS.NOWSMS_SCHEME_ITS;
        smscIPAddress = properties.getProperty(PATH+"ip-address", "127.0.0.1");
        smscPort = Integer.parseInt(properties.getProperty(PATH+"port", "9500"));
        systemId = properties.getProperty(PATH+"system-id", "user1");
        password = properties.getProperty(PATH+"password", "1234");
        serviceType = properties.getProperty(PATH+"service-type", "");
        systemType = properties.getProperty(PATH+"system-type", "test");
        sourceAddress = properties.getProperty(PATH+"source-address", "8080");
    }
}
