/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.StringReader;
import java.util.Properties;
import java.util.Set;

@Component
@Scope("singleton")
public class EventNotificationService {
    Logger logger = LoggerFactory.getLogger(MaintananceService.class);

    public static String NOTIFICATION_ENTER_EVENT="enterEvent", NOTIFICATION_BALANCE_TOPUP="balanceTopup", MESSAGE_LINKING_TOKEN_GENERATED="linkingToken", MESSAGE_RESTORE_PASSWORD ="restorePassword";
    public static String TYPE_SMS="sms", TYPE_EMAIL_TEXT="email.text", TYPE_EMAIL_SUBJECT="email.subject";
    Properties notificationText;
    Boolean notifyBySMSAboutEnterEvent;

    @Resource
    SMSService smsService;

    static final String[] DEFAULT_MESSAGES={
        NOTIFICATION_ENTER_EVENT+"."+TYPE_SMS, "[eventName] [eventTime] ([surname] [firstName]). Баланс: [balance] р.",
        NOTIFICATION_ENTER_EVENT+"."+TYPE_EMAIL_TEXT, "<html>\n" + "<body>\n" + "Уважаемый клиент, <br/><br/>\n" + "\n"
                    + "[eventName] [eventTime] ([surname] [firstName]). <br/>\n"
                    + "Текущий баланс лицевого счета [balance] рублей. <br/>\n" + "<br/>\n" + "С уважением,<br/>\n"
                    + "Служба поддержки клиентов\n" + "<br/><br/>\n"
                    + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
                    + "</body>\n" + "</html>",
        NOTIFICATION_ENTER_EVENT+"."+TYPE_EMAIL_SUBJECT, "Уведомление о времени прихода и ухода",
        NOTIFICATION_BALANCE_TOPUP+"."+TYPE_SMS, "Зачислено [paySum]; баланс [balance] ([contractId] [surname] [firstName])",
        NOTIFICATION_BALANCE_TOPUP+"."+TYPE_EMAIL_TEXT, "<html>\n<body>\nУважаемый клиент, <br/><br/>\n"
                    + "\n"
                    + "на Ваш лицевой счет ([contractId] [surname] [firstName]) были зачислены средства в размере [paySum] руб.<br/>\n"
                    + "Текущий баланс лицевого счета [balance] руб.\n" + "<br/><br/>\n" + "С уважением,<br/>\n"
                    + "Служба поддержки клиентов\n" + "<br/><br/>\n"
                    + "<p style=\"color:#cccccc;font-size:xx-small;font-weight:bold\">Вы можете отключить данные уведомления в своем личном кабинете</p>\n"
                    + "</body>\n" + "</html>",
        NOTIFICATION_BALANCE_TOPUP+"."+TYPE_EMAIL_SUBJECT, "Уведомление о пополнении баланса",
        MESSAGE_RESTORE_PASSWORD +"."+TYPE_EMAIL_TEXT, "Если Вы не запрашивали восстановление пароля, пожалуйста, удалите данное письмо. Для восстановления пароля перейдите по ссылке [url]",
        MESSAGE_RESTORE_PASSWORD +"."+TYPE_EMAIL_SUBJECT, "Восстановление пароля",
        /////
        MESSAGE_LINKING_TOKEN_GENERATED+"."+TYPE_SMS, "Код активации: [linkingToken]",
        MESSAGE_LINKING_TOKEN_GENERATED+"."+TYPE_EMAIL_TEXT, "<html>\n" + "<body>\n" + "Уважаемый клиент, <br/><br/>\n" + "\n"
                    + "Код активации личного кабинета: [linkingToken]. <br/>\n"
                    + "Если Вы не запрашивали код активации, пожалуйста, удалите данное письмо. <br/>\n" + "<br/>\n" + "С уважением,<br/>\n"
                    + "Служба поддержки клиентов\n"
                    + "</body>\n" + "</html>",
        MESSAGE_LINKING_TOKEN_GENERATED+"."+TYPE_EMAIL_SUBJECT, "Код активации личного кабинета",
    };

    String getDefaultText(String name) {
        for (int n=0;n<DEFAULT_MESSAGES.length;n+=2) {
            if (name.equals(DEFAULT_MESSAGES[n])) return DEFAULT_MESSAGES[n+1];
        }
        return null;
    }

