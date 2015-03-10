/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.core.sms.SendResponse;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;
import ru.axetta.ecafe.processor.core.sms.smpp.SMPPClient;

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


    @PersistenceContext(unitName = "processorPU")
    private javax.persistence.EntityManager em;

    @Autowired
    @Qualifier(value = "txManager")
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    public SMSService() {
    }

    /*@Async
    public void sendSMSAsync(long idOfClient, EMPEventType empEvent) throws Exception {
        RuntimeContext.getAppContext().getBean(SMSService.class).sendSMS(idOfClient, empEvent);
    }

    public boolean sendSMS(long idOfClient, EMPEventType empEvent) throws Exception {
        Client client = DAOService.getInstance().findClientById(idOfClient);
        boolean sending = RuntimeContext.getAppContext().getBean(EMPProcessor.class).sendEvent(client, empEvent);
        boolean result = registerClientSMSCharge(sending, client,
                                                 "", client.getMobile(), empEvent.getType(), empEvent.buildText());
        return result;
    }*/

    @Async
    public void sendSMSAsync(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values) throws Exception {
        RuntimeContext.getAppContext().getBean(SMSService.class).sendSMS(idOfClient, messageType, messageTargetId, textObject, values);
    }

    public boolean sendSMS(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values) throws Exception {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Client client = null; String phoneNumber, sender;
        try {
            client = em.find(Client.class, idOfClient);
            if (client == null) {
                throw new Exception ("Client doesn't exist");
            }
            if(!client.isNotifyViaSMS() && messageType!=ClientSms.TYPE_LINKING_TOKEN) return false;
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
        ISmsService smsService = RuntimeContext.getInstance().getSmsService();
        logger.info("sending SMS, sender: {}, phoneNumber: {}, text: {}", new Object[]{sender, phoneNumber,
                                                                                       textObject.toString()});
        String textMessage = null;
        if(smsService instanceof SMPPClient){
            try {
                sendResponse = smsService.sendTextMessage(sender, phoneNumber, textObject);
                logger.info(String.format("sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s, RC: %s, error: %s",
                        sendResponse.getMessageId(), sender, phoneNumber, textObject.toString(), sendResponse.getStatusCode(), sendResponse.getError()));
                textMessage = textObject.toString();
            } catch (Exception e) {
                logger.warn("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}, exception: {}",
                        new Object[]{sender, phoneNumber, textObject.toString(), e});
            }
        } else {
            for (int i = 0; i < 3; i++) {
                try {
                    if(smsService instanceof EMPSmsServiceImpl) {
                        sendResponse = ((EMPSmsServiceImpl) smsService).sendTextMessage(sender, client, textObject);
                        boolean isSuccess = sendResponse.isSuccess();
                        if(sendResponse != null && !isSuccess) {
                            String msg = ((EMPEventType) textObject).buildText();
                            msg = String.format("E:[%s] %s", sendResponse.getStatusCode(), msg);
                            regisgterClientSMSFailedCharge(client, sendResponse.getMessageId(),
                                                           phoneNumber, messageTargetId, messageType, msg);
                            return false;
                        }
                        //textObject = ((EMPEventType) textObject).buildText();
                        textMessage = ((EMPEventType) textObject).buildText();
                    } else {
                        sendResponse = smsService.sendTextMessage(sender, phoneNumber, textObject);
                        textMessage = textObject.toString();
                    }
                    logger.info(String.format("sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s, RC: %s, error: %s",
                            sendResponse.getMessageId(), sender, phoneNumber, textMessage, sendResponse.getStatusCode(), sendResponse.getError()));
                    if (sendResponse.isSuccess()) {
                        break;
                    }
                } catch (Exception e) {
                    /*logger.warn("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}, exception: {}",
                            new Object[]{sender, phoneNumber, textObject.toString(), e});*/
                        logger.error("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}",
                            new Object[]{sender, phoneNumber, textObject.toString()}, e);
                }
            }
        }

        boolean result = registerClientSMSCharge(null != sendResponse && sendResponse.isSuccess(), client,
                                                 sendResponse.getMessageId(), phoneNumber, messageTargetId, messageType,
                                                 textMessage);


        //  Добавление в список не отправленных sms
        //if(sendResponse != null && !sendResponse.isSuccess()) {
        boolean failureTestingMode = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SMS_FAILURE_TESTING_MODE);
        result = result && !failureTestingMode;
        if(!result) {
            String serviceName = RuntimeContext.getInstance().getConfigProperties().
                    getProperty(RuntimeContext.SMS_SERVICE_PARAM_BASE + ".type", "atompark");
            SMSResendingService.getInstance().addResending(sendResponse.getMessageId(), client,
                    phoneNumber, serviceName, messageTargetId, messageType, textObject, values);
        }

        return result;
    }

    protected boolean regisgterClientSMSFailedCharge(Client client, String messageId,
                                                     String phoneNumber, Long messageTargetId, int messageType, String text) throws Exception {
        if(text == null || StringUtils.isBlank(text)) {
            return false;
        }
        ClientSms clientSms = RuntimeContext.getFinancialOpsManager()
                .createClientFailedSmsCharge(client, messageId, phoneNumber, messageTargetId, messageType, text,
                        new Date());
        createdClientSms.set(clientSms);
        return true;
    }

    protected boolean registerClientSMSCharge(boolean success, Client client, String messageId,
                                              String phoneNumber, Long messageTargetId, int messageType, String text) throws Exception {
        ClientSms clientSms = null;
        boolean result = false;
        if (success) {
            boolean delivered =  RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl;
            clientSms = RuntimeContext.getFinancialOpsManager()
                    .createClientSmsCharge(client, messageId, phoneNumber, messageTargetId, messageType, text,
                            new Date(), delivered);
            result = true;
        }
        createdClientSms.set(clientSms);
        return result;
    }

    public static ClientSms getCreatedClientSms() {
        return createdClientSms.get();
    }
}
