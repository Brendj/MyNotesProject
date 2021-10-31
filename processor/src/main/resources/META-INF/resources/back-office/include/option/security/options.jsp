<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра настроек ИБ --%>
<h:panelGrid id="optionsSecurityPage" binding="#{mainPage.optionsSecurityPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Период смены пароля (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.clientPeriodPasswordChange}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период блокировки повторного использования логина (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.periodBlockLoginReUse}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период неиспользования учетной записи, после чего она будет заблокирована (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.periodBlockUnusedLogin}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Максимальное число неудачных попыток ввода логина/пароля (кол-во)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.clientMaxAuthFaultCount}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период блокировки аккаунта после максимального числа неудачных попыток ввода логина/пароля (в минутах)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.clientTmpBlockAccTime}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период действия sms-кода (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityPage.periodSmsCodeAlive}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />
    </h:panelGrid>

    <h:panelGrid columns="1" styleClass="borderless-grid">
        <a4j:commandButton value="Применить" action="#{mainPage.optionsSecurityPage.save()}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />

</h:panelGrid>


