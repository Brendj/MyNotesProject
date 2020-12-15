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

    <h:outputText escape="true" value="Общие настройки" styleClass="output-text-strong" />
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
    </h:panelGrid>

    <rich:separator height="1" />
        <h:outputText escape="true" value="Модуль \"АРМ Администратора ОУ\"" styleClass="output-text-strong" />
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <h:selectBooleanCheckbox value="#{mainPage.optionsSecurityClientPage.armAdminAuthWithoutCardForAdmin}" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить авторизацию без ЭИ для роли \"Администратор\" (созданной по умолчанию) и роли \"Администратор CКУД\"" styleClass="output-text" />
            <h:outputText escape="true" value="" styleClass="output-text" />

            <h:selectBooleanCheckbox value="#{mainPage.optionsSecurityClientPage.armAdminAuthWithoutCardForOther}" label="" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить авторизацию без ЭИ для остальных ролей" styleClass="output-text" />
            <h:outputText escape="true" value="" styleClass="output-text" />

            &nbsp;
            <h:outputText escape="true" value="Время автоматического выхода из УЗ пользователя при бездействии (в минутах)" styleClass="output-text" />
            <h:inputText value="#{mainPage.optionsSecurityClientPage.armAdminUserIdleTimeout}" maxlength="10"
                         styleClass="input-text" style="margin-left: 10px;" />
        </h:panelGrid>
        <rich:separator height="1" />
        <h:outputText escape="true" value="Модуль \"АРМ оператора питания (кассира)\"" styleClass="output-text-strong" />
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <h:selectBooleanCheckbox value="#{mainPage.optionsSecurityClientPage.armCashierAuthWithoutCard}" label="" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить авторизацию без ЭИ для всех ролей" styleClass="output-text" />
            <h:outputText escape="true" value="" styleClass="output-text" />

            &nbsp;
            <h:outputText escape="true" value="Время автоматического выхода из УЗ пользователя при бездействии (в минутах)" styleClass="output-text" />
            <h:inputText value="#{mainPage.optionsSecurityClientPage.armCashierUserIdleTimeout}" maxlength="10"
                         styleClass="input-text" style="margin-left: 10px;" />
        </h:panelGrid>
        <rich:separator height="1" />
        <h:outputText escape="true" value="Модуль \"АРМ контролёра входа (охранника)\"" styleClass="output-text-strong" />
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <h:selectBooleanCheckbox value="#{mainPage.optionsSecurityClientPage.armSecurityAuthWithoutCard}" label="" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить авторизацию без ЭИ для всех ролей" styleClass="output-text" />
            <h:outputText escape="true" value="" styleClass="output-text" />

            &nbsp;
            <h:outputText escape="true" value="Время автоматического выхода из УЗ пользователя при бездействии (в минутах)" styleClass="output-text" />
            <h:inputText value="#{mainPage.optionsSecurityClientPage.armSecurityUserIdleTimeout}" maxlength="10"
                         styleClass="input-text" style="margin-left: 10px;" />
        </h:panelGrid>
        <rich:separator height="1" />
        <h:outputText escape="true" value="Модуль \"АРМ библиотекаря\"" styleClass="output-text-strong" />
        <h:panelGrid columns="3" styleClass="borderless-grid">
            <h:selectBooleanCheckbox value="#{mainPage.optionsSecurityClientPage.armLibraryAuthWithoutCard}" label="" styleClass="output-text" />
            <h:outputText escape="true" value="Разрешить авторизацию без ЭИ для всех ролей" styleClass="output-text" />
            <h:outputText escape="true" value="" styleClass="output-text" />

            &nbsp;
            <h:outputText escape="true" value="Время автоматического выхода из УЗ пользователя при бездействии (в минутах)" styleClass="output-text" />
            <h:inputText value="#{mainPage.optionsSecurityClientPage.armLibraryUserIdleTimeout}" maxlength="10"
                         styleClass="input-text" style="margin-left: 10px;" />
        </h:panelGrid>


    <h:panelGrid columns="1" styleClass="borderless-grid">
        <a4j:commandButton value="Применить" action="#{mainPage.optionsSecurityClientPage.save()}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />

</h:panelGrid>


