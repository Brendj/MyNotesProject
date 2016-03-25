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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Component
@Scope("singleton")
public class SMSService {

    private static Logger logger = LoggerFactory.getLogger(SMSService.class);
    private static final ThreadLocal<ClientSms> createdClientSms = new ThreadLocal<ClientSms>();


    @Resource(name = "smsSendingTaskExecutor")
    protected TaskExecutor taskExecutor;

    @PersistenceContext(unitName = "processorPU")
    private javax.persistence.EntityManager em;

    @Autowired
    @Qualifier(value = "txManager")
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    public SMSService() {
    }

    public void logQueue() {
        try {
            int activeCount = ((ThreadPoolTaskExecutor)taskExecutor).getActiveCount();
            int size = ((ThreadPoolTaskExecutor)taskExecutor).getThreadPoolExecutor().getQueue().remainingCapacity();
            int queue = ((ThreadPoolTaskExecutor)taskExecutor).getThreadPoolExecutor().getQueue().size();
            logger.info(String.format("Размеры очереди smsSendingTaskExecutor: выполняется потоков - %s, "+
                    "доступно в очереди - %s, размер очереди - %s", activeCount, size, queue));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
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

    /*@Async
    public void sendSMSAsync(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values) throws Exception {
        //RuntimeContext.getAppContext().getBean(SMSService.class).sendSMS(idOfClient, messageType, messageTargetId, textObject, values);
    }

    public boolean sendSMS(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values) throws Exception {
        //  pervious sendSMS located in SendSmsThreadWrapper.sendSMS
    }
    */

    public boolean sendSMSAsync(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values, Date eventTime) {
        RunnableSendSmsThreadWrapper wrapper = new RunnableSendSmsThreadWrapper
                (transactionManager, em, idOfClient, messageType, messageTargetId, textObject, values, eventTime);
        taskExecutor.execute(wrapper);
        return true;
    }

    public boolean sendSMS(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values, Date eventTime) throws Exception {
        SendSmsThreadWrapper wrapper = new SendSmsThreadWrapper(transactionManager, em);
        return wrapper.sendSMS(idOfClient, messageType, messageTargetId, textObject, values, eventTime);
    }

    public static final class RunnableSendSmsThreadWrapper extends SendSmsThreadWrapper implements Runnable {
        protected long idOfClient;
        protected int messageType;
        protected Long messageTargetId;
        protected Object textObject;
        protected String[] values;
        protected boolean result;
        protected Date eventTime;

        public RunnableSendSmsThreadWrapper() {
        }

        public RunnableSendSmsThreadWrapper(org.springframework.transaction.PlatformTransactionManager transactionManager, javax.persistence.EntityManager em,
                                            long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values, Date eventTime) {
            super(transactionManager, em);

            this.idOfClient = idOfClient;
            this.messageType = messageType;
            this.messageTargetId = messageTargetId;
            this.textObject = textObject;
            this.values = values;
            this.result = false;
            this.eventTime = eventTime;
        }

        @Override
        public void run() {
            try {
                result = sendSMS(this.idOfClient, this.messageType, this.messageTargetId, this.textObject, this.values, this.eventTime);
            } catch (Exception e) {
                logger.error("Failed to send SMS", e);
                result = false;
            }
        }
    }

    public static class SendSmsThreadWrapper {
        protected org.springframework.transaction.PlatformTransactionManager transactionManager;
        protected javax.persistence.EntityManager em;

        public SendSmsThreadWrapper() {
        }

        public SendSmsThreadWrapper(org.springframework.transaction.PlatformTransactionManager transactionManager, javax.persistence.EntityManager em) {
            this.transactionManager = transactionManager;
            this.em = em;
        }

        public boolean sendSMS(long idOfClient, int messageType, Long messageTargetId, Object textObject, String[] values, Date eventTime) throws Exception {
            if(transactionManager == null) {
                throw new IllegalStateException("Transaction manager is null");
            }
            if(em == null) {
                throw new IllegalStateException("Entity manager is null");
            }
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            Client client = null; String phoneNumber = null, sender = null;

            ISmsService smsService = RuntimeContext.getInstance().getSmsService();

            try {
                client = em.find(Client.class, idOfClient);
                if (client == null) {
                    throw new Exception ("Client doesn't exist");
                }

                if (!smsService.ignoreNotifyFlags()){
                    if(!client.isNotifyViaSMS() && messageType!=ClientSms.TYPE_LINKING_TOKEN) return false;
                }
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

            boolean result = false;

            if (sendResponse != null && messageType != ClientSms.TYPE_SUMMARY_DAILY_NOTIFICATION
                    && messageType != ClientSms.TYPE_SUMMARY_WEEKLY_NOTIFICATION) {
                Long idOfSourceOrg = EventNotificationService.getSourceOrgIdFromValues(values);
                result = registerClientSMSCharge(sendResponse.isSuccess(), client, sendResponse.getMessageId(),
                        phoneNumber, messageTargetId, messageType, textMessage, eventTime, idOfSourceOrg);
            }

            //  Добавление в список не отправленных sms
            //if(sendResponse != null && !sendResponse.isSuccess()) {
            boolean failureTestingMode = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SMS_FAILURE_TESTING_MODE);
            result = result && !failureTestingMode;
            if(!result) {
                String serviceName = RuntimeContext.getInstance().getConfigProperties().
                        getProperty(RuntimeContext.SMS_SERVICE_PARAM_BASE + ".type", "atompark");
                SMSResendingService.getInstance().addResending(sendResponse.getMessageId(), client,
                        phoneNumber, serviceName, messageTargetId, messageType, textObject, values, eventTime);
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

        protected boolean registerClientSMSCharge(boolean success, Client client, String messageId, String phoneNumber, Long messageTargetId,
                int messageType, String text, Date eventTime, Long idOfSourceOrg) throws Exception {
            ClientSms clientSms = null;
            boolean result = false;
            if (success) {
                boolean delivered =  RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl;
                clientSms = RuntimeContext.getFinancialOpsManager()
                        .createClientSmsCharge(client, messageId, phoneNumber, messageTargetId, messageType, text,
                                new Date(), eventTime, delivered, idOfSourceOrg);
                createdClientSms.set(clientSms);
                result = true;
            }
            return result;
        }
    }

    public static ClientSms getCreatedClientSms() {
        return createdClientSms.get();
    }

    public Boolean isEmailSentByPlatform() {
        ISmsService s = RuntimeContext.getInstance().getSmsService();
        return s.emailDisabled();
    }

    public Boolean ignoreNotifyFlags() {
        ISmsService s = RuntimeContext.getInstance().getSmsService();
        return s.ignoreNotifyFlags();
    }
}
