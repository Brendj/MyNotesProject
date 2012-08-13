/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    private String passwordRestoreEmailSubject;
    private String passwordRestoreEmailMessageText;

    private String linkingTokenEmailSubject;
    private String linkingTokenEmailMessageText;
    private String linkingTokenSMSMessageText;

    @Resource
    EventNotificationService eventNotificationService;

    public String getPageFilename() {
        return "option/message_configure";
    }

    /* Page action */
    @Override
    public void onShow() throws Exception {
        balanceEmailMessageText = eventNotificationService.getNotificationText(EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_EMAIL_TEXT)
                .replaceAll("\\[br\\]","\n");
        balanceEmailSubject = eventNotificationService.getNotificationText(
                EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_EMAIL_SUBJECT);
        balanceSMSMessageText = eventNotificationService.getNotificationText(
                EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_SMS);
        enterEventEmailMessageText = eventNotificationService.getNotificationText(EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_EMAIL_TEXT)
                .replaceAll("\\[br\\]", "\n");
        enterEventEmailSubject = eventNotificationService.getNotificationText(
                EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_EMAIL_SUBJECT);
        enterEventSMSMessageText = eventNotificationService.getNotificationText(EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_SMS);
        passwordRestoreEmailMessageText = eventNotificationService.getNotificationText(EventNotificationService.MESSAGE_RESTORE_PASSWORD, EventNotificationService.TYPE_EMAIL_TEXT);
        passwordRestoreEmailSubject = eventNotificationService.getNotificationText(EventNotificationService.MESSAGE_RESTORE_PASSWORD, EventNotificationService.TYPE_EMAIL_SUBJECT);
        linkingTokenEmailMessageText = eventNotificationService.getNotificationText(EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_EMAIL_TEXT)
                .replaceAll("\\[br\\]","\n");
        linkingTokenEmailSubject = eventNotificationService.getNotificationText(
                EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_EMAIL_SUBJECT);
        linkingTokenSMSMessageText = eventNotificationService.getNotificationText(
                EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_SMS);
    }

    public Object save() throws Exception {
        try {
            eventNotificationService.updateMessageTemplates(new String[]{
                    EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_EMAIL_TEXT,
                    enterEventEmailMessageText,
                    EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    enterEventEmailSubject,
                    EventNotificationService.NOTIFICATION_ENTER_EVENT, EventNotificationService.TYPE_SMS,
                    enterEventSMSMessageText,
                    ////
                    EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_EMAIL_TEXT,
                    balanceEmailMessageText,
                    EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    balanceEmailSubject,
                    EventNotificationService.NOTIFICATION_BALANCE_TOPUP, EventNotificationService.TYPE_SMS,
                    balanceSMSMessageText,
                    /////
                    EventNotificationService.MESSAGE_RESTORE_PASSWORD, EventNotificationService.TYPE_EMAIL_TEXT,
                    passwordRestoreEmailMessageText,
                    EventNotificationService.MESSAGE_RESTORE_PASSWORD, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    passwordRestoreEmailSubject,
                    ////
                    EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_EMAIL_TEXT,
                    linkingTokenEmailMessageText,
                    EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    linkingTokenEmailSubject,
                    EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED, EventNotificationService.TYPE_SMS,
                    linkingTokenSMSMessageText});

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

    public String getPasswordRestoreEmailSubject() {
        return passwordRestoreEmailSubject;
    }

    public void setPasswordRestoreEmailSubject(String passwordRestoreEmailSubject) {
        this.passwordRestoreEmailSubject = passwordRestoreEmailSubject;
    }

    public String getPasswordRestoreEmailMessageText() {
        return passwordRestoreEmailMessageText;
    }

    public void setPasswordRestoreEmailMessageText(String passwordRestoreEmailMessageText) {
        this.passwordRestoreEmailMessageText = passwordRestoreEmailMessageText;
    }

    public String getLinkingTokenEmailSubject() {
        return linkingTokenEmailSubject;
    }

    public void setLinkingTokenEmailSubject(String linkingTokenEmailSubject) {
        this.linkingTokenEmailSubject = linkingTokenEmailSubject;
    }

    public String getLinkingTokenEmailMessageText() {
        return linkingTokenEmailMessageText;
    }

    public void setLinkingTokenEmailMessageText(String linkingTokenEmailMessageText) {
        this.linkingTokenEmailMessageText = linkingTokenEmailMessageText;
    }

    public String getLinkingTokenSMSMessageText() {
        return linkingTokenSMSMessageText;
    }

    public void setLinkingTokenSMSMessageText(String linkingTokenSMSMessageText) {
        this.linkingTokenSMSMessageText = linkingTokenSMSMessageText;
    }
}
