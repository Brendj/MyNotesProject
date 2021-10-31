<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToViewReports())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель просмотра правила обработки автоматических отчетов --%>
<h:panelGrid id="reportJobViewGrid" binding="#{mainPage.reportJobViewPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportJobViewPage.idOfSchedulerJob}" styleClass="input-text"
                     style="width: 200px;" />
        <h:outputText escape="true" value="Название задачи" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportJobViewPage.jobName}" styleClass="input-text"
                     style="width: 400px;" />
        <h:outputText escape="true" value="Тип отчета" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.reportJobViewPage.reportType}" styleClass="input-text"
                     style="width: 600px;" />
        <h:outputText escape="true" value="Включено" styleClass="output-text" />
        <h:selectBooleanCheckbox disabled="true" value="#{mainPage.reportJobViewPage.enabled}"
                                 styleClass="output-text" />
        <h:outputText escape="true" value="CRON-выражение" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:inputText readonly="true" value="#{mainPage.reportJobViewPage.cronExpression}" maxlength="128"
                         styleClass="input-text" style="width: 200px;" />
            <h:outputLink target="_blank" value="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/crontrigger.html"
                          styleClass="command-link">
                <h:outputText escape="true" value="описание" styleClass="output-text" />
            </h:outputLink>
        </h:panelGrid>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid">
        <rich:simpleTogglePanel label="Ручной запуск" switchType="client"
                                opened="false" headerClass="filter-panel-header">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText value="Дата выборки от:" styleClass="output-text"/>
                <rich:calendar value="#{mainPage.reportJobViewPage.generateStartDate}" popup="true"/>
                <h:outputText value="Дата выборки до:" styleClass="output-text"/>
                <rich:calendar value="#{mainPage.reportJobViewPage.generateEndDate}" popup="true"/>
            </h:panelGrid>
            <rich:spacer width="20"/>
            <a4j:commandButton value="Запустить сейчас" action="#{mainPage.reportJobViewPage.triggerJob}"
            reRender="workspaceTogglePanel" styleClass="command-button" />
        </rich:simpleTogglePanel>
        <a4j:commandButton value="Редактировать" action="#{mainPage.showReportJobEditPage}"
                           reRender="selectedReportJobGroupMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
</h:panelGrid>