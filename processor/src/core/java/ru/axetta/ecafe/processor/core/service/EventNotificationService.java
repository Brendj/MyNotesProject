/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventTypeFactory;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPLeaveWithGuardianEventType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class EventNotificationService {

    Logger logger = LoggerFactory.getLogger(EventNotificationService.class);

    public static String NOTIFICATION_ENTER_EVENT = "enterEvent";
    public static String NOTIFICATION_BALANCE_TOPUP = "balanceTopup";
    public static String MESSAGE_LINKING_TOKEN_GENERATED = "linkingToken";
    public static String MESSAGE_RESTORE_PASSWORD = "restorePassword";
    public static String MESSAGE_PAYMENT = "payment";
    public static String NOTIFICATION_PASS_WITH_GUARDIAN = "passWithGuardian";
    public static String NOTIFICATION_SMS_SUBSCRIPTION_FEE = "smsSubscriptionFee";
    public static String NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS = "smsSubFeeWithdrawSuccessful";
    public static String NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS = "smsSubFeeWithdrawNotSuccessful";
    public static String NOTIFICATION_SUBSCRIPTION_FEEDING = "subscriptionFeeding";
    public static String NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS = "subFeeWithdrawNotSuccessful";
    public static String NOTIFICATION_GOOD_REQUEST_CHANGE = "goodRequestChange";
    public static String TYPE_SMS = "sms", TYPE_EMAIL_TEXT = "email.text", TYPE_EMAIL_SUBJECT = "email.subject";
    Properties notificationText;
    Boolean notifyBySMSAboutEnterEvent;

    @Resource
    SMSService smsService;
    @Autowired
    private RuntimeContext runtimeContext;

    static final String[] DEFAULT_MESSAGES = {
            NOTIFICATION_ENTER_EVENT + "." + TYPE_SMS,
            "[eventName] [eventTime] ([contractId] [surname] [firstName]). Баланс: [balance] р.",
            NOTIFICATION_ENTER_EVENT + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о времени прихода и ухода",
            NOTIFICATION_ENTER_EVENT + "." + TYPE_EMAIL_TEXT,
            "<html>\n" + "<body>\n" + "Уважаемый клиент, <br/><br/>\n\n"
            + "[eventName] [eventTime] ([surname] [firstName]). <br/>\n"
            + "Текущий баланс лицевого счета [balance] рублей. <br/>\n" + "<br/>\n" + "С уважением,<br/>\n"
            + "Служба поддержки клиентов\n" + "<br/><br/>\n"
            + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
            + "</body>\n" + "</html>",
            NOTIFICATION_BALANCE_TOPUP + "." + TYPE_SMS,
            "Зачислено [paySum]; баланс [balance] ([contractId] [surname] [firstName])",
            NOTIFICATION_BALANCE_TOPUP + "." + TYPE_EMAIL_TEXT,
            "<html>\n<body>\nУважаемый клиент, <br/><br/>\n" + "\n"
            + "на Ваш лицевой счет ([contractId] [surname] [firstName]) были зачислены средства в размере [paySum] руб.<br/>\n"
            + "Текущий баланс лицевого счета [balance] руб.\n" + "<br/><br/>\n" + "С уважением,<br/>\n"
            + "Служба поддержки клиентов\n" + "<br/><br/>\n"
            + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
            + "</body>\n" + "</html>",
            NOTIFICATION_BALANCE_TOPUP + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о пополнении баланса",
            MESSAGE_RESTORE_PASSWORD + "." + TYPE_EMAIL_TEXT,
            "Если Вы не запрашивали восстановление пароля, пожалуйста, удалите данное письмо. Для восстановления пароля перейдите по ссылке [url]",
            MESSAGE_RESTORE_PASSWORD + "." + TYPE_EMAIL_SUBJECT, "Восстановление пароля",
            /////
            MESSAGE_LINKING_TOKEN_GENERATED + "." + TYPE_SMS, "Код активации: [linkingToken]",
            MESSAGE_LINKING_TOKEN_GENERATED + "." + TYPE_EMAIL_TEXT,
            "<html>\n" + "<body>\n" + "Уважаемый клиент, <br/><br/>\n" + "\n"
                    + "Код активации личного кабинета: [linkingToken]. <br/>\n"
                    + "Если Вы не запрашивали код активации, пожалуйста, удалите данное письмо. <br/>\n" + "<br/>\n"
                    + "С уважением,<br/>\n" + "Служба поддержки клиентов\n" + "</body>\n" + "</html>",
            MESSAGE_LINKING_TOKEN_GENERATED + "." + TYPE_EMAIL_SUBJECT, "Код активации личного кабинета",
            MESSAGE_PAYMENT + "." + TYPE_SMS,
            "Списание [date] Л/с: [contractId] Буфет: [others] Комплекс: [complexes]",
            MESSAGE_PAYMENT + "." + TYPE_EMAIL_SUBJECT, "Уведомление о списании средств",
            MESSAGE_PAYMENT + "." + TYPE_EMAIL_TEXT,
            "<html>\n" + "<body>\n" + "Уважаемый клиент, <br/><br/>\n" + "\n"
            + "Дата списания: [date]. <br/>\n"
            + "Л/c: [contractId]. <br/>\n"
            + "Буфет: [others]. <br/>\n"
            + "Комплекс: [complexes]. <br/>\n"+ "<br/>\n"
            + "С уважением,<br/>\n" + "Служба поддержки клиентов\n" + "</body>\n" + "</html>",
            NOTIFICATION_SMS_SUBSCRIPTION_FEE + "." + TYPE_SMS,
            "Л/с: [contractId] Сервис SMS: не забудьте пополнить баланс до [withdrawDate]",
            NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS + "." + TYPE_SMS,
            "Списание [date] Л/с: [contractId] Сервис SMS: [smsSubscriptionFee] р.",
            NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS + "." + TYPE_SMS,
            "Л/с: [contractId] Сервис SMS отключен. Причина: недостаточный баланс.",
            /////
            NOTIFICATION_SUBSCRIPTION_FEEDING + "." + TYPE_SMS,
            "Л/с: [contractId] Сервис АП: не забудьте пополнить баланс до [withdrawDate]",
            NOTIFICATION_SUBSCRIPTION_FEEDING + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о состоянии субсчета абонентского питания",
            NOTIFICATION_SUBSCRIPTION_FEEDING + "." + TYPE_EMAIL_TEXT,
            "<html>\n<body>\nУважаемый клиент, <br/><br/>\n\n"
                    + "не забудьте пополнить баланс до [withdrawDate]. <br/>\n"
                    + "Текущий баланс лицевого счета ([contractId]) [balance] рублей. "
                    + "<br/>\n<br/>\nС уважением,<br/>\nСлужба поддержки клиентов\n<br/><br/>\n"
                    + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
                    + "</body>\n</html>",
            NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS + "." + TYPE_SMS,
            "Л/с: [contractId] Сервис АП. Причина: недостаточный баланс субсчета АП.",
            /////
            NOTIFICATION_PASS_WITH_GUARDIAN + "." + TYPE_SMS,
            "[eventName] [eventTime] [surname] [firstName] ([guardian])",
            NOTIFICATION_PASS_WITH_GUARDIAN + "." + TYPE_EMAIL_TEXT,
            "<html>\n<body>\nУважаемый клиент, <br/><br/>\n\n"
                    + "[eventName] [eventTime] [surname] [firstName] с представителем [guardian]. <br/>\n"
                    + "С уважением,<br/>\n"
                    + "Служба поддержки клиентов\n<br/><br/>\n"
                    + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
                    + "</body>\n" + "</html>",
            NOTIFICATION_PASS_WITH_GUARDIAN + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о времени прихода и ухода",
            NOTIFICATION_GOOD_REQUEST_CHANGE + "." + TYPE_EMAIL_TEXT,
            "[reportValues]",
            NOTIFICATION_GOOD_REQUEST_CHANGE + "." + TYPE_EMAIL_SUBJECT,
            "Заявка [shortOrgName] [reportType]",
            NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о состоянии подписки абонентского питания",
            NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS + "." + TYPE_EMAIL_TEXT,
            "<html>\n<body>\nУважаемый клиент, <br/><br/>\n\n"
                    + "По лицевому счету [contractId] "
                    + "Сервис АП отключен. Причина: недостаточный баланс. "
                    + "<br/>\n<br/>\nС уважением,<br/>\nСлужба поддержки клиентов\n<br/><br/>\n"
                    + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
                    + "</body>\n</html>"
    };                       // короткое имя школы

    String getDefaultText(String name) {
        for (int n = 0; n < DEFAULT_MESSAGES.length; n += 2) {
            if (name.equals(DEFAULT_MESSAGES[n])) {
                return DEFAULT_MESSAGES[n + 1];
            }
        }
        return null;
    }

    public synchronized String getNotificationText(String message, String type) {
        String prop = message + "." + type;
        if (notificationText == null) {
            StringReader stringReader = new StringReader(
                    RuntimeContext.getInstance().getOptionValueString(Option.OPTION_NOTIFICATION_TEXT));
            Properties properties = new Properties();
            try {
                properties.load(stringReader);
                notificationText = properties;
            } catch (Exception e) {
                logger.error("Failed to load notification text, notifications will be disabled", e);
                return null;
            }
        }
        String v = notificationText.getProperty(prop);
        if (v == null) {
            v = getDefaultText(prop);
        }
        return v;
    }

    public boolean sendEmail(Client client, String type, String[] values) {
        if (client.getEmail() == null || client.getEmail().length() == 0) {
            return false;
        }
        String emailText = getNotificationText(type, TYPE_EMAIL_TEXT), emailSubject = getNotificationText(type,
                TYPE_EMAIL_SUBJECT);
        if (emailText == null || emailSubject == null) {
            logger.warn("No email text is specified for type '" + type + "'. Email is not sent");
            return false;
        } else {
            emailText = formatMessage(emailText, values);
            emailSubject = formatMessage(emailSubject, values);
            try {
                RuntimeContext.getInstance().getPostman()
                        .postNotificationEmail(client.getEmail(), emailSubject, emailText);
            } catch (Exception e) {
                logger.error("Failed to send email notification", e);
                return false;
            }
        }
        return true;
    }

    @Async
    public synchronized boolean sendEmailAsync(String email, String type, String[] values) {
        logger.trace("start");
        if (StringUtils.isEmpty(email)) {
            logger.trace("email is empty");
            return false;
        }  else {

            String emailText = getNotificationText(type, TYPE_EMAIL_TEXT), emailSubject = getNotificationText(type,
                    TYPE_EMAIL_SUBJECT);
            logger.trace(emailSubject+" : "+emailText);
            if (emailText == null || emailSubject == null) {
                logger.warn(String.format("No email text is specified for type '%s'. Email is not sent", type));
                return false;
            } else {
                emailText = formatMessage(emailText, values);
                emailSubject = formatMessage(emailSubject, values);
                try {
                    logger.trace("run");
                    RuntimeContext.getInstance().getPostman().postNotificationEmail(email, emailSubject, emailText);
                } catch (Exception e) {
                    logger.error("Failed to send email notification", e);
                    return false;
                }
            }
        }
        return true;
    }

    @Async
    public void sendNotificationAsync(Client client, String type, String[] values) {
        sendNotification(client, type, values);
    }

    @Async
    public void sendNotificationAsync(Client client, String type, String[] values, Client guardian) {
        sendNotification(client, type, values, null, guardian);
    }

    @Async
    public void sendNotificationAsync(Client client, String type, String[] values, Integer passDirection) {
        sendNotification(client, type, values, passDirection, null);
    }

    @Async
    public void sendNotificationAsync(Client client, String type, String[] values, Integer passDirection, Client guardian) {
        sendNotification(client, type, values, passDirection, guardian);
    }

    @Async
    public void sendMessageAsync(Client client, String type, String[] values) {
        sendMessage(client, type, values);
    }

    public void sendNotification(Client client, String type, String[] values) {
        sendNotification(client, type, values, null, null);
    }

    public void sendNotification(Client client, String type, String[] values, Integer passDirection, Client guardian) {
        if (!client.isNotifyViaSMS() && !client.isNotifyViaEmail()) {
            return;
        }
        if (!isNotificationEnabled(client, type)) {
            return;
        }
        if (client.isNotifyViaSMS()) {
            if (isSMSNotificationEnabledForType(type)) {
                sendSMS(client, type, values, passDirection, guardian);
            }
        }
        if (client.isNotifyViaEmail()) {
            sendEmail(client, type, values);
        }
    }

    public boolean isNotificationEnabled(Client client, String type) {
        ClientNotificationSetting.Predefined predefined = ClientNotificationSetting.Predefined.parseByBinding(type);
        if (predefined == null) {
            return true;
        }
        RuntimeContext runtimeContext = null;
        Session session = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
        } catch (Exception e) {
            logger.error("Failed to receive session using RuntimeContext");
            return true;
        }

        try {
            org.hibernate.Query q = session.createSQLQuery("select notifytype " +
                    "from cf_clientsnotificationsettings " +
                    "where idofclient=?");
            q.setLong(0, client.getIdOfClient());
            List resultList = q.list();
            if (resultList.size() < 1 && predefined.isEnabledAtDefault()) {
                return true;
            }
            for (Object o : resultList) {
                BigInteger bi = (BigInteger) o;
                if (bi.longValue() == predefined.getValue()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Failed to check client {" + client.getIdOfClient() +
                    "} notification " + type + " in database", e);
        } finally {
            try {
                session.close();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public boolean sendMessage(Client client, String type, String[] values) {
        boolean bSend = false;
        if (client.hasMobile()) {
            bSend |= sendSMS(client, type, values);
        }
        if (client.hasEmail()) {
            bSend |= sendEmail(client, type, values);
        }
        return bSend;
    }

    public boolean sendSMS(Client client, String type, String[] values) {
        return sendSMS(client, type, values, true);
    }

    public boolean sendSMS(Client client, String type, String[] values, Client guardian) {
        return sendSMS(client, type, values, true, null, guardian);
    }

    public boolean sendSMS(Client client, String type, String[] values, Integer passDirection) {
        return sendSMS(client, type, values, true, passDirection);
    }

    public boolean sendSMS(Client client, String type, String[] values, boolean sendAsync) {
        return sendSMS(client, type, values, sendAsync, null);
    }

    public boolean sendSMS(Client client, String type, String[] values, boolean sendAsync, Integer direction) {
        return sendSMS(client, type, values, sendAsync, direction, null);
    }

    public boolean sendSMS(Client client, String type, String[] values, Integer direction, Client guardian) {
        return sendSMS(client, type, values, true, direction, null);
    }

    public boolean sendSMS(Client client, String type, String[] values, boolean sendAsync, Integer direction, Client guardian) {
        if (client.getMobile() == null || client.getMobile().length() == 0) {
            return false;
        }
        String text = getNotificationText(type, TYPE_SMS);
        if (text == null) {
            logger.warn("No notification SMS text is specified for type '" + type + "'. SMS is not sent");
            return false;
        }
        text = formatMessage(text, values);

        if (text.length() > 68) {
            text = text.substring(0, 67) + "..";
        }
        boolean result = false;
        try {
            int clientSMSType;
            if (type.equals(NOTIFICATION_ENTER_EVENT) || type.equals(NOTIFICATION_PASS_WITH_GUARDIAN)) {
                clientSMSType = ClientSms.TYPE_ENTER_EVENT_NOTIFY;
            } else if (type.equals(NOTIFICATION_BALANCE_TOPUP)) {
                clientSMSType = ClientSms.TYPE_PAYMENT_REGISTERED;
            } else if (type.equals(MESSAGE_LINKING_TOKEN_GENERATED)) {
                clientSMSType = ClientSms.TYPE_LINKING_TOKEN;
            } else if (type.equals(MESSAGE_PAYMENT)) {
                clientSMSType = ClientSms.TYPE_PAYMENT_NOTIFY;
            } else if (type.equals(NOTIFICATION_SMS_SUBSCRIPTION_FEE)) {
                clientSMSType = ClientSms.TYPE_SMS_SUBSCRIPTION_FEE;
            } else if (type.equals(NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS) || type
                    .equals(NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS)) {
                clientSMSType = ClientSms.TYPE_SMS_SUB_FEE_WITHDRAW;
            } else if(type.equals(NOTIFICATION_SUBSCRIPTION_FEEDING)){
                clientSMSType = ClientSms.TYPE_SUBSCRIPTION_FEEDING;
            } else if (type.equals(NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS)){
                clientSMSType = ClientSms.TYPE_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS;
            }else {
                throw new Exception("No client SMS type defined for notification " + type);
            }

            Object textObject = getTextObject(text, type, client, direction, guardian, values);
            if(textObject != null) {
                if (sendAsync) {
                    smsService.sendSMSAsync(client.getIdOfClient(), clientSMSType, textObject);
                    result = true;
                } else {
                    result = smsService.sendSMS(client.getIdOfClient(), clientSMSType, textObject);
                }
            }
        } catch (Exception e) {
            String message = String.format("Failed to send SMS notification to client with contract_id = %s.", client.getContractId());
            logger.error(message, e);
            return false;
        }
        return result;
    }

    private Object getTextObject(String text, String type, Client client, Integer direction, Client guardian, String[] values) {
        ISmsService smsService = runtimeContext.getSmsService();
        if(smsService instanceof EMPSmsServiceImpl) {
            EMPEventType empType = null;
            if(type.equals(NOTIFICATION_ENTER_EVENT) && direction != null &&
                    (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_EVENT, client);
            } else if(type.equals(NOTIFICATION_PASS_WITH_GUARDIAN) && direction != null &&
                    (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_WITH_GUARDIAN_EVENT, client);
                putGuardianParams(guardian, empType);
            } else if(type.equals(NOTIFICATION_BALANCE_TOPUP)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.FILL_EVENT, client);
                String amount = findValueInParams(new String [] {"paySum"}, values);
                String balance = findValueInParams(new String [] {"balance"}, values);
                if(amount != null && amount.length() > 0) {
                    empType.getParameters().put("amount", amount);
                }
                if(balance != null && balance.length() > 0) {
                    empType.getParameters().put("balance", balance);
                }
            } else if(type.equals(NOTIFICATION_ENTER_EVENT) && direction != null &&
                    (direction == EnterEvent.EXIT || direction == EnterEvent.RE_EXIT)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LEAVE_EVENT, client);
            } else if(type.equals(NOTIFICATION_PASS_WITH_GUARDIAN) && direction != null &&
                    (direction == EnterEvent.EXIT || direction == EnterEvent.RE_EXIT)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LEAVE_WITH_GUARDIAN_EVENT, client);
                putGuardianParams(guardian, empType);
            } /*else if(type.equals(MESSAGE_LINKING_TOKEN_GENERATED)) {
                EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.TOKEN_GENERATED_EVENT, client);
            }*/

            //  Устанавливаем дату
            String empDateStr = findValueInParams(new String [] {"empTime"}, values);
            String dateStr = findValueInParams(new String [] {"date", "eventTime", "time"}, values);
            if(dateStr != null && !StringUtils.isBlank(dateStr)) {
                try {
                    long ts = Date.parse(dateStr);
                    empType.setTime(ts);
                } catch (Exception e) {
                    logger.info("Failed to parse EMP date using simple date parser", e);
                }
            }
            if(empDateStr != null && !StringUtils.isBlank(empDateStr)) {
                try {
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                    Date eventDate = df.parse(empDateStr);
                    empType.setTime(eventDate.getTime());
                } catch (Exception e) {
                    logger.error("Failed to parse EMP date", e);
                }
            }
            /*for(int i=0; i<values.length-1; i+=2) {
                String name = values [i];
                String val = values[i+1];
                if(name.equals("empTime")) {
                    try {
                        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                        Date eventDate = df.parse(val);
                        empType.setTime(eventDate.getTime());
                        break;
                    } catch (Exception e) {
                        logger.error("Failed to parse EMP date", e);
                    }
                }
                if(name.equals("date") || name.equals("eventTime")) {
                    try {
                        long ts = Date.parse(val);
                        empType.setTime(ts);
                    } catch (Exception e) {
                        logger.info("Failed to parse EMP date using simple date parser", e);
                    }
                }
            }*/

            return empType;
        } else {
            return text;
        }
    }

    protected String findValueInParams(String valueNames[], String values[]) {
        if(valueNames == null || valueNames.length < 1) {
            return "";
        }
        for(int i=0; i<values.length-1; i+=2) {
            String name = values [i];
            String val = values[i+1];
            for(String vn : valueNames) {
                if(name.equals(vn)) {
                    return val;
                }
            }
        }
        return "";
    }

    private static final void putGuardianParams(Client guardian, EMPEventType empType) {
        String sn = "-";
        String n = "-";
        if(guardian != null) {
            sn = guardian.getPerson().getSurname();
            n = guardian.getPerson().getFirstName();
        }
        empType.getParameters().put(EMPLeaveWithGuardianEventType.GUARDIAN_SURNAME_PARAM, sn);
        empType.getParameters().put(EMPLeaveWithGuardianEventType.GUARDIAN_NAME_PARAM, n);
    }

    private boolean isSMSNotificationEnabledForType(String type) {
        if (type.equals(NOTIFICATION_ENTER_EVENT) || type.equals(NOTIFICATION_PASS_WITH_GUARDIAN)) {
            if (notifyBySMSAboutEnterEvent == null) {
                notifyBySMSAboutEnterEvent = RuntimeContext.getInstance()
                        .getOptionValueBool(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT);
            }
            return (notifyBySMSAboutEnterEvent != null && notifyBySMSAboutEnterEvent);
        } else {
            return true;
        }
    }

    private String formatMessage(String text, String[] values) {
        text = text.replaceAll("\\[br\\]", "\n");
        for (int n = 0; n < values.length; n += 2) {
            String s = "[" + values[n] + "]";
            int pos = text.indexOf(s);
            if (pos != -1) {
                text = text.substring(0, pos) + values[n + 1] + text.substring(pos + s.length());
            }
        }
        return text;
    }

    public void updateMessageTemplates(String[] values) {
        Properties properties = new Properties();
        StringBuilder stringBuilder = new StringBuilder();
        for (int n = 0; n < values.length; n += 3) {
            String value = values[n + 2];
            value = value.replaceAll("\\n", "[br]");
            value = value.replaceAll("\\r", "");
            String propName = values[n] + "." + values[n + 1];
            stringBuilder.append(propName);
            stringBuilder.append("=");
            stringBuilder.append(value).append("\n");
            properties.setProperty(propName, value);
        }
        RuntimeContext.getInstance().setOptionValue(Option.OPTION_NOTIFICATION_TEXT, stringBuilder.toString());
        RuntimeContext.getInstance().saveOptionValues();
        notificationText = properties;
    }


    /*public void sendPaymentNotificationSMS(long idOfOrg, SyncRequest.PaymentRegistry.Payment payment) {
        if (!RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON)) {
            return;
        }


        RuntimeContext runtimeContext = null;
        Session session = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
        } catch (Exception e) {
            logger.error("Failed to receive session using RuntimeContext");
            return;
        }


        Criteria clientCriteria = session.createCriteria(Client.class);
        clientCriteria.add(Restrictions.eq("idOfClient", payment.getIdOfClient()));
        List clientsList = clientCriteria.list();
        if (clientsList.size() < 1) {
            logger.error("Failed to receive clients with id " +
                    payment.getIdOfClient() + " to send SMS payment notification");
            return;
        }
        Client cl = (Client) clientsList.get(0);

        //  Если у пользователя не стоит флажка отправлять СМС, то пропускаем его
        if (!cl.isNotifyViaSMS()) {
            return;
        }


        long complexes = 0L;
        long others = 0L;
        Enumeration<SyncRequest.PaymentRegistry.Payment.Purchase> purchases = payment.getPurchases();
        while (purchases.hasMoreElements()) {
            SyncRequest.PaymentRegistry.Payment.Purchase purchase = purchases.nextElement();
            if (purchase.getType() >= OrderDetail.TYPE_COMPLEX_MIN && purchase.getType() <= OrderDetail.TYPE_COMPLEX_MAX) {
                complexes += purchase.getSocDiscount() + purchase.getRPrice();
            } else {
                others += purchase.getSocDiscount() + purchase.getRPrice();
            }
        }


        String date = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(System.currentTimeMillis()));
        String msg = "Столовая " + date + "\n" +
                "Л/с:" + cl.getContractId() + "\n" +
                "Буфет:" + beautifyAmount(others) + "\n" +
                "Комплекс:" + beautifyAmount(complexes);
        RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                .sendMessageAsync(cl, EventNotificationService.MESSAGE_PAYMENT,
                        new String[]{EventNotificationService.MESSAGE_PAYMENT, msg});
    }*/

    public String beautifyAmount(long amt) {
        String balanceStr = NumberFormat.getCurrencyInstance().format((double) amt / 100) + "р.";
        return balanceStr;
    }
}
