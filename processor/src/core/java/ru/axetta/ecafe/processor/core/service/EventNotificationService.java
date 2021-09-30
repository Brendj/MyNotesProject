/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sms.ISmsService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventTypeFactory;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPLeaveWithGuardianEventType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class EventNotificationService {

    Logger logger = LoggerFactory.getLogger(EventNotificationService.class);
    private final Object emailSend = new Object();

    public static String NOTIFICATION_ENTER_EVENT = "enterEvent";
    public static String NOTIFICATION_BALANCE_TOPUP = "balanceTopup";
    public static String MESSAGE_LINKING_TOKEN_GENERATED = "linkingToken";
    public static String MESSAGE_RESTORE_PASSWORD = "restorePassword";
    public static String MESSAGE_PAYMENT = "payment";
    public static String MESSAGE_PAYMENT_BAR = "paymentBuffet";
    public static String MESSAGE_PAYMENT_PAY = "paymentPay";
    public static String MESSAGE_PAYMENT_FREE = "paymentReduced";
    public static String NOTIFICATION_PASS_WITH_GUARDIAN = "passWithGuardian";
    public static String NOTIFICATION_SMS_SUBSCRIPTION_FEE = "smsSubscriptionFee";
    public static String NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS = "smsSubFeeWithdrawSuccessful";
    public static String NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS = "smsSubFeeWithdrawNotSuccessful";
    public static String NOTIFICATION_SUBSCRIPTION_FEEDING = "subscriptionFeeding";
    public static String NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS = "subFeeWithdrawNotSuccessful";
    public static String NOTIFICATION_GOOD_REQUEST_CHANGE = "goodRequestChange";
    public static String NOTIFICATION_SUMMARY_BY_DAY = "summaryByDay";
    public static String NOTIFICATION_SUMMARY_BY_WEEK = "summaryByWeek";
    public static String NOTIFICATION_INFO_MAILING = "infoMailing";
    public static String NOTIFICATION_LOW_BALANCE = "lowBalance";
    public static String NOTIFICATION_ENTER_MUSEUM = "enterMuseum";
    public static String NOTIFICATION_NOENTER_MUSEUM = "noEnterMuseum";
    public static String NOTIFICATION_ENTER_CULTURE = "enterCulture";
    public static String NOTIFICATION_EXIT_CULTURE = "exitCulture";
    public static String NOTIFICATION_END_BENEFIT = "endBenefit";
    public static String NOTIFICATION_PREFERENTIAL_FOOD = "preferentialFood";
    public static String NOTIFICATION_START_SICK = "startSick";
    public static String NOTIFICATION_CANCEL_START_SICK = "CstartSick";
    public static String NOTIFICATION_END_SICK = "endSick";
    public static String NOTIFICATION_CANCEL_END_SICK = "CendSick";
    public static String NOTIFICATION_LIBRARY = "library";
    public static String NOTIFICATION_CANCEL_PREORDER = "preorderCancelNotification";
    public static String NOTIFICATION_CLIENT_NEWPASSWORD = "clientNewPassword";
    public static String NOTIFICATION_EXPIRED_REGULAR_PAYMENT = "regularPaymentExpired";
    public static String TYPE_SMS = "sms", TYPE_EMAIL_TEXT = "email.text", TYPE_EMAIL_SUBJECT = "email.subject";
    Properties notificationText;
    Boolean notifyBySMSAboutEnterEvent;
    private static Boolean ignoreEmptyMobile = null;
    private static final String IGNORE_EMPTY_MOBILE_OPTION = "ecafe.processor.sms.service.emp.ignoreEmptyMobile";

    public static final String GUARDIAN_VALUES_KEY = "guardianId";
    public static final String TARGET_VALUES_KEY   = "targetId";
    public static final String SOURCE_ORG_VALUES_KEY   = "sourceOrgId";
    public static final String DIRECTION_VALUES_KEY   = "direction";
    public static final String ENTER_WITH_CHECKER_VALUES_KEY   = "enterWithChecker";
    public static final String ORG_ADDRESS_KEY = "address";
    public static final String ORG_SHORT_NAME_KEY = "shortnameinfoservice";
    public static final String CLIENT_GENDER_KEY = "gender";
    public static final String CLIENT_GENDER_VALUE_MALE = "male";
    public static final String CLIENT_GENDER_VALUE_FEMALE = "female";

    public static final String PARAM_ORDER_EVENT_TIME = "orderEventTime";
    public static final String PARAM_AMOUNT_PRICE = "amountPrice";
    public static final String PARAM_AMOUNT_LUNCH = "amountLunch";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_BALANCE_TO_NOTIFY = "balanceToNotify";
    public static final String PARAM_COMPLEX_NAME = "complexName";
    public static final String PARAM_DATE = "date";
    public static final String PARAM_AMOUNT_BUY_ALL = "amountBuyAll";
    public static final String PARAM_FRATION = "FRation";

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
            "[eventName] [eventTime] [surname] [firstName] ([guardian] [childPassCheckerMark] [childPassCheckerName])",
            NOTIFICATION_PASS_WITH_GUARDIAN + "." + TYPE_EMAIL_TEXT,
            "<html>\n<body>\nУважаемый клиент, <br/><br/>\n\n"
                    + "[eventName] [eventTime] [surname] [firstName] с представителем [guardian] [childPassCheckerMark] [childPassCheckerName] . <br/>\n"
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
                    + "</body>\n</html>",
            NOTIFICATION_LOW_BALANCE + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): баланс ниже [balanceToNotify] руб.",
            NOTIFICATION_ENTER_MUSEUM + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): посещение музея [event_place_code]",
            NOTIFICATION_NOENTER_MUSEUM + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): возврат билета в музей [event_place_code]",
            NOTIFICATION_START_SICK + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): Рекомендация об освобождении",
            NOTIFICATION_CANCEL_START_SICK + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): Аннулирование рекомендаций об освобождении",
            NOTIFICATION_END_SICK + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): Рекомендация о возможности посещать ОО",
            NOTIFICATION_CANCEL_END_SICK + "." + TYPE_SMS,
            "[surname] [name] (л/с: [account]): Аннулирование рекомендаций о возможности посещать ОО",
            NOTIFICATION_ENTER_CULTURE + "." + TYPE_SMS,
            "<html>\n" + "<body>\n" + "<b>Здравствуйте!<br/><br/>\n"
                    + "[empDate] в [empTimeH]</b> [surname] [name] зашел в здание культуры по адресу: [address]([shortnameinfoservice]).\n"
                    + "</body>\n" + "</html>",

            NOTIFICATION_EXIT_CULTURE + "." + TYPE_SMS,
            "<html>\n" + "<body>\n" + "<b>Здравствуйте!<br/><br/>\n"
                    + "[empDate] в [empTimeH]</b> [surname] [name] вышел из здания культуры по адресу: [address]([shortnameinfoservice]).\n"
                    + "</body>\n" + "</html>",
            NOTIFICATION_ENTER_CULTURE + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о заходе в здание культуры",
            NOTIFICATION_ENTER_CULTURE + "." + TYPE_EMAIL_TEXT,
            "<html>\n" + "<body>\n" + "<b>Здравствуйте!<br/><br/>\n"
                    + "[empDate] в [empTimeH]</b> [surname] [name] зашел в здание культуры по адресу: [address]([shortnameinfoservice]).\n"
                    + "</body>\n" + "</html>",
            NOTIFICATION_EXIT_CULTURE + "." + TYPE_EMAIL_SUBJECT,
            "Уведомление о выходе из здания культуры",
            NOTIFICATION_EXIT_CULTURE + "." + TYPE_EMAIL_TEXT,
            "<html>\n" + "<body>\n" + "<b>Здравствуйте!<br/><br/>\n"
                    + "[empDate] в [empTimeH]</b> [surname] [name] вышел из здания культуры по адресу: [address]([shortnameinfoservice]).\n"
                    + "</body>\n" + "</html>"
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
    public boolean sendEmailAsync(String email, String type, String[] values) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }  else {

            String emailText = getNotificationText(type, TYPE_EMAIL_TEXT), emailSubject = getNotificationText(type,
                    TYPE_EMAIL_SUBJECT);
            if (emailText == null || emailSubject == null) {
                logger.info(String.format("No email text is specified for type '%s'. Email is not sent", type));
                return false;
            } else {
                emailText = formatMessage(emailText, values);
                emailSubject = formatMessage(emailSubject, values);
                synchronized (emailSend) {
                    try {
                        RuntimeContext.getInstance().getPostman().postNotificationEmail(email, emailSubject, emailText);
                    } catch (Exception e) {
                        logger.error("Failed to send email notification", e);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Async
    public void sendNotificationAsync(Client destClient, Client dataClient, String type, String[] values, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, eventTime);
    }

    @Async
    public void sendNotificationAsync(Client destClient, Client dataClient, String type, String[] values, Client guardian, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, null, guardian, eventTime);
    }

    @Async
    public void sendNotificationAsync(Client destClient, Client dataClient, String type, String[] values, Integer passDirection, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, passDirection, null, eventTime);
    }

    @Async
    public void sendNotificationAsync(Client destClient, Client dataClient, String type, String[] values, Integer passDirection, Client guardian, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, passDirection, guardian, eventTime);
    }

    @Async
    public void sendMessageAsync(Client client, String type, String[] values, Date eventTime) {
        sendMessage(client, type, values, eventTime);
    }

    public void sendNotification(Client destClient, Client dataClient, String type, String[] values, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, null, null, eventTime);
    }

    public void sendNotification(Client destClient, Client dataClient, String type, String[] values, Integer passDirection, Client guardian, Date eventTime) {
        sendNotification(destClient, dataClient, type, values, passDirection, guardian, null, eventTime);
    }

    public void sendNotification(Client destClient, Client dataClient, String type, String[] values, Integer passDirection, Client guardian, Boolean sendAsync, Date eventTime) {

        if (dataClient == null && !isNotificationEnabled(destClient, type, values)) {
            logger.info("У клиента с л/с " + destClient.getContractId() + " отключен данный тип уведомления");
            return;
        }
        Boolean sms = null;
        if (smsService.ignoreNotifyFlags() || destClient.isNotifyViaSMS()) {
            if (isSMSNotificationEnabledForType(type)) {
                if(sendAsync != null) {
                    sms = sendSMS(destClient, dataClient, type, values, sendAsync, passDirection, guardian, eventTime);
                } else {
                    sms = sendSMS(destClient, dataClient, type, values, passDirection, guardian, eventTime);
                }
            }
        } else
        {
            logger.info("У клиента с л/с " + destClient.getContractId() + " отключен уведомление по SMS");
        }

        if (smsService.isEmailSentByPlatform()) {
            return;
        }

        Boolean email = null;
        if (destClient.isNotifyViaEmail()) {
            email = sendEmail(destClient, type, values);
        }

        if (!(destClient.getMobile() == null || destClient.getMobile().length() == 0) && smsService.ignoreNotifyFlags()) {
            if (sms != null || email != null) {
                if ((sms != null && !sms) && (email != null && !email)) {
                    throw new RuntimeException("Failed to send notification via sms and email");
                }
                if (sms != null && !sms) {
                    throw new RuntimeException("Failed to send notification via sms");
                }
                if (email != null && !email) {
                    throw new RuntimeException("Failed to send notification via email");
                }
            }
        }
    }

    public boolean isNotificationEnabled(Client client, String type, String[] values) {
        if (type.equals(EventNotificationService.MESSAGE_PAYMENT)) {
            type = getOrderNotificationType(values);
        }
        ClientNotificationSetting.Predefined predefined;
        //Сообщения об окончинии срока льгот имеет тип Служебные
        if (type.equals(EventNotificationService.NOTIFICATION_END_BENEFIT) ||
                type.equals(EventNotificationService.NOTIFICATION_CANCEL_PREORDER))
            predefined = ClientNotificationSetting.Predefined.SMS_NOTIFY_SPECIAL;
        else
            predefined = ClientNotificationSetting.Predefined.parseByBinding(type);
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

    private String getOrderNotificationType(String[] values) {
        if(EventNotificationService.findBooleanValueInParams(new String[]{"isBarOrder"}, values)) {
            return MESSAGE_PAYMENT;
        } else if(EventNotificationService.findBooleanValueInParams(new String[]{"isPayOrder"}, values)) {
            return MESSAGE_PAYMENT_PAY;
        } else if(EventNotificationService.findBooleanValueInParams(new String[]{"isFreeOrder"}, values)) {
            return MESSAGE_PAYMENT_FREE;
        } else {
            return MESSAGE_PAYMENT;
        }
    }

    public boolean sendMessage(Client client, String type, String[] values, Date eventTime) {
        boolean bSend = false;
        if (client.hasMobile()) {
            bSend |= sendSMS(client, null, type, values, eventTime);
        }

        ISmsService smsService = RuntimeContext.getInstance().getSmsService();

        if (client.hasEmail() && client.isNotifyViaEmail() && !smsService.emailDisabled()) {
            bSend |= sendEmail(client, type, values);
        }
        return bSend;
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, true, eventTime);
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, Client guardian, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, true, null, guardian, eventTime);
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, Integer passDirection, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, true, passDirection, eventTime);
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, boolean sendAsync, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, sendAsync, null, eventTime);
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, boolean sendAsync, Integer direction, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, sendAsync, direction, null, eventTime);
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, Integer direction, Client guardian, Date eventTime) {
        return sendSMS(destClient, dataClient, type, values, true, direction, guardian, eventTime);
    }

    private boolean isLeavingGroup(Client client) {
        return client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())
                || client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_DELETED.getValue());
    }

    public static boolean isIgnoreEmptyMobile() {
        if (ignoreEmptyMobile == null) {
            ignoreEmptyMobile = RuntimeContext.getInstance().getConfigProperties().getProperty(IGNORE_EMPTY_MOBILE_OPTION, "false").equals("true");
        }
        return ignoreEmptyMobile;
    }

    public boolean sendSMS(Client destClient, Client dataClient, String type, String[] values, boolean sendAsync, Integer direction, Client guardian, Date eventTime) {
        if ((StringUtils.isEmpty(destClient.getMobile()) && !isIgnoreEmptyMobile()) || isLeavingGroup(destClient)) {
            return false;
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
            } else if (type.equals(NOTIFICATION_SUMMARY_BY_DAY)) {
                clientSMSType = ClientSms.TYPE_SUMMARY_DAILY_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_LOW_BALANCE)) {
                clientSMSType = ClientSms.TYPE_LOW_BALANCE_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_ENTER_MUSEUM)) {
                clientSMSType = ClientSms.TYPE_ENTER_MUSEUM_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_NOENTER_MUSEUM)) {
                clientSMSType = ClientSms.TYPE_NOENTER_MUSEUM_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_ENTER_CULTURE)) {
                clientSMSType = ClientSms.TYPE_ENTER_CULTURE_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_END_BENEFIT)) {
                clientSMSType = ClientSms.TYPE_CLIENT_END_BENEFIT_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_PREFERENTIAL_FOOD)) {
                clientSMSType = ClientSms.TYPE_CLIENT_PREFERENTIAL_FOOD_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_EXIT_CULTURE)) {
                clientSMSType = ClientSms.TYPE_EXIT_CULTURE_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_START_SICK)) {
                clientSMSType = ClientSms.TYPE_NOTIFICATION_START_SICK;
            } else if (type.equals(NOTIFICATION_CANCEL_START_SICK)) {
                clientSMSType = ClientSms.TYPE_NOTIFICATION_CANCEL_START_SICK;
            } else if (type.equals(NOTIFICATION_END_SICK)) {
                clientSMSType = ClientSms.TYPE_NOTIFICATION_END_SICK;
            } else if (type.equals(NOTIFICATION_CANCEL_END_SICK)) {
                clientSMSType = ClientSms.TYPE_NOTIFICATION_CANCEL_END_SICK;
            } else if (type.equals(NOTIFICATION_CANCEL_PREORDER)) {
                clientSMSType = ClientSms.TYPE_PREORDER_CANCEL_NOTIFICATION;
            } else if (type.equals(NOTIFICATION_LIBRARY)) {
                clientSMSType = ClientSms.TYPE_NOTIFICATION_LIBRARY;
            } else {
                throw new Exception("No client SMS type defined for notification " + type);
            }

            Object textObject = getTextObject(type, destClient, dataClient, direction, guardian, values);
            if(textObject != null) {
                if (sendAsync) {
                    smsService.sendSMSAsync(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, eventTime);
                    result = true;
                } else {
                    result = smsService.sendSMS(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, eventTime);
                }
            }
        } catch (Exception e) {
            String message = String.format("Failed to send SMS notification to client with contract_id = %s.", destClient.getContractId());
            logger.error(message, e);
            return false;
        }
        return result;
    }

    public boolean sendNotificationExpiredSubscription(Client destClient, Client dataClient, String type, String[] values, Date eventTime) {
        if (StringUtils.isEmpty(destClient.getMobile())) return true;
        boolean result = false;
        int clientSMSType = ClientSms.TYPE_EXPIRED_REGULAR_PAYMENT_SUBSCRIPTION_NOTIFICATION;
        try {
            Object textObject = getExpiredRegularPaymentSubscriptionNotificationObject(destClient, dataClient, values);
            if (textObject != null) {
                smsService.sendSMSAsync(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, eventTime);
                result = true;
            }
        } catch (Exception e) {
            String message = String.format("Failed to send summary notification to client with contract_id = %s.", destClient.getContractId());
            logger.error(message, e);
            return false;
        }

        return result;
    }

    private Object getExpiredRegularPaymentSubscriptionNotificationObject(Client destClient, Client dataClient, String[] values) {
        EMPEventType empType = null;
        if (dataClient != null)
            empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.REGULAR_PAYMENT_EXPIRATION_EVENT, dataClient, destClient);
        else
            empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.REGULAR_PAYMENT_EXPIRATION_EVENT, destClient);
        for (int i = 0; i < values.length-1; i=i+2) {
            empType.getParameters().put(values[i], values[i+1]);
        }
        return empType;
    }

    public boolean sendNotificationSummary(Client destClient, Client dataClient, String type, String[] values,
            Date eventTime, int notificationType) {
        boolean result = false;
        int clientSMSType = notificationType; //ClientSms.TYPE_SUMMARY_DAILY_NOTIFICATION;
        try {
            Object textObject = getSummaryNotificationObject(type, destClient, dataClient, values);
            if (textObject != null) {
                smsService.sendSMSAsync(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, eventTime);
                result = true;
            }
        } catch (Exception e) {
            String message = String.format("Failed to send summary notification to client with contract_id = %s.", destClient.getContractId());
            logger.error(message, e);
            return false;
        }
        return result;
    }

    private Object getSummaryNotificationObject(String type, Client destClient, Client dataClient, String[] values) {
        EMPEventType empType = null;
        if(type.equals(NOTIFICATION_SUMMARY_BY_DAY)) {
            if (dataClient != null)
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SUMMARY_DAILY_EVENT, dataClient, destClient);
            else
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SUMMARY_DAILY_EVENT, destClient);
        }
        if(type.equals(NOTIFICATION_SUMMARY_BY_WEEK)) {
            if (dataClient != null)
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SUMMARY_WEEKLY_EVENT, dataClient, destClient);
            else
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SUMMARY_WEEKLY_EVENT, destClient);
        }

        for (int i = 0; i < values.length-1; i=i+2) {
            empType.getParameters().put(values[i], values[i+1]);
        }

        String empDateStr = findValueInParams(new String [] {"empTime"}, values);
        if(empDateStr != null && !StringUtils.isBlank(empDateStr)) {
            try {
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                Date eventDate = df.parse(empDateStr);
                empType.setTime(eventDate.getTime());
            } catch (Exception e) {
                logger.error("Failed to parse EMP date", e);
            }
        }
        return empType;
    }

    @Async
    public boolean sendNotificationInfoMailingAsync(Client destClient, String[] values, Date eventTime) {
        boolean result = false;
        int clientSMSType = ClientSms.TYPE_INFO_MAILING_NOTIFICATION;
        try {
            Object textObject = getInfoMailingNotificationObject(destClient, values);
            if (textObject != null) {
                smsService.sendSMSAsync(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, eventTime);
                result = true;
            }
        } catch (Exception e) {
            String message = String.format("Failed to send summary notification to client with contract_id = %s.", destClient.getContractId());
            logger.error(message, e);
            return false;
        }
        return result;
    }

    @Async
    public boolean sendNotificationClientNewPasswordAsync(Client destClient, String[] values) {
        boolean result = false;
        int clientSMSType = ClientSms.TYPE_CLIENT_NEWPASSWORD_NOTIFICATION;
        try {
            Object textObject = getClientNewPasswordNotificationObject(destClient, values);
            if (textObject != null) {
                smsService.sendSMSAsync(destClient, clientSMSType, getTargetIdFromValues(values), textObject, values, new Date());
                result = true;
            }
        } catch (Exception e) {
            String message = String.format("Failed to send client new password notification to client with contract_id = %s.", destClient.getContractId());
            logger.error(message, e);
            return false;
        }
        return result;
    }

    private Object getInfoMailingNotificationObject(Client destClient, String[] values) {
        EMPEventType empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.INFO_MAILING_EVENT, destClient);
        for (int i = 0; i < values.length-1; i=i+2) {
            empType.getParameters().put(values[i], values[i+1]);
        }
        return empType;
    }

    private Object getClientNewPasswordNotificationObject(Client destClient, String[] values) {
        EMPEventType empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.CLIENT_NEWPASSWORD_EVENT, destClient);
        for (int i = 0; i < values.length-1; i=i+2) {
            empType.getParameters().put(values[i], values[i+1]);
        }
        return empType;
    }

    public static final String[] attachToValues(String key, String val, String [] values) {
        if(val == null) {
            return values;
        }
        if(values == null || values.length < 2) {
            values = new String[] { key, "" + val };
            return values;
        }

        for(int i=0; i<values.length-1; i+=2) {
            String name = values [i];
            if(name.equals(key)) {
                values[i+1] = "" + val;
                return values;
            }
        }


        String[] newValues = new String[values.length + 2];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 2] = key;
        newValues[newValues.length - 1] = "" + val;
        return newValues;
    }

    public static final String[] attachSourceOrgIdToValues(Long orgId, String[] values) {
        if(orgId == null) {
            return values;
        }
        return attachToValues(SOURCE_ORG_VALUES_KEY, "" + orgId, values);
    }

    public static final String[] attachTargetIdToValues(Long targetId, String[] values) {
        if(targetId == null) {
            return values;
        }
        return attachToValues(TARGET_VALUES_KEY, "" + targetId, values);
    }

    public static final String[] attachEventDirectionToValues(Integer direction, String[] values) {
        if(direction == null) {
            return values;
        }
        return attachToValues(DIRECTION_VALUES_KEY, "" + direction, values);
    }

    public static final String[] attachGuardianIdToValues(Long guardianId, String[] values) {
        if(guardianId == null) {
            return values;
        }
        return attachToValues(GUARDIAN_VALUES_KEY, "" + guardianId, values);
    }

    public static final String[] attachOrgAddressToValues(String address, String[] values) {
        if (StringUtils.isEmpty(address)) {
            return values;
        }
        return attachToValues(ORG_ADDRESS_KEY, address, values);
    }

    public static final String[] attachOrgShortNameToValues(String shortName, String[] values) {
        if (StringUtils.isEmpty(shortName)) {
            return values;
        }
        return attachToValues(ORG_SHORT_NAME_KEY, shortName, values);
    }

    public static final String[] attachGenderToValues(Integer gender, String[] values) {
        if (null == gender) {
            return values;
        }

        String genderString;
        switch (gender) {
            case 0: genderString = CLIENT_GENDER_VALUE_FEMALE; break;
            case 1: genderString = CLIENT_GENDER_VALUE_MALE; break;
            default: return values;
        }

        return attachToValues(CLIENT_GENDER_KEY, genderString, values);
    }

    public static final String[] attachMoneyToValues(Long amountBuyAll, String[] values, String nameParam) {
        if (null == amountBuyAll) {
            return values;
        }

        Long rub = amountBuyAll / 100;
        Long cop = amountBuyAll % 100;
        String cop_str = cop.toString();
        while (cop_str.length() < 2) {
            cop_str = "0" + cop_str;
        }
        String amountBuyAllString = rub.toString() + "," + cop_str;

        return attachToValues(nameParam, amountBuyAllString, values);
    }

    public static Long getTargetIdFromValues(String[] values) {
        String id = findValueInParams(new String [] {TARGET_VALUES_KEY}, values);
        if(id == null || StringUtils.isBlank(id) || !NumberUtils.isNumber(id)) {
            return null;
        }
        return Long.parseLong(id);
    }

    public static Long getSourceOrgIdFromValues(String[] values) {
        String id = findValueInParams(new String [] {SOURCE_ORG_VALUES_KEY}, values);
        if(id == null || StringUtils.isBlank(id) || !NumberUtils.isNumber(id)) {
            return null;
        }
        return Long.parseLong(id);
    }

    public static Long getGuardianIdFromValues(String[] values) {
        String id = findValueInParams(new String [] {GUARDIAN_VALUES_KEY}, values);
        if(id == null || StringUtils.isBlank(id) || !NumberUtils.isNumber(id)) {
            return null;
        }
        return Long.parseLong(id);
    }

    public static Integer getEventDirectionFromValues(String[] values) {
        String id = findValueInParams(new String [] {DIRECTION_VALUES_KEY}, values);
        if(id == null || StringUtils.isBlank(id) || !NumberUtils.isNumber(id)) {
            return null;
        }
        return Integer.parseInt(id);
    }

    private EMPEventType getEnterEventEMPType(Client destClient, Client dataClient, Integer direction, String[] values) {
        EMPEventType empType;
        int type;
        String id = findValueInParams(new String [] {ENTER_WITH_CHECKER_VALUES_KEY}, values);
        switch (id) {
            case "1":
                if (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)
                    type = EMPEventTypeFactory.ENTER_WITH_CHECKER;
                else
                    type = EMPEventTypeFactory.LEAVE_WITH_CHECKER;
                break;
            case "2":
                if (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)
                    type = EMPEventTypeFactory.ENTER_WITH_GUARDIAN_EVENT;
                else
                    type = EMPEventTypeFactory.LEAVE_WITH_GUARDIAN_EVENT;
                break;
            case "3":
                if (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)
                    type = EMPEventTypeFactory.ENTER_EVENT;
                else
                    type = EMPEventTypeFactory.LEAVE_EVENT;
                break;
            default:
                if (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)
                    type = EMPEventTypeFactory.ENTER_EVENT;
                else
                    type = EMPEventTypeFactory.LEAVE_EVENT;
                break;
        }
        if (dataClient != null) {
            empType = EMPEventTypeFactory.buildEvent(type, dataClient, destClient);
        } else {
            empType = EMPEventTypeFactory.buildEvent(type, destClient);
        }
        return empType;
    }

    private Object getTextObject(String type, Client destClient, Client dataClient, Integer direction, Client guardianClient, String[] values) {
        ISmsService smsService = runtimeContext.getSmsService();
        if(smsService instanceof EMPSmsServiceImpl) {
            EMPEventType empType = null;
            if(type.equals(NOTIFICATION_ENTER_EVENT) && direction != null &&
                    (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY
                    || direction == EnterEvent.EXIT || direction == EnterEvent.RE_EXIT)) {
                empType = getEnterEventEMPType(destClient, dataClient, direction, values);
                putOrgParams(empType, values);
                putGenderParams(empType, values);
            } else if(type.equals(NOTIFICATION_PASS_WITH_GUARDIAN) && direction != null &&
                    (direction == EnterEvent.ENTRY || direction == EnterEvent.RE_ENTRY)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_WITH_GUARDIAN_EVENT, dataClient, destClient);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_WITH_GUARDIAN_EVENT, destClient);
                }
                putGuardianParams(guardianClient, empType);
                putOrgParams(empType, values);
            } else if(type.equals(NOTIFICATION_BALANCE_TOPUP)) {

                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.FILL_EVENT, dataClient, destClient);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.FILL_EVENT, destClient);
                }

                String amount = findValueInParams(new String[]{"paySum"}, values);
                String balance = findValueInParams(new String[]{"balance"}, values);
                if (amount != null && amount.length() > 0) {
                    empType.getParameters().put("amount", amount);
                }
                if (balance != null && balance.length() > 0) {
                    empType.getParameters().put("balance", balance);
                }
            } else if(type.equals(NOTIFICATION_PASS_WITH_GUARDIAN) && direction != null &&
                    (direction == EnterEvent.EXIT || direction == EnterEvent.RE_EXIT)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory
                            .buildEvent(EMPEventTypeFactory.LEAVE_WITH_GUARDIAN_EVENT, dataClient, destClient);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LEAVE_WITH_GUARDIAN_EVENT, destClient);
                }
                putGuardianParams(guardianClient, empType);
                putOrgParams(empType, values);
            } else if(type.equals(MESSAGE_LINKING_TOKEN_GENERATED)) {
                empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.TOKEN_GENERATED_EVENT, destClient);
                String token = findValueInParams(new String [] {"linkingToken"}, values);
                empType.getParameters().put("token", token);
            }  else if(type.equals(MESSAGE_PAYMENT)) {
                int empEventType = -1;
                if(findBooleanValueInParams(new String[]{"isBarOrder"}, values)) {
                    empEventType = EMPEventTypeFactory.PAYMENT_EVENT;
                } else if(findBooleanValueInParams(new String[]{"isPayOrder"}, values)) {
                    empEventType = EMPEventTypeFactory.PAYMENT_PAY_EVENT;
                } else if(findBooleanValueInParams(new String[]{"isFreeOrder"}, values)) {
                    empEventType = EMPEventTypeFactory.PAYMENT_REDUCED_EVENT;
                } else {
                    throw new RuntimeException("Попытка отправить уведомление о неизвестном типе события");
                }

                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(empEventType, dataClient, destClient);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(empEventType, destClient);
                }

                if (!findValueInParams(new String[]{PARAM_FRATION}, values).isEmpty())
                {
                    String ration = findValueInParams(new String[]{PARAM_FRATION}, values);
                    empType.getParameters().put(PARAM_FRATION, ration);
                }

                //  дата только для платного комплекса + льготного комплекса
                if(findBooleanValueInParams(new String[]{"isFreeOrder"}, values) ||
                   findBooleanValueInParams(new String[]{"isPayOrder"}, values)) {
                    String orderEventDate = findValueInParams(new String[]{PARAM_ORDER_EVENT_TIME}, values);
                    empType.getParameters().put(PARAM_DATE, orderEventDate.substring(0, 10)); //дата без времени
                    String complexName = findValueInParams(new String[]{PARAM_COMPLEX_NAME}, values);
                    empType.getParameters().put(PARAM_COMPLEX_NAME, complexName);
                }

                //  сумма только для буфет + платное
                String amountPrice = findValueInParams(new String[]{PARAM_AMOUNT_PRICE}, values);
                String amountLunch = findValueInParams(new String[]{PARAM_AMOUNT_LUNCH}, values);
                String amount = findValueInParams(new String[]{PARAM_AMOUNT}, values);
                amountPrice = amountPrice != null && !StringUtils.isEmpty(amountPrice) ? amountPrice : "" + 0D;
                amountLunch = amountLunch != null && !StringUtils.isEmpty(amountLunch) ? amountLunch : "" + 0D;
                amount = amount != null && !StringUtils.isEmpty(amount) ? amount : "" + 0D;
                empType.getParameters().put(PARAM_AMOUNT_PRICE, amountPrice);
                empType.getParameters().put(PARAM_AMOUNT_LUNCH, amountLunch);
                empType.getParameters().put(PARAM_AMOUNT, amount);
                if (empEventType == EMPEventTypeFactory.PAYMENT_EVENT) {
                    String amountBuyAll = findValueInParams(new String[]{PARAM_AMOUNT_BUY_ALL}, values);
                    amountBuyAll = amountBuyAll != null && !StringUtils.isEmpty(amountBuyAll) ? amountBuyAll : "" + 0D;
                    empType.getParameters().put(PARAM_AMOUNT_BUY_ALL, amountBuyAll);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_LOW_BALANCE)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LOW_BALANCE_EVENT, dataClient, destClient);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LOW_BALANCE_EVENT, destClient);
                }
                String balanceToNotify = findValueInParams(new String[]{PARAM_BALANCE_TO_NOTIFY}, values);
                empType.getParameters().put(PARAM_BALANCE_TO_NOTIFY, balanceToNotify);
            } else if (type.equals(NOTIFICATION_ENTER_MUSEUM)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_MUSEUM_EVENT, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_MUSEUM_EVENT, destClient, values);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_NOENTER_MUSEUM)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.NOENTER_MUSEUM_EVENT, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.NOENTER_MUSEUM_EVENT, destClient, values);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_END_BENEFIT)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.END_BENEFIT, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.END_BENEFIT, destClient, values);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_PREFERENTIAL_FOOD)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.REFUSAL_PREFERENTIAL_FOOD, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.REFUSAL_PREFERENTIAL_FOOD, destClient, values);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_ENTER_CULTURE)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_CULTURE, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_CULTURE, destClient, values);
                }
                putGenderParams(empType, values);
            } else if (type.equals(NOTIFICATION_EXIT_CULTURE)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LEAVE_CULTURE, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.LEAVE_CULTURE, destClient, values);
                }
                putGenderParams(empType, values);
            }
            else if (type.equals(NOTIFICATION_START_SICK) || type.equals(NOTIFICATION_CANCEL_START_SICK)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SPECIAL_TYPE_EVENT, dataClient, destClient, 2, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SPECIAL_TYPE_EVENT, destClient, 2, values);
                }
                putGenderParams(empType, values);
            }
            else if (type.equals(NOTIFICATION_END_SICK) || type.equals(NOTIFICATION_CANCEL_END_SICK)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SPECIAL_TYPE_EVENT, dataClient, destClient, 3, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.SPECIAL_TYPE_EVENT, destClient, 3, values);
                }
                putGenderParams(empType, values);
            }
            else if (type.equals(NOTIFICATION_LIBRARY)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_LIBRARY, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.ENTER_LIBRARY, destClient, values);
                }
            }
            else if (type.equals(NOTIFICATION_CANCEL_PREORDER)) {
                if (dataClient != null) {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.CANCEL_PREORDER, dataClient, destClient, values);
                } else {
                    empType = EMPEventTypeFactory.buildEvent(EMPEventTypeFactory.CANCEL_PREORDER, destClient, values);
                }
            }

            String empDateStr = findValueInParams(new String[]{"empTime"}, values);
            if (empDateStr != null && !StringUtils.isBlank(empDateStr)) {
                try {
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                    Date eventDate = df.parse(empDateStr);
                    empType.getParameters().put("time", new SimpleDateFormat("HH:mm").format(new Date(eventDate.getTime())));
                } catch (Exception e) {
                    logger.error("Failed to parse EMP date", e);
                }
            }

            String isTest = findValueInParams(new String[]{ExternalEventNotificationService.TEST}, values);
            if (!isTest.equals(""))
                empType.getParameters().put(ExternalEventNotificationService.TEST, isTest);

            empType.setTime(new Date().getTime());
            return empType;
        } else {
            return "Not found";
        }
    }

    public static boolean findBooleanValueInParams(String valueNames[], String values[]) {
        String res = findValueInParams(valueNames, values);
        if(res == null || StringUtils.isBlank(res)) {
            return false;
        }
        if(NumberUtils.isNumber(res)) {
            double v = NumberUtils.toDouble(res);
            return v > 0;
        }
        return Boolean.parseBoolean(res);
    }

    protected static String findValueInParams(String valueNames[], String values[]) {
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
            try {
                sn = guardian.getPerson().getSurname();
                n = guardian.getPerson().getFirstName();
            } catch (Exception e) {
                Person p = DAOReadonlyService.getInstance().getPersonByClient(guardian);
                sn = p.getSurname();
                n = p.getFirstName();
            }
        }
        empType.getParameters().put(EMPLeaveWithGuardianEventType.GUARDIAN_SURNAME_PARAM, sn);
        empType.getParameters().put(EMPLeaveWithGuardianEventType.GUARDIAN_NAME_PARAM, n);
    }

    private static final void putOrgParams(EMPEventType empType, String[] values) {
        String orgAddress = findValueInParams(new String[] {ORG_ADDRESS_KEY}, values);
        if (!StringUtils.isEmpty(orgAddress)) {
            empType.getParameters().put(ORG_ADDRESS_KEY, orgAddress);
        }
        String orgShortName = findValueInParams(new String[] {ORG_SHORT_NAME_KEY}, values);
        if (!StringUtils.isEmpty(orgShortName)) {
            empType.getParameters().put(ORG_SHORT_NAME_KEY, orgShortName);
        }
    }

    private static final void putGenderParams(EMPEventType empType, String[] values) {
        String gender = findValueInParams(new String[]{CLIENT_GENDER_KEY}, values);
        if (null != gender) {
            empType.getParameters().put(CLIENT_GENDER_KEY, gender);
        }
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
}
