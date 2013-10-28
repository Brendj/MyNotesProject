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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.PersistenceContext;
import java.util.Date;

@Component
@Scope("singleton")
public class SMSService {

    private static Logger logger = LoggerFactory.getLogger(SMSService.class);
    private static final ThreadLocal<ClientSms> createdClientSms = new ThreadLocal<ClientSms>();

    @Autowired
    private RuntimeContext runtimeContext;

    @PersistenceContext(unitName = "processorPU")
    private javax.persistence.EntityManager em;

    @Autowired
    @Qualifier(value = "txManager")
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    public SMSService() {
    }

    @Async
    public void sendSMSAsync(long idOfClient, int messageType, String text) throws Exception {
        RuntimeContext.getAppContext().getBean(SMSService.class).sendSMS(idOfClient, messageType, text);
    }

    public boolean sendSMS(long idOfClient, int messageType, String text) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Client client = null; String phoneNumber, sender;
        try {
            client = em.find(Client.class, idOfClient);
            if (client == null) {
                throw new Exception ("Client doesn't exist");
            }
            if(!client.isNotifyViaSMS()) return false;
            phoneNumber = client.getMobile();
            if (!StringUtils.isNotEmpty(phoneNumber)) return false;
            phoneNumber = PhoneNumberCanonicalizator.canonicalize(phoneNumber);
            if (StringUtils.length(phoneNumber) != 11) return false;
            sender = StringUtils.substring(StringUtils.defaultString(client.getOrg().getSmsSender()), 0, 11);
            transactionManager.commit(status);
            status = null;
        }
        finally {
            if (status!=null) transactionManager.rollback(status);
        }

        SendResponse sendResponse = null;
        ISmsService smsService = runtimeContext.getSmsService();
        logger.info("sending SMS, sender: {}, phoneNumber: {}, text: {}", new Object[]{sender, phoneNumber, text});
        for (int i = 0; i < 3; i++) {
            try {
                sendResponse = smsService.sendTextMessage(sender, phoneNumber, text);
                logger.info(String.format("sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s, RC: %s, error: %s",
                    sendResponse.getMessageId(), sender, phoneNumber, text, sendResponse.getStatusCode(), sendResponse.getError()));
                if (sendResponse.isSuccess()) {
                    break;
                }
            } catch (Exception e) {
                logger.warn("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}, exception: {}",
                        new Object[]{sender, phoneNumber, text, e});
            }
        }
        ClientSms clientSms = null;
        boolean result = false;
        if (null != sendResponse && sendResponse.isSuccess()) {
            clientSms = RuntimeContext.getFinancialOpsManager()
                    .createClientSmsCharge(client, sendResponse.getMessageId(), phoneNumber, messageType, text,
                            new Date());
            result = true;
        }
        createdClientSms.set(clientSms);
        return result;
    }

    public static ClientSms getCreatedClientSms() {
        return createdClientSms.get();
    }
}
