/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.smpp;


import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.smpp.client.Client;
import ru.axetta.smpp.client.SendResult;
import ru.axetta.smpp.client.USSD_MAPPINGS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class SMPPClient extends ISmsService {

    private String sourceAddressNpi;
    private String sourceAddressTon;
    private String smscIPAddress;
    private int smscPort;
    private String systemId;
    private String password;
    private String systemType;
    private String serviceType;
    private String sourceAddress;
    //private Boolean serviceStatus;
    private Client client;
    private final static Logger LOGGER = LoggerFactory.getLogger(SMPPClient.class);
    private final SMPPListener smppListener;


    @Override
    public SendResponse sendTextMessage(String sender, String phoneNumber, Object textObject) throws Exception {
        if(!(textObject instanceof String)) {
            throw new Exception("Text argument must be a string");
        }
        String text = (String) textObject;
        LOGGER.info("start sending: " + text);
        long destination = Long.parseLong(phoneNumber);
        SendResult sr;
        if(client.getStatus()==  Client.STATE_ONLINE){
            sr = client.send(text, destination);
        } else {
            int count =0;
            int err = -1;
            while (count<5){
                //err = client.start(sourceAddress, smscIPAddress, smscPort, systemId, systemType, serviceType, password, sourceAddressTon, sourceAddressNpi);
                if(err==0) break;
                count++;
            }
            if(err==0){
                sr = client.send(text, destination);
            } else {
                sr = new SendResult(-3,"SMPP Client does not connect");
            }
        }
        if (sr.err != 0) {
            throw new Exception("sending error");
        } else {
            LOGGER.info("message sent=" + sr.id);
        }
        return new SendResponse(translateSendStatus(sr.err), sr.err != 0?null:"message sent="+sr.id, sr.id);
    }

    @Override
    public DeliveryResponse getDeliveryStatus(String messageId) throws Exception {
        return new DeliveryResponse(DeliveryResponse.DELIVERED, null, null);
        //if(client.getStatus()==Client.STATE_ONLINE){
        //    //Boolean isDeliveryReport = smppListener.getMessageStack().get(messageId);
        //    //if(isDeliveryReport==null) isDeliveryReport=false;
        //    //return new DeliveryResponse(isDeliveryReport?DeliveryResponse.DELIVERED:DeliveryResponse.SENT, null, null);
        //} else {
        //    return new DeliveryResponse(DeliveryResponse.UNKNOWN, null, null);
        //}
    }

    public SMPPClient(Config config, Properties properties, String PATH) throws Exception{
        super(config);
        PATH = PATH+".smpp.";
        smppListener = new SMPPListener();
        client = new Client(smppListener, "CMD Processor");
        client.ussd_mapping = USSD_MAPPINGS.NOWSMS_SCHEME_ITS;
        smscIPAddress = properties.getProperty(PATH + "ip-address", "127.0.0.1");
        smscPort = Integer.parseInt(properties.getProperty(PATH + "port", "9500"));
        systemId = properties.getProperty(PATH+"system-id", "user");
        password = properties.getProperty(PATH+"password", "user");
        serviceType = properties.getProperty(PATH+"service-type", "");
        systemType = properties.getProperty(PATH+"system-type", "test");
        sourceAddress = properties.getProperty(PATH + "source-address", "5223");
        sourceAddressTon = properties.getProperty(PATH + "source-address-ton", "5");
        sourceAddressNpi = properties.getProperty(PATH + "source-address-npi", "0");

        //serviceStatus =  properties.getProperty(PATH+"service-status", "0").equals("1");
        //serviceStatus = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SMPP_CLIENT_STATUS);
        this.config = new Config(smscIPAddress, systemId, password, "","");
        startService();
    }

    public void startService(){
        int err = client.start(sourceAddress, smscIPAddress, smscPort, systemId, systemType, serviceType, password/*, sourceAddressTon, sourceAddressNpi*/);
        if (err != 0) {
            LOGGER.error("SMPP Client connecting error " + err);
        } else {
            LOGGER.info("SMPP Client connected");
        }
        //if(serviceStatus){
        //} else {
        //    LOGGER.info("SMPP Client does not connect");
        //}
    }

    @PreDestroy
    public void stopService(){
        if (client.getStatus() != Client.STATE_OFFLINE) {
            client.stop();
        } else {
            LOGGER.info("Client already stopped");
        }
        //if(serviceStatus){
        //} else {
        //    LOGGER.error("connecting error");
        //}
    }

    /**
     * По ответу сервера делает вывод о статусе отправки
     *
     * @param sendStatus статус из ответа шлюза
     * @return статус отправки
     */
    private int translateSendStatus(int sendStatus) {
        if (sendStatus == 0) {
            return SendResponse.MIN_SUCCESS_STATUS;
        } else {
            return SendResponse.COMMON_FAILURE;
        }


    }

}