    public synchronized String getNotificationText(String message, String type) {
        String prop = message+"."+type;
        if (notificationText==null) {
            StringReader stringReader = new StringReader(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_NOTIFICATION_TEXT));
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
        if (v==null) v=getDefaultText(prop);
        return v;
    }

    public boolean sendEmail(Client client, String type, String[] values) {
        if (client.getEmail()==null || client.getEmail().length()==0) return false;
        String emailText = getNotificationText(type, TYPE_EMAIL_TEXT), emailSubject = getNotificationText(type, TYPE_EMAIL_SUBJECT);
        if (emailText==null || emailSubject==null) {
            logger.warn("No email text is specified for type '"+type+"'. Email is not sent");
            return false;
        } else {
            emailText = formatMessage(emailText, values);
            emailSubject = formatMessage(emailSubject, values);
            try {
                RuntimeContext.getInstance().getPostman().postNotificationEmail(client.getEmail(), emailSubject, emailText);
            } catch (Exception e) {
                logger.error("Failed to send email notification", e);
                return false;
            }
        }
        return true;
    }

    @Async
    public void sendNotificationAsync(Client client, String type, String[] values) {
        sendNotification(client, type, values);
    }
    @Async
    public void sendMessageAsync(Client client, String type, String[] values) {
        sendMessage(client, type, values);
    }

    public void sendNotification(Client client, String type, String[] values) {
        if (client.isNotifyViaSMS()) {
            if (isSMSNotificationEnabledForType(type)) {
                sendSMS(client, type, values);
            }
        }
        if(client.isNotifyViaEmail()){
            sendEmail(client, type, values);
        }
    }
    public boolean sendMessage(Client client, String type, String[] values) {
        boolean bSend=false;
        if (client.hasMobile()) {
            bSend|=sendSMS(client, type, values);
        }
        if (client.hasEmail()) {
            bSend|=sendEmail(client, type, values);
        }
        return bSend;
    }

    public boolean sendSMS(Client client, String type, String[] values) {
        if (client.getMobile()==null || client.getMobile().length()==0) return false;
        String text = getNotificationText(type, TYPE_SMS);
        if (text==null) {
            logger.warn("No notification SMS text is specified for type '"+type+"'. SMS is not sent");
            return false;
        }
        text = formatMessage(text, values);
        if (text.length()>68) text=text.substring(0, 67)+"..";
        try {
            int clientSMSType;
            if (type.equals(NOTIFICATION_ENTER_EVENT)) clientSMSType = ClientSms.TYPE_ENTER_EVENT_NOTIFY;
            else if (type.equals(NOTIFICATION_BALANCE_TOPUP)) clientSMSType = ClientSms.TYPE_PAYMENT_REGISTERED;
            else if (type.equals(MESSAGE_LINKING_TOKEN_GENERATED)) clientSMSType = ClientSms.TYPE_LINKING_TOKEN;
            else throw new Exception("No client SMS type defined for notification "+type);
            smsService.sendSMSAsync(client.getIdOfClient(), clientSMSType, text);
        } catch (Exception e) {
            logger.error("Failed to send SMS notification", e);
            return false;
        }
        return true;
    }

    private boolean isSMSNotificationEnabledForType(String type) {
        if (type.equals(NOTIFICATION_ENTER_EVENT)) {
            if (notifyBySMSAboutEnterEvent==null) notifyBySMSAboutEnterEvent = RuntimeContext.getInstance().getOptionValueBool(
                    Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT);
            return (notifyBySMSAboutEnterEvent!=null && notifyBySMSAboutEnterEvent);
        }
        else return true;
    }

    private String formatMessage(String text, String[] values) {
        text = text.replaceAll("\\[br\\]", "\n");
        for (int n=0;n<values.length;n+=2) {
            String s ="["+values[n]+"]";
            int pos = text.indexOf(s);
            if (pos!=-1) text = text.substring(0, pos)+values[n+1]+text.substring(pos+s.length());
        }
        return text;
    }

    public void updateMessageTemplates(String[] values) {
        Properties properties = new Properties();
        StringBuilder stringBuilder = new StringBuilder();
        for (int n=0;n<values.length;n+=3) {
            String value=values[n+2];
            value=value.replaceAll("\\n","[br]");
            value=value.replaceAll("\\r","");
            String propName=values[n]+"."+values[n+1];
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
