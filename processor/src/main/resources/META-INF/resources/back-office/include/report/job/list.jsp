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

<%-- Панель посмотра списка правил обработки автоматических отчетов --%>
<rich:dataTable id="reportJobTable" binding="#{mainPage.reportJobListPage.pageComponent}"
                footerClass="data-table-footer" value="#{mainPage.reportJobListPage.items}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <a4j:commandLink reRender="selectedReportJobGroupMenu, workspaceForm" value="#{item.idOfSchedulerJob}" action="#{mainPage.showReportJobViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfSchedulerJob}" target="#{mainPage.selectedIdOfReportJob}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название" />
        </f:facet>
        <a4j:commandLink reRender="reportJobGroupMenu, workspaceForm" value="#{item.jobName}" action="#{mainPage.showReportJobViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfSchedulerJob}" target="#{mainPage.selectedIdOfReportJob}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип отчета" />
        </f:facet>
        <h:outputText value="#{item.reportType}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Включено" />
        </f:facet>
        <h:selectBooleanCheckbox disabled="true" readonly="true" value="#{item.enabled}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="CRON-выражение" />
        </f:facet>
        <h:outputText value="#{item.cronExpression}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <a4j:commandLink reRender="selectedReportJobGroupMenu, workspaceForm" action="#{mainPage.showReportJobEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfSchedulerJob}" target="#{mainPage.selectedIdOfReportJob}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditReports}">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('reportJobDeletePanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfSchedulerJob}" target="#{mainPage.removedIdOfReportJob}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="reportJobTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
