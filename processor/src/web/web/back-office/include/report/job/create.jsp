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

<%-- Панель создания правила обработки автоматических отчетов --%>
<h:panelGrid id="reportJobCreateGrid" binding="#{mainPage.reportJobCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Название задачи" styleClass="output-text" />
    <h:inputText value="#{mainPage.reportJobCreatePage.jobName}" maxlength="128" styleClass="input-text"
                 style="width: 400px;" />
    <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.reportJobCreatePage.reportType}" styleClass="input-text" style="width: 600px;">
        <f:selectItems value="#{mainPage.reportJobCreatePage.reportTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Включено" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportJobCreatePage.enabled}" styleClass="output-text" />
    <h:outputText escape="true" value="CRON-выражение" styleClass="output-text" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:inputText value="#{mainPage.reportJobCreatePage.cronExpression}" maxlength="128" styleClass="input-text"
                     style="width: 200px;" />
        <h:outputLink target="_blank" value="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/crontrigger.html"
                      styleClass="command-link">
            <h:outputText escape="true" value="описание" styleClass="output-text" />
        </h:outputLink>
    </h:panelGrid>
</h:panelGrid>
<h:panelGrid id="panelCreate" styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Показать правила" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{mainPage.reportJobCreatePage.showRules}" styleClass="output-text">
        <a4j:support event="onclick" reRender="panelCreate"/>
    </h:selectBooleanCheckbox>

    <h:outputText value="Список правил" styleClass="output-text" rendered="#{mainPage.reportJobCreatePage.showRules}" />
    <h:panelGroup layout="block" style="height: 150px; width: 600px; overflow-y: scroll;"
                  rendered="#{mainPage.reportJobCreatePage.showRules}">
        <h:selectManyCheckbox id="rules" value="#{mainPage.reportJobCreatePage.preferentialRules}"
                              layout="pageDirection" styleClass="output-text"
                              rendered="#{mainPage.reportJobCreatePage.showRules}">
            <f:selectItems value="#{mainPage.availableCreateRules}" />
        </h:selectManyCheckbox>
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Добавить задачу" action="#{mainPage.createReportJob}"
                       reRender="selectedReportJobGroupMenu, workspaceTogglePanel" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>