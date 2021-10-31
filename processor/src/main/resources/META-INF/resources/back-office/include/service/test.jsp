<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid">
    <h:outputText escape="true" value="Тест ЭЦП" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Публичный ключ" styleClass="output-text" />
        <h:inputText value="#{mainPage.publicSignKey}" size="64" styleClass="input-text" />
        <h:outputText escape="true" value="Закрытый ключ" styleClass="output-text" />
        <h:inputText value="#{mainPage.privateSignKey}" size="64" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Генерировать пару ключей для ЭЦП" action="#{mainPage.testSignatureKeyPairGeneration}"
                       reRender="serviceTestPanel" styleClass="command-button" />

    <%--Тест SMS --%>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Идентификатор SMS (не более 32 символов)" styleClass="output-text" />
        <h:inputText value="#{mainPage.smsMessageId}" size="32" styleClass="input-text" />
        <h:outputText escape="true" value="Номер телефона" styleClass="output-text" />
        <h:inputText value="#{mainPage.smsPhoneNumber}" size="20" styleClass="input-text" />
        <h:outputText escape="true" value="Текст SMS (не более 70 символов)" styleClass="output-text" />
        <h:inputText value="#{mainPage.smsMessageText}" size="70" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Отправить" action="#{mainPage.testSmsSend}" reRender="serviceTestPanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Проверить статус доставки" action="#{mainPage.testSmsDeliveryCheck}"
                       reRender="serviceTestPanel" styleClass="command-button" />
    <a4j:commandButton value="testConcurrentUpdates" action="#{mainPage.testConcurrentUpdates}"
                       reRender="serviceTestPanel" styleClass="command-button" />

    <%--Тест генерации SMS message ID --%>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Исходный идентификатор SMS (не более 16 символов)"
                      styleClass="output-text" />
        <h:inputText value="#{mainPage.currentSmsMessageId}" size="16" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Генерировать новый идентификатор SMS " action="#{mainPage.testSmsMessageIdGeneration2}"
                       reRender="serviceTestPanel" styleClass="command-button" />

    <%--Тест лога --%>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Текст для логирования" styleClass="output-text" />
        <h:inputText value="#{mainPage.testLogMessage}" size="128" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Записать текст в лог с уровнем ERROR" action="#{mainPage.testLogError}"
                       reRender="serviceTestPanel" styleClass="command-button" />

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>