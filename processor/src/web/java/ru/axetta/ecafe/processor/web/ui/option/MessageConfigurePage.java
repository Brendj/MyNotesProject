/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.04.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MessageConfigurePage extends BasicWorkspacePage {

    private String balanceEmailSubject;
    private String balanceEmailMessageText;
    private String balanceSMSMessageText;

    private String enterEventEmailSubject;
    private String enterEventEmailMessageText;
    private String enterEventSMSMessageText;

    private Properties messageProperties;

    @Autowired
    RuntimeContext runtimeContext;

    public String getPageFilename() {
        return "option/message_configure";
    }

    /* Page action */
    @Override
    public void onShow() throws Exception {
        messageProperties = new Properties();
        StringReader stringReader = new StringReader(runtimeContext.getOptionValueString(Option.OPTION_EMAIL_TEXT));
        Properties properties = new Properties();
        properties.load(stringReader);
        balanceEmailMessageText = properties.getProperty("ecafe.processor.email.service.balanceMessageText")
                .replaceAll("\\[br\\]","\n");
        balanceEmailSubject = properties.getProperty("ecafe.processor.email.service.balanceSubject");
        balanceSMSMessageText = properties.getProperty("ecafe.processor.sms.service.balanceMessageText");
        enterEventEmailMessageText = properties.getProperty("ecafe.processor.email.service.enterEventMessageText")
                .replaceAll("\\[br\\]","\n");
        enterEventEmailSubject = properties.getProperty("ecafe.processor.email.service.enterEventSubject");
        enterEventSMSMessageText = properties.getProperty("ecafe.processor.sms.service.enterEventMessageText");
    }

    public Object save() throws Exception {
        try {
            Properties properties = new Properties();
            balanceEmailMessageText=balanceEmailMessageText.replaceAll("\\n","[br]");
            balanceEmailMessageText=balanceEmailMessageText.replaceAll("\\r","");
            properties.setProperty("ecafe.processor.email.service.balanceMessageText",balanceEmailMessageText);
            properties.setProperty("ecafe.processor.email.service.balanceSubject",balanceEmailSubject);
            balanceSMSMessageText=balanceSMSMessageText.replaceAll("\\n","");
            balanceSMSMessageText=balanceSMSMessageText.replaceAll("\\r","");
            properties.setProperty("ecafe.processor.sms.service.balanceMessageText",balanceSMSMessageText);

            enterEventEmailMessageText=enterEventEmailMessageText.replaceAll("\\n","[br]");
            enterEventEmailMessageText=enterEventEmailMessageText.replaceAll("\\r","");
            properties.setProperty("ecafe.processor.email.service.enterEventMessageText",enterEventEmailMessageText);
            properties.setProperty("ecafe.processor.email.service.enterEventSubject",enterEventEmailSubject);
            enterEventSMSMessageText=enterEventSMSMessageText.replaceAll("\\n","");
            enterEventSMSMessageText=enterEventSMSMessageText.replaceAll("\\r","");
            properties.setProperty("ecafe.processor.sms.service.enterEventMessageText",enterEventSMSMessageText);

            StringBuilder stringBuilder = new StringBuilder();
            Set stringSet = properties.keySet();
            for (Object key: stringSet){
                String sKey = (String) key;
                stringBuilder.append(sKey);
                stringBuilder.append("=");
                String sValue = properties.getProperty(sKey);
                stringBuilder.append(sValue);
                stringBuilder.append("\r\n");
            }
            runtimeContext.setOptionValue(Option.OPTION_EMAIL_TEXT, stringBuilder.toString());
            runtimeContext.saveOptionValues();
            printMessage("Настройки сохранены.");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при сохранении", e);
        }
        onShow();
        return null;
    }

    public Object cancel() throws Exception {
        onShow();
        printMessage("Настройки отменены.");
        return null;
    }
    /* Getter and Setters */
    public String getBalanceSMSMessageText() {
        return balanceSMSMessageText;
    }

    public void setBalanceSMSMessageText(String balanceSMSMessageText) {
        this.balanceSMSMessageText = balanceSMSMessageText;
    }

    public String getEnterEventEmailSubject() {
        return enterEventEmailSubject;
    }

    public void setEnterEventEmailSubject(String enterEventEmailSubject) {
        this.enterEventEmailSubject = enterEventEmailSubject;
    }

    public String getEnterEventEmailMessageText() {
        return enterEventEmailMessageText;
    }

    public void setEnterEventEmailMessageText(String enterEventEmailMessageText) {
        this.enterEventEmailMessageText = enterEventEmailMessageText;
    }

    public String getEnterEventSMSMessageText() {
        return enterEventSMSMessageText;
    }

    public void setEnterEventSMSMessageText(String enterEventSMSMessageText) {
        this.enterEventSMSMessageText = enterEventSMSMessageText;
    }

    public String getBalanceEmailMessageText() {
        return balanceEmailMessageText;
    }

    public void setBalanceEmailMessageText(String balanceEmailMessageText) {
        this.balanceEmailMessageText = balanceEmailMessageText;
    }

    public String getBalanceEmailSubject() {
        return balanceEmailSubject;
    }

    public void setBalanceEmailSubject(String balanceEmailSubject) {
        this.balanceEmailSubject = balanceEmailSubject;
    }
}
