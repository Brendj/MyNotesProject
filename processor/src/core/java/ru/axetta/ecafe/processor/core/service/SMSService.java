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
import java.util.Map;

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
            QueueState queueState = getQueueState();
            logger.info(String.format("Размеры очереди smsSendingTaskExecutor: выполняется потоков - %s, "
                            + "доступно в очереди - %s, размер очереди - %s", queueState.getActiveCount(), queueState.getSize(),
                    queueState.getQueue()));
            ThreadPoolTaskExecutor ex = (ThreadPoolTaskExecutor) RuntimeContext.getAppContext()
                    .getBean("executorWithPoolSizeRange");
            if (ex != null) {
                logger.info(String.format("Размеры очереди asyncThreadPoolTaskExecutor: выполняется потоков - %s, "
                                + "доступно в очереди - %s, размер очереди - %s, выполнено задач - %s", ex.getActiveCount(),
                        ex.getThreadPoolExecutor().getQueue().remainingCapacity(),
                        ex.getThreadPoolExecutor().getQueue().size(),
                        ex.getThreadPoolExecutor().getCompletedTaskCount()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean sendSMSAsync(Client client, int messageType, Long messageTargetId, Object textObject,
            String[] values, Date eventTime) {
        RunnableSendSmsThreadWrapper wrapper = new RunnableSendSmsThreadWrapper(transactionManager, em, client,
                messageType, messageTargetId, textObject, values, eventTime);
        taskExecutor.execute(wrapper);
        return true;
    }

    public boolean sendSMS(Client client, int messageType, Long messageTargetId, Object textObject, String[] values,
            Date eventTime) throws Exception {
        SendSmsThreadWrapper wrapper = new SendSmsThreadWrapper(transactionManager, em);
        return wrapper.sendSMS(client, messageType, messageTargetId, textObject, values, eventTime);
    }

    public static final class RunnableSendSmsThreadWrapper extends SendSmsThreadWrapper implements Runnable {

        protected Client client;
        protected int messageType;
        protected Long messageTargetId;
        protected Object textObject;
        protected String[] values;
        protected boolean result;
        protected Date eventTime;

        public RunnableSendSmsThreadWrapper() {
        }

        public RunnableSendSmsThreadWrapper(
                org.springframework.transaction.PlatformTransactionManager transactionManager,
                javax.persistence.EntityManager em, Client client, int messageType, Long messageTargetId,
                Object textObject, String[] values, Date eventTime) {
            super(transactionManager, em);

            this.client = client;
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
                result = sendSMS(this.client, this.messageType, this.messageTargetId, this.textObject, this.values,
                        this.eventTime);
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

        public SendSmsThreadWrapper(org.springframework.transaction.PlatformTransactionManager transactionManager,
                javax.persistence.EntityManager em) {
            this.transactionManager = transactionManager;
            this.em = em;
        }

        private boolean ignoreMobileTest(int messageType) {
            return (messageType == ClientSms.TYPE_INFO_MAILING_NOTIFICATION) || EventNotificationService
                    .isIgnoreEmptyMobile();
        }

        public boolean sendSMS(Client client, int messageType, Long messageTargetId, Object textObject, String[] values,
                Date eventTime) throws Exception {
            if (transactionManager == null) {
                throw new IllegalStateException("Transaction manager is null");
            }
            if (em == null) {
                throw new IllegalStateException("Entity manager is null");
            }
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            String phoneNumber = null, sender = "";

            ISmsService smsService = RuntimeContext.getInstance().getSmsService();

            try {
                if (client == null) {
                    throw new Exception("Client doesn't exist");
                }

                if (!smsService.ignoreNotifyFlags()) {
                    if (!client.isNotifyViaSMS() && messageType != ClientSms.TYPE_LINKING_TOKEN) {
                        return false;
                    }
                }
                phoneNumber = client.getMobile();
                if (!StringUtils.isNotEmpty(phoneNumber) && !ignoreMobileTest(messageType)) {
                    return false;
                }
                phoneNumber = PhoneNumberCanonicalizator.canonicalize(phoneNumber);
                if (StringUtils.length(phoneNumber) != 11 && !ignoreMobileTest(messageType)) {
                    return false;
                }
                transactionManager.commit(status);
                status = null;
            } finally {
                if (status != null) {
                    transactionManager.rollback(status);
                }
            }

            SendResponse sendResponse = null;

            logger.info("sending SMS: " + getLoggingInfo(textObject, messageType));
            String textMessage = null;
            if (smsService instanceof SMPPClient) {
                try {
                    sendResponse = smsService.sendTextMessage(sender, phoneNumber, textObject);
                    logger.info(String.format(
                            "sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, text: %s, RC: %s, error: %s",
                            sendResponse.getMessageId(), sender, phoneNumber, textObject.toString(),
                            sendResponse.getStatusCode(), sendResponse.getError()));
                    textMessage = textObject.toString();
                } catch (Exception e) {
                    logger.warn("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}, exception: {}",
                            new Object[]{sender, phoneNumber, textObject.toString(), e.getMessage()});
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    try {
                        if (smsService instanceof EMPSmsServiceImpl) {
                            sendResponse = ((EMPSmsServiceImpl) smsService).sendTextMessage(client, textObject);
                            boolean isSuccess = sendResponse.isSuccess();
                            if (sendResponse != null && !isSuccess) {
                                String msg = ((EMPEventType) textObject).buildText();
                                msg = String.format("E:[%s] %s", sendResponse.getStatusCode(), msg);
                                regisgterClientSMSFailedCharge(client, sendResponse.getMessageId(), phoneNumber,
                                        messageTargetId, messageType, msg);
                                return false;
                            }
                            textMessage = ((EMPEventType) textObject).buildText();
                        } else {
                            sendResponse = smsService.sendTextMessage(sender, phoneNumber, textObject);
                            textMessage = textObject.toString();
                        }
                        logger.info(
                                String.format("sent SMS, idOfSms: %s, sender: %s, phoneNumber: %s, RC: %s, error: %s",
                                        sendResponse.getMessageId(), sender, phoneNumber, sendResponse.getStatusCode(),
                                        sendResponse.getError()));
                        if (sendResponse.isSuccess()) {
                            break;
                        }
                    } catch (Exception e) {
                        logger.error("Failed to send SMS, sender: {}, phoneNumber: {}, text: {}, exception: {}",
                                new Object[]{sender, phoneNumber, textObject.toString(), e.getMessage()});
                    }
                }
            }

            boolean result = false;

            if (sendResponse != null) {
                Long idOfSourceOrg = EventNotificationService.getSourceOrgIdFromValues(values);
                result = registerClientSMSCharge(sendResponse.isSuccess(), client, sendResponse.getMessageId(),
                        phoneNumber, messageTargetId, messageType, textMessage, eventTime, idOfSourceOrg);

                //  Добавление в список неотправленных sms
                boolean failureTestingMode = RuntimeContext.getInstance()
                        .getOptionValueBool(Option.OPTION_SMS_FAILURE_TESTING_MODE);
                result = result && !failureTestingMode;
                if (!result) {
                    String serviceName = RuntimeContext.getInstance().getConfigProperties().
                            getProperty(RuntimeContext.SMS_SERVICE_PARAM_BASE + ".type", "atompark");
                    SMSResendingService.getInstance()
                            .addResending(sendResponse.getMessageId(), client, phoneNumber, serviceName, messageTargetId,
                                    messageType, textObject, values, eventTime);
                }
            }
            return result;
        }

        private String getLoggingInfo(Object textObject, int messageType) {
            String result = "";
            ((EMPEventType) textObject).getParameters();
            try {
                result += "phone=" + ((EMPEventType) textObject).getMsisdn();
                result += ";typeId=" + ((EMPEventType) textObject).getType();
                if (messageType >= ClientSms.TYPE_NOTIFICATION_START_SICK
                        && messageType <= ClientSms.TYPE_NOTIFICATION_CANCEL_END_SICK) {
                    //Только для специальных уведомлений
                    for (Map.Entry<String, String> entry : ((EMPEventType) textObject).getParameters().entrySet()) {
                        System.out.println("ID =  " + entry.getKey() + " Value = " + entry.getValue());
                        if (entry.getKey().equals(ExternalEventNotificationService.SURNAME) || entry.getKey()
                                .equals(ExternalEventNotificationService.NAME) || entry.getKey().equals("OrgName")
                                || entry.getKey().equals(ExternalEventNotificationService.ACCOUNT) || entry.getKey()
                                .equals(EventNotificationService.CLIENT_GENDER_KEY) || entry.getKey()
                                .equals(ExternalEventNotificationService.EMP_DATE) || entry.getKey().equals("OrgNum")) {
                            result += ";" + entry.getKey() + "=" + entry.getValue();
                        }
                    }
                } else {
                    Map<String, String> param = ((EMPEventType) textObject).getParameters();
                    for (String k : param.keySet()) {
                        String v = param.get(k);
                        result += ";" + k + "=" + v;
                    }
                }
            } catch (Exception ignore) {
            }
            return result;
        }

        protected boolean regisgterClientSMSFailedCharge(Client client, String messageId, String phoneNumber,
                Long messageTargetId, int messageType, String text) throws Exception {
            if (text == null || StringUtils.isBlank(text)) {
                return false;
            }
            ClientSms clientSms = RuntimeContext.getFinancialOpsManager()
                    .createClientFailedSmsCharge(client, messageId, phoneNumber, messageTargetId, messageType, text,
                            new Date());
            createdClientSms.set(clientSms);
            return true;
        }

        protected boolean registerClientSMSCharge(boolean success, Client client, String messageId, String phoneNumber,
                Long messageTargetId, int messageType, String text, Date eventTime, Long idOfSourceOrg)
                throws Exception {
            ClientSms clientSms = null;
            boolean result = false;
            if (success) {
                boolean delivered = RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl;
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

    public QueueState getQueueState() {
        int activeCount = ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount();
        int size = ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor().getQueue().remainingCapacity();
        int queue = ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor().getQueue().size();
        return new QueueState(activeCount, size, queue);
    }

    public class QueueState {

        private int activeCount;
        private int size;
        private int queue;

        public QueueState(int activeCount, int size, int queue) {
            this.activeCount = activeCount;
            this.size = size;
            this.queue = queue;
        }

        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getQueue() {
            return queue;
        }

        public void setQueue(int queue) {
            this.queue = queue;
        }
    }
}
