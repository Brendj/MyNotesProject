/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.CurrentPositionsManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.sms.SendResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedList;

@Component
@Scope("singleton")
public class SMSService {
    Logger logger = LoggerFactory.getLogger(MaintananceService.class);
    @PersistenceContext
    EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    RuntimeContext runtimeContext;

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
            RuntimeContext.getFinancialOpsManager().createClientSmsCharge(client, sendResponse.getMessageId(), phoneNumber,
                                messageType, text, new Date());
            return true;
        }
        return false;
    }


}
