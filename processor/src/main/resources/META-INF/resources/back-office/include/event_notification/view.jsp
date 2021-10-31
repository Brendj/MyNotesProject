<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель посмотра правила обработки уведомлений --%>
<h:panelGrid id="eventNotificationViewGrid" binding="#{mainPage.eventNotificationViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.eventNotificationViewPage.idOfReportHandleRule}"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Название" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.eventNotificationViewPage.notificationName}"
                     style="width: 600px;" styleClass="input-text" />
        <h:outputText escape="true" value="Включено" styleClass="output-text" />
        <h:selectBooleanCheckbox disabled="true" readonly="true" value="#{mainPage.eventNotificationViewPage.enabled}"
                                 styleClass="output-text" />
        <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.eventNotificationViewPage.eventType}" style="width: 600px;"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Формат уведомления" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.eventNotificationViewPage.documentFormat}"
                     converter="reportFormatConverter" style="width: 600px;" styleClass="input-text" />
        <h:outputText escape="true" value="Тема письма" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.eventNotificationViewPage.subject}" style="width: 600px;"
                     styleClass="input-text" />
    </h:panelGrid>
    <rich:dataTable value="#{mainPage.eventNotificationViewPage.routeAddresses}" var="item"
                    columnClasses="left-aligned-column">
        <f:facet name="header">
            <h:outputText escape="true" value="Адреса рассылки" styleClass="output-text" />
        </f:facet>
        <rich:column>
            <h:outputText escape="true" value="#{item}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
    <rich:dataTable value="#{mainPage.eventNotificationViewPage.ruleConditionItems}" var="item"
                    columnClasses="left-aligned-column, center-aligned-column, left-aligned-column">
        <f:facet name="header">
            <h:outputText escape="true" value="Условия отправки уведомления" styleClass="output-text" />
        </f:facet>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionArgument}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionOperationText}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <h:outputText escape="true" value="#{item.conditionConstant}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showEventNotificationEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>
<rich:dataTable value="#{mainPage.eventNotificationViewPage.paramHints}" var="item"
                columnClasses="left-aligned-column, left-aligned-column">
    <f:facet name="header">
        <h:outputText escape="true" value="Описание параметров для темы, адресов и условий" styleClass="output-text" />
    </f:facet>
    <rich:column>
        <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
    </rich:column>
    <rich:column>
        <h:outputText escape="true" value="#{item.description}" styleClass="output-text" />
    </rich:column>
</rich:dataTable>