<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="messageConfigurePage" type="ru.axetta.ecafe.processor.web.ui.option.MessageConfigurePage"--%>
<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditUsers())
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
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

    <h:panelGroup style="margin-top: 10px">
        <a4j:commandButton value="Сохранить" action="#{messageConfigurePage.save}"
                           reRender="mainMenu, workspaceTogglePanel, messageConfigurePanelGrid"
                           styleClass="command-button"/>
        <a4j:commandButton value="Отмена" action="#{messageConfigurePage.cancel}"
                           reRender="mainMenu, workspaceTogglePanel, messageConfigurePanelGrid"
                           styleClass="command-button" />
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>