<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра настроек ИБ --%>
<h:panelGrid id="optionsSecurityClientPage" binding="#{mainPage.optionsSecurityClientPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Период смены пароля (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.clientPeriodPasswordChange}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период блокировки повторного использования логина (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.clientPeriodBlockLoginReUse}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период неиспользования учетной записи, после чего она будет заблокирована (в днях)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.clientPeriodBlockUnusedLogin}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Максимальное число неудачных попыток ввода логина/пароля (кол-во)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.clientMaxAuthFaultCount}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Период блокировки аккаунта после максимального числа неудачных попыток ввода логина/пароля (в минутах)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.clientTmpBlockAccTime}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Время автоматического выхода из УЗ пользователя (в минутах, минимум 15 минут)" styleClass="output-text" />
        <h:inputText value="#{mainPage.optionsSecurityClientPage.userIdleTimeout}" maxlength="10"
                     styleClass="input-text" style="margin-left: 10px;" />

        <h:outputText escape="true" value="Разрешить авторизацию без ЭИ" styleClass="output-text" />
        <rich:dataTable value="#{mainPage.optionsSecurityClientPage.securityClientAuthorizationItems}" var="securityClientAuthorizationItem">
            <rich:column>
                <h:selectBooleanCheckbox value="#{securityClientAuthorizationItem.enabled}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{securityClientAuthorizationItem.optionName}" styleClass="output-text" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>

    <h:panelGrid columns="1" styleClass="borderless-grid">
        <a4j:commandButton value="Применить" action="#{mainPage.optionsSecurityClientPage.save()}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />

</h:panelGrid>


