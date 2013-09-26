/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.sms.SendResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("singleton")
public class SMSService {

    private static Logger logger = LoggerFactory.getLogger(SMSService.class);
    private static final ThreadLocal<ClientSms> createdClientSms = new ThreadLocal<ClientSms>();

    @Autowired
    private RuntimeContext runtimeContext;

    public SMSService() {
    }


    @Async
    public void sendSMSAsync(Client client, int messageType, String text) throws Exception {
        sendSMS(client, messageType, text);
    }

    public synchronized boolean sendSMS(Client client, int messageType, String text) throws Exception {
        String phoneNumber, sender;
        if (client == null) {
            throw new Exception("Client doesn't exist");
        }
        if (!client.isNotifyViaSMS()) {
            return false;
        }
        phoneNumber = client.getMobile();
        if (StringUtils.isEmpty(phoneNumber)) {
            return false;
        }
        phoneNumber = PhoneNumberCanonicalizator.canonicalize(phoneNumber);
        if (StringUtils.length(phoneNumber) != 11) {
            return false;
        }
        sender = StringUtils.substring(StringUtils.defaultString(client.getOrg().getSmsSender()), 0, 11);

        SendResponse sendResponse = null;
        ISmsService smsService = runtimeContext.getSmsService();
        try {
            logger.info(String.format("sending SMS, sender: %s, phoneNumber: %s, text: %s",
                    sender, phoneNumber, text));
            sendResponse = smsService.sendTextMessage(sender, phoneNumber, text);
            logger.info(String.format("sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s, RC: %s, error: %s",
                    sendResponse.getMessageId(), sender, phoneNumber, text, sendResponse.getStatusCode(), sendResponse.getError()));
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format(
                        "Failed to send SMS, sender: %s, phoneNumber: %s, text: %s",
                        sender, phoneNumber, text), e);
            }
        }
        if (null != sendResponse && sendResponse.isSuccess()) {
            ClientSms clientSms = RuntimeContext.getFinancialOpsManager()
                    .createClientSmsCharge(client, sendResponse.getMessageId(), phoneNumber, messageType, text,
                            new Date());
            createdClientSms.set(clientSms);
            return true;
        }
        return false;
    }

    public static ClientSms getCreatedClientSms() {
        return createdClientSms.get();
    }
}
