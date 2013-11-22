<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

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
    <h:outputText escape="true" value="Адрес электронной почты" styleClass="output-text" />
    <h:inputText value="#{userSettings.email}" maxlength="128" styleClass="input-text"/>

</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{userSettings.save}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{userSettings.restore}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>