<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="messageConfigurePage" type="ru.axetta.ecafe.processor.web.ui.option.MessageConfigurePage"--%>
<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers() &&
        !ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isAdmin())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<h:panelGrid id="messageConfigurePanelGrid" binding="#{messageConfigurePage.pageComponent}" styleClass="borderless-grid">
    <rich:tabPanel>
        <rich:tab label="SMS уведомления о пополнении баланса" id="balance-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.balanceSMSMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[paySum] - размер зачисленных средств" styleClass="output-text" />
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомления о посещении" id="enterEvent-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.enterEventSMSMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - название события" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время события" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомление с кодом активации" id="linkingToken-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.linkingTokenSMSMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[linkingToken] - код активации" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомление о списании средств" id="payment-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.paymentSMSMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[date] - дата оповещения" styleClass="output-text" />
                    <h:outputText value="[contractId] - дата оповещения" styleClass="output-text" />
                    <h:outputText value="[others] - суммы оплаты не комплексного питания" styleClass="output-text" />
                    <h:outputText value="[complexes] - суммы оплаты комплексного питания" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомление о списании абон. платы за SMS-сервис" id="smsSubFeeWithdraw-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст уведомляющего сообщения:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.smsSubscriptionFeeSMSText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Текст (успешное списание):" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.smsSubFeeWithdrawSuccessfulSMSText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Текст (неудачное списание):" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40"
                                 value="#{messageConfigurePage.smsSubFeeWithdrawNotSuccessfulSMSText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[date] - дата оповещения" styleClass="output-text" />
                    <h:outputText value="[smsSubscriptionFee] - размер абонентской платы за SMS-сервис"
                                  styleClass="output-text" />
                    <h:outputText value="[withdrawDate] - дата списания абон. платы" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомление о посещении с представителем" id="passWithGuardian-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.passWithGuardianSMSMessageText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - название события" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время события" styleClass="output-text" />
                    <h:outputText value="[guardian] - фамилия и имя представителя" styleClass="output-text"/>
                    <h:outputText value="[childPassCheckerMark] - признак того, кто сделал отметку о проходе за представителя" styleClass="output-text"/>
                    <h:outputText value="[childPassCheckerName] - фамилия и имя того, кто сделал отметку о проходе за представителя" styleClass="output-text"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS-уведомление сервис АП" id="ap-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст уведомляющего сообщения:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.notificationSubscriptionFeedingSMSText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Текст (неудачное списание):" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.notificationSubscriptionFeedingNotSuccessSMSText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[withdrawDate] - дата списания абон. платы" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомление о состоянии субсчета абонентского питания" id="stateSubaccounts-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.notificationSubscriptionFeedingSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea id="stateSubaccounts-Email-text" rows="15" cols="80" value="#{messageConfigurePage.notificationSubscriptionFeedingEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[withdrawDate] - дата списания абон. платы" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомления о пополнении баланса" id="balance-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.balanceEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea id="balance-Email-text" rows="15" cols="80" value="#{messageConfigurePage.balanceEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[paySum] - размер зачисленных средств" styleClass="output-text" />
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомления о посещении">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.enterEventEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="15" cols="80" value="#{messageConfigurePage.enterEventEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - название события" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время события" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомления с кодом активации" id="linkingToken-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.linkingTokenEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea id="linkingToken-Email-text" rows="15" cols="80" value="#{messageConfigurePage.linkingTokenEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[linkingToken] - код активации" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail сброса пароля">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.passwordRestoreEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea id="passwordRestore-Email-text" rows="15" cols="80" value="#{messageConfigurePage.passwordRestoreEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[url] - URL для сброса пароля" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомление о списании средств" id="payment-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.paymentEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea id="payment-Email-text" rows="15" cols="80" value="#{messageConfigurePage.paymentEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[date] - дата оповещения" styleClass="output-text" />
                    <h:outputText value="[contractId] - дата оповещения" styleClass="output-text" />
                    <h:outputText value="[others] - суммы оплаты не комплексного питания" styleClass="output-text" />
                    <h:outputText value="[complexes] - суммы оплаты комплексного питания" styleClass="output-text" />
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомление о посещении с представителем" id="passWithGuardian-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.passWithGuardianEmailSubject}" size="80" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="15" cols="80" value="#{messageConfigurePage.passWithGuardianEmailMessageText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                    <h:outputText value="[surname] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - название события" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время события" styleClass="output-text" />
                    <h:outputText value="[guardian] - фамилия и имя представителя" styleClass="output-text" />
                    <h:outputText value="[childPassCheckerMark] - признак того, кто сделал отметку о проходе за представителя" styleClass="output-text"/>
                    <h:outputText value="[childPassCheckerName] - фамилия и имя того, кто сделал отметку о проходе за представителя" styleClass="output-text"/>
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомление об изменении заявки" id="goodRequestChange-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.goodRequestChangeEmailSubject}" size="80" maxlength="128"
                             styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="15" cols="80" value="#{messageConfigurePage.goodRequestChangeEmailMessageText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[shortOrgName] - Короткое наименование организации" styleClass="output-text" />
                    <h:outputText value="[address] - адресс организации" styleClass="output-text" />
                    <h:outputText value="[reportValues] - таблица сводного отчета по заявкам" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомление о состоянии подписки абонентского питания" id="subscriptionState-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.notificationSubscriptionFeedingWithdrawNotSuccessSubject}"
                             size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="15" cols="80"
                                 value="#{messageConfigurePage.notificationSubscriptionFeedingWithdrawNotSuccessEmailMessageText}"
                                 styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[contractId] - номер лицевого счета" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

    <h:panelGroup style="margin-top: 10px">
        <a4j:commandButton value="Сохранить" action="#{messageConfigurePage.save}"
                           reRender="workspaceTogglePanel, messageConfigurePanelGrid"
                           styleClass="command-button"/>
        <a4j:commandButton value="Отмена" action="#{messageConfigurePage.cancel}"
                           reRender="workspaceTogglePanel, messageConfigurePanelGrid"
                           styleClass="command-button" />
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <%--Скрипт по выраниванию высоты заголовков у табов.--%>
    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("table.rich-tabpanel").each(function () {
                var tabPanelContainer = jQuery('table:first', this);
                // Locate the tallest TD
                var max_height = 0;
                jQuery("td.rich-tabhdr-side-cell", tabPanelContainer).each(function () {
                    var td_height = jQuery(this).height();
                    if (max_height < td_height) {
                        max_height = td_height;
                    }
                });
                // Set explicit heights on all other TDs
                if (max_height > 0) {
                    jQuery("td.rich-tabhdr-side-cell", tabPanelContainer).each(function () {
                        jQuery(this).height(max_height);
                    });
                }
            });
        });
    </script>
</h:panelGrid>