<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="userSettings" type="ru.axetta.ecafe.processor.web.ui.UserSettings"--%>

<h:panelGrid id="userSettingsPanelGrid" binding="#{userSettings.pageComponent}" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Имя пользователя" styleClass="output-text" />
    <h:inputText value="#{userSettings.userName}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Текущий пароль" styleClass="output-text" />
    <h:inputSecret value="#{userSettings.currPlainPassword}" maxlength="64" styleClass="input-text" />
    <h:outputText escape="true" value="Сменить пароль" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{userSettings.changePassword}" styleClass="output-text">
        <a4j:support event="onclick" reRender="userSettingsPanelGrid" ajaxSingle="true" />
    </h:selectBooleanCheckbox>
    <h:outputText escape="true" value="Новый пароль" rendered="#{userSettings.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret value="#{userSettings.plainPassword}" maxlength="64"
                   rendered="#{userSettings.changePassword}"
                   readonly="#{!userSettings.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Подтверждение" rendered="#{userSettings.changePassword}"
                  styleClass="output-text" />
    <h:inputSecret id="userSettingsPasswordConfirmation" value="#{userSettings.plainPasswordConfirmation}"
                   maxlength="64" rendered="#{userSettings.changePassword}"
                   readonly="#{!userSettings.changePassword}" styleClass="input-text" />
    <h:outputText escape="true" value="Контактный телефон" styleClass="output-text" />
    <h:inputText value="#{userSettings.phone}" maxlength="32" styleClass="input-text"
                 converter="phoneConverter" />
    <h:outputText escape="true" value="Адрес электронной почты" styleClass="output-text" rendered="#{!userSettings.userIsCardOperator()}" />
    <h:inputText value="#{userSettings.email}" maxlength="128" styleClass="input-text" rendered="#{!userSettings.userIsCardOperator()}"/>
    <h:outputText escape="true" value="Список организаций рассылки сводного отчета по заявкам" styleClass="output-text"
                  rendered="#{!userSettings.userIsSecurityAdmin() && !userSettings.userIsCardOperator()}"/>
    <h:panelGroup rendered="#{!userSettings.userIsSecurityAdmin() && !userSettings.userIsCardOperator()}">
        <a4j:commandButton value="..." action="#{userSettings.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
            <f:setPropertyActionListener value="0" target="#{userSettings.selectOrgType}"/>
            <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
            <f:setPropertyActionListener value="#{userSettings.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{userSettings.orgFilter}}" />
    </h:panelGroup>
    <h:outputText escape="true" value="Список организаций рассылки по отмененным заказам" styleClass="output-text"
                  rendered="#{!userSettings.userIsSecurityAdmin() && !userSettings.userIsCardOperator()}"/>
    <h:panelGroup rendered="#{!userSettings.userIsSecurityAdmin() && !userSettings.userIsCardOperator()}">
        <a4j:commandButton value="..." action="#{userSettings.showOrgListSelectCancelPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px">
            <f:setPropertyActionListener value="1" target="#{userSettings.selectOrgType}"/>
            <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
            <f:setPropertyActionListener value="#{userSettings.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
        </a4j:commandButton>
        <h:outputText styleClass="output-text" escape="true" value=" {#{userSettings.orgFilterCanceled}}" />
    </h:panelGroup>

</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{userSettings.save}" reRender="workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{userSettings.restore}"
                       reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>