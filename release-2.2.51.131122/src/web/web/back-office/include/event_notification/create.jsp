<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditReports()) 
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания уведомления о событии --%>
<h:panelGrid id="eventNotificationCreateGrid" binding="#{mainPage.eventNotificationCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Название" styleClass="output-text" />
    <h:inputText value="#{mainPage.eventNotificationCreatePage.notificationName}" maxlength="64"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Включено" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.eventNotificationCreatePage.enabled}" styleClass="output-text" />
    <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.eventNotificationCreatePage.eventType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.eventNotificationCreatePage.eventTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Формат отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.eventNotificationCreatePage.documentFormat}" styleClass="input-text">
        <f:selectItems value="#{mainPage.eventNotificationCreatePage.reportFormatMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тема письма" styleClass="output-text" />
    <h:inputText value="#{mainPage.eventNotificationCreatePage.subject}" maxlength="128" style="width: 600px;"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Адреса рассылки" styleClass="output-text" />
    <h:inputText value="#{mainPage.eventNotificationCreatePage.routeAddresses}" maxlength="1024" style="width: 600px;"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Условия применения правила" styleClass="output-text" />
    <h:inputText value="#{mainPage.eventNotificationCreatePage.ruleConditionItems}" maxlength="1024"
                 style="width: 600px;" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Добавить уведомление" action="#{mainPage.createEventNotification}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
<rich:dataTable value="#{mainPage.eventNotificationCreatePage.eventParamHints}" var="eventParamHint"
                columnClasses="center-aligned-column">
    <f:facet name="header">
        <h:outputText escape="true" value="Описание параметров для темы, адресов и условий" styleClass="column-header" />
    </f:facet>
    <rich:column colspan="2">
        <h:outputText escape="true" value="#{eventParamHint.typeName}" styleClass="output-text" />
    </rich:column>
    <rich:subTable value="#{eventParamHint.paramHints}" var="item"
                   columnClasses="left-aligned-column, left-aligned-column">
        <rich:column>
            <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.description}" styleClass="output-text" />
        </rich:column>
    </rich:subTable>
</rich:dataTable>