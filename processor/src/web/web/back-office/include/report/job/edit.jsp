<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditReports()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель редактирования правила обработки автоматических отчетов --%>
<h:panelGrid id="reportJobEditGrid" binding="#{mainPage.reportJobEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.reportJobEditPage.idOfSchedulerJob}" styleClass="input-text"
                 style="width: 200px;" />
    <h:outputText escape="true" value="Название задачи" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportJobEditPage.jobName}" styleClass="input-text" maxlength="128"
                 style="width: 400px;" />
    <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.reportJobEditPage.reportType}" styleClass="input-text" style="width: 600px;">
        <f:selectItems value="#{mainPage.reportJobEditPage.reportTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Включено" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportJobEditPage.enabled}" styleClass="output-text" />
    <h:outputText escape="true" value="CRON-выражение" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:inputText value="#{mainPage.reportJobEditPage.cronExpression}" maxlength="128" styleClass="input-text"
                     style="width: 200px;" />
        <h:outputLink target="_blank" value="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/crontrigger.html"
                      styleClass="command-link">
            <h:outputText escape="true" value="описание" styleClass="output-text" />
        </h:outputLink>
    </h:panelGrid>
</h:panelGrid>
<h:panelGrid id="panelEdit" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Показать правила" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportJobEditPage.showRules}" styleClass="output-text">
        <a4j:support event="onclick" reRender="panelEdit"/>
    </h:selectBooleanCheckbox>

    <h:outputText value="Список правил" styleClass="output-text" rendered="#{mainPage.reportJobEditPage.showRules}" />
    <h:panelGroup layout="block" style="height: 150px; width: 600px; overflow-y: scroll;"
                  rendered="#{mainPage.reportJobEditPage.showRules}">
        <h:selectManyCheckbox id="rules" value="#{mainPage.reportJobEditPage.preferentialRules}" layout="pageDirection"
                              styleClass="output-text" rendered="#{mainPage.reportJobEditPage.showRules}">
            <f:selectItems value="#{mainPage.availableEditRules}" />
        </h:selectManyCheckbox>
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateReportJob}"
                       reRender="selectedReportJobGroupMenu, workspaceTogglePanel" styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showReportJobEditPage}"
                       reRender="selectedReportJobGroupMenu, workspaceTogglePanel" ajaxSingle="true"
                       styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>