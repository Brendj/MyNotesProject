<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

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
                    <h:outputText value="[contractId] - номер договора клиента" styleClass="output-text" />
                    <h:outputText value="[surName] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="SMS уведомления о времени прихода и ухода ребенка" id="enterEvent-SMS">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="10" cols="40" value="#{messageConfigurePage.enterEventSMSMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер контракта клиента" styleClass="output-text" />
                    <h:outputText value="[surName] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - событие прихода или ухода ребенка" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время прихода или ухода ребенка" styleClass="output-text" />
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
                    <h:outputText value="[contractId] - номер контракта клиента" styleClass="output-text" />
                    <h:outputText value="[surName] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                </h:panelGrid>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="E-mail уведомления о времени прихода и ухода ребенка" id="enterEvent-Email">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Тема:" styleClass="output-text" />
                <h:inputText value="#{messageConfigurePage.enterEventEmailSubject}" size="80" maxlength="128" styleClass="input-text" />
                <h:outputText escape="true" value="Текст:" styleClass="output-text" />
                <h:inputTextarea rows="15" cols="80" value="#{messageConfigurePage.enterEventEmailMessageText}" styleClass="input-text" />
                <h:outputText escape="true" value="Ключевые слова:" styleClass="output-text" />
                <h:panelGrid>
                    <h:outputText value="[balance] - текущий баланс лицевого счета" styleClass="output-text" />
                    <h:outputText value="[contractId] - номер контракта клиента" styleClass="output-text" />
                    <h:outputText value="[surName] - фамилия клиента" styleClass="output-text" />
                    <h:outputText value="[firstName] - имя клиента" styleClass="output-text" />
                    <h:outputText value="[eventName] - событие прихода или ухода ребенка" styleClass="output-text" />
                    <h:outputText value="[eventTime] - время прихода или ухода ребенка" styleClass="output-text" />
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