/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.04.12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
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

    private String paymentEmailSubject;
    private String paymentEmailMessageText;
    private String paymentSMSMessageText;

    private String notificationSubscriptionFeedingSMSText;
    private String notificationSubscriptionFeedingNotSuccessSMSText;
    private String notificationSubscriptionFeedingSubject;
    private String notificationSubscriptionFeedingEmailMessageText;

    private String smsSubscriptionFeeSMSText;
    private String smsSubFeeWithdrawSuccessfulSMSText;
    private String smsSubFeeWithdrawNotSuccessfulSMSText;

    private String passWithGuardianSMSMessageText;
    private String passWithGuardianEmailMessageText;
    private String passWithGuardianEmailSubject;

    private String goodRequestChangeEmailMessageText;
    private String goodRequestChangeEmailSubject;
    private String notificationSubscriptionFeedingWithdrawNotSuccessSubject;
    private String notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText;

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

        paymentEmailSubject = eventNotificationService.getNotificationText(
                EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_EMAIL_SUBJECT);
        paymentEmailMessageText = eventNotificationService.getNotificationText(
                EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_EMAIL_TEXT);
        paymentSMSMessageText = eventNotificationService.getNotificationText(
                EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_SMS);
        notificationSubscriptionFeedingSMSText = eventNotificationService.
                getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, EventNotificationService.TYPE_SMS);
        notificationSubscriptionFeedingNotSuccessSMSText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS, EventNotificationService.TYPE_SMS);
        notificationSubscriptionFeedingSubject = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, EventNotificationService.TYPE_EMAIL_SUBJECT);
        notificationSubscriptionFeedingEmailMessageText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING,EventNotificationService.TYPE_EMAIL_TEXT);
        smsSubscriptionFeeSMSText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SMS_SUBSCRIPTION_FEE,
                        EventNotificationService.TYPE_SMS);
        smsSubFeeWithdrawSuccessfulSMSText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS,
                        EventNotificationService.TYPE_SMS);
        smsSubFeeWithdrawNotSuccessfulSMSText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS,
                        EventNotificationService.TYPE_SMS);
        passWithGuardianSMSMessageText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN,
                        EventNotificationService.TYPE_SMS);
        passWithGuardianEmailMessageText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN,
                        EventNotificationService.TYPE_EMAIL_TEXT);
        passWithGuardianEmailSubject = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN,
                        EventNotificationService.TYPE_EMAIL_SUBJECT);

        goodRequestChangeEmailSubject = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE,
                        EventNotificationService.TYPE_EMAIL_SUBJECT);
        goodRequestChangeEmailMessageText = eventNotificationService
                .getNotificationText(EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE,
                        EventNotificationService.TYPE_EMAIL_TEXT);
        notificationSubscriptionFeedingWithdrawNotSuccessSubject = eventNotificationService.getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS,
                EventNotificationService.TYPE_EMAIL_SUBJECT);
        notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText = eventNotificationService.getNotificationText(EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS,
                EventNotificationService.TYPE_EMAIL_TEXT);
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
                    linkingTokenSMSMessageText,
                    ////
                    EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    paymentEmailSubject,
                    EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_EMAIL_TEXT,
                    paymentEmailMessageText,
                    EventNotificationService.MESSAGE_PAYMENT, EventNotificationService.TYPE_SMS,
                    paymentSMSMessageText,
                    ////
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, EventNotificationService.TYPE_SMS,
                    notificationSubscriptionFeedingSMSText,
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS, EventNotificationService.TYPE_SMS,
                    notificationSubscriptionFeedingNotSuccessSMSText,
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    notificationSubscriptionFeedingSubject,
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING,EventNotificationService.TYPE_EMAIL_TEXT,
                    notificationSubscriptionFeedingEmailMessageText,
                    EventNotificationService.NOTIFICATION_SMS_SUBSCRIPTION_FEE, EventNotificationService.TYPE_SMS,
                    smsSubscriptionFeeSMSText,
                    EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS,
                    EventNotificationService.TYPE_SMS, smsSubFeeWithdrawSuccessfulSMSText,
                    EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS,
                    EventNotificationService.TYPE_SMS, smsSubFeeWithdrawNotSuccessfulSMSText,
                    ////
                    EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, EventNotificationService.TYPE_EMAIL_TEXT,
                    passWithGuardianEmailMessageText,
                    EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    passWithGuardianEmailSubject,
                    EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, EventNotificationService.TYPE_SMS,
                    passWithGuardianSMSMessageText,
                    ////
                    EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, EventNotificationService.TYPE_EMAIL_TEXT,
                    goodRequestChangeEmailMessageText,
                    EventNotificationService.NOTIFICATION_GOOD_REQUEST_CHANGE, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    goodRequestChangeEmailSubject,
                    ////
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS, EventNotificationService.TYPE_EMAIL_SUBJECT,
                    notificationSubscriptionFeedingWithdrawNotSuccessSubject,
                    EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS, EventNotificationService.TYPE_EMAIL_TEXT,
                    notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText
            });

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

    public String getPaymentEmailSubject() {
        return paymentEmailSubject;
    }

    public void setPaymentEmailSubject(String paymentEmailSubject) {
        this.paymentEmailSubject = paymentEmailSubject;
    }

    public String getPaymentEmailMessageText() {
        return paymentEmailMessageText;
    }

    public void setPaymentEmailMessageText(String paymentEmailMessageText) {
        this.paymentEmailMessageText = paymentEmailMessageText;
    }

    public String getPaymentSMSMessageText() {
        return paymentSMSMessageText;
    }

    public void setPaymentSMSMessageText(String paymentSMSMessageText) {
        this.paymentSMSMessageText = paymentSMSMessageText;
    }

    public String getNotificationSubscriptionFeedingSMSText() {
        return notificationSubscriptionFeedingSMSText;
    }

    public void setNotificationSubscriptionFeedingSMSText(String notificationSubscriptionFeedingSMSText) {
        this.notificationSubscriptionFeedingSMSText = notificationSubscriptionFeedingSMSText;
    }

    public String getNotificationSubscriptionFeedingNotSuccessSMSText() {
        return notificationSubscriptionFeedingNotSuccessSMSText;
    }

    public void setNotificationSubscriptionFeedingNotSuccessSMSText(
            String notificationSubscriptionFeedingNotSuccessSMSText) {
        this.notificationSubscriptionFeedingNotSuccessSMSText = notificationSubscriptionFeedingNotSuccessSMSText;
    }

    public String getNotificationSubscriptionFeedingSubject() {
        return notificationSubscriptionFeedingSubject;
    }

    public void setNotificationSubscriptionFeedingSubject(String notificationSubscriptionFeedingSubject) {
        this.notificationSubscriptionFeedingSubject = notificationSubscriptionFeedingSubject;
    }

    public String getNotificationSubscriptionFeedingEmailMessageText() {
        return notificationSubscriptionFeedingEmailMessageText;
    }

    public void setNotificationSubscriptionFeedingEmailMessageText(
            String notificationSubscriptionFeedingEmailMessageText) {
        this.notificationSubscriptionFeedingEmailMessageText = notificationSubscriptionFeedingEmailMessageText;
    }

    public String getNotificationSubscriptionFeedingWithdrawNotSuccessSubject() {
        return notificationSubscriptionFeedingWithdrawNotSuccessSubject;
    }

    public void setNotificationSubscriptionFeedingWithdrawNotSuccessSubject(
            String notificationSubscriptionFeedingWithdrawNotSuccessSubject) {
        this.notificationSubscriptionFeedingWithdrawNotSuccessSubject = notificationSubscriptionFeedingWithdrawNotSuccessSubject;
    }

    public String getNotificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText() {
        return notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText;
    }

    public void setNotificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText(
            String notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText) {
        this.notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText = notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText;
    }

    public String getSmsSubscriptionFeeSMSText() {
        return smsSubscriptionFeeSMSText;
    }

    public void setSmsSubscriptionFeeSMSText(String smsSubscriptionFeeSMSText) {
        this.smsSubscriptionFeeSMSText = smsSubscriptionFeeSMSText;
    }

    public String getSmsSubFeeWithdrawSuccessfulSMSText() {
        return smsSubFeeWithdrawSuccessfulSMSText;
    }

    public void setSmsSubFeeWithdrawSuccessfulSMSText(String smsSubFeeWithdrawSuccessfulSMSText) {
        this.smsSubFeeWithdrawSuccessfulSMSText = smsSubFeeWithdrawSuccessfulSMSText;
    }

    public String getSmsSubFeeWithdrawNotSuccessfulSMSText() {
        return smsSubFeeWithdrawNotSuccessfulSMSText;
    }

    public void setSmsSubFeeWithdrawNotSuccessfulSMSText(String smsSubFeeWithdrawNotSuccessfulSMSText) {
        this.smsSubFeeWithdrawNotSuccessfulSMSText = smsSubFeeWithdrawNotSuccessfulSMSText;
    }

    public String getPassWithGuardianSMSMessageText() {
        return passWithGuardianSMSMessageText;
    }

    public void setPassWithGuardianSMSMessageText(String passWithGuardianSMSMessageText) {
        this.passWithGuardianSMSMessageText = passWithGuardianSMSMessageText;
    }

    public String getPassWithGuardianEmailMessageText() {
        return passWithGuardianEmailMessageText;
    }

    public void setPassWithGuardianEmailMessageText(String passWithGuardianEmailMessageText) {
        this.passWithGuardianEmailMessageText = passWithGuardianEmailMessageText;
    }

    public String getPassWithGuardianEmailSubject() {
        return passWithGuardianEmailSubject;
    }

    public void setPassWithGuardianEmailSubject(String passWithGuardianEmailSubject) {
        this.passWithGuardianEmailSubject = passWithGuardianEmailSubject;
    }

    public String getGoodRequestChangeEmailMessageText() {
        return goodRequestChangeEmailMessageText;
    }

    public void setGoodRequestChangeEmailMessageText(String goodRequestChangeEmailMessageText) {
        this.goodRequestChangeEmailMessageText = goodRequestChangeEmailMessageText;
    }

    public String getGoodRequestChangeEmailSubject() {
        return goodRequestChangeEmailSubject;
    }

    public void setGoodRequestChangeEmailSubject(String goodRequestChangeEmailSubject) {
        this.goodRequestChangeEmailSubject = goodRequestChangeEmailSubject;
    }
}
