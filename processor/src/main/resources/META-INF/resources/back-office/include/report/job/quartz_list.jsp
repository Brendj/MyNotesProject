<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToViewReports())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель посмотра списка правил обработки автоматических отчетов --%>
<h:outputText escape="true" value="Запланированные задачи менеджера расписаний Quartz Scheduler" styleClass="output-text" />
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Обновить" action="#{mainPage.quartzJobsListPage.reload}"
                       reRender="workspaceTogglePanel" styleClass="command-button" status="quartzJobsListStatus" />
    <a4j:status id="quartzJobsListStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
</h:panelGrid>

<rich:dataTable id="quartzJobsListTable" binding="#{mainPage.quartzJobsListPage.pageComponent}"
                footerClass="data-table-footer" value="#{mainPage.quartzJobsListPage.items}" var="item" rows="50"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Ид. расписания" />
        </f:facet>
        <h:outputText value="#{item.scheduleId}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название расписания" />
        </f:facet>
        <h:outputText value="#{item.scheduleName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип отчета" />
        </f:facet>
        <h:outputText value="#{item.jobClass}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="CRON-выражение" />
        </f:facet>
        <h:outputText value="#{item.cronExpression}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Включенные правила" />
        </f:facet>
        <h:outputText value="#{item.ruleIds}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Следующий запуск" />
        </f:facet>
        <h:outputText value="#{item.nextRun}" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="quartzJobsListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                           stepControls="auto" boundaryControls="hide">
            <f:facet name="previous">
                <h:graphicImage value="/images/16x16/left-arrow.png" />
            </f:facet>
            <f:facet name="next">
                <h:graphicImage value="/images/16x16/right-arrow.png" />
            </f:facet>
        </rich:datascroller>
    </f:facet>
</rich:dataTable>
