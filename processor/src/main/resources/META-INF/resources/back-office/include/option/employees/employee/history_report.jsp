<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2009. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="employeeHistoryReportPage" type="ru.axetta.ecafe.processor.web.ui.option.employees.EmployeeHistoryReportPage"--%>
<h:panelGrid id="employeeHistoryReportGrid" binding="#{employeeHistoryReportPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{employeeHistoryReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{employeeHistoryReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <a4j:commandButton value="Генерировать отчет" action="#{employeeHistoryReportPage.buildReport}"
                           reRender="workspaceTogglePanel, employeeHistoryReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:dataTable id="employeeHistoryReportTable" value="#{employeeHistoryReportPage.cardEventOperationItems}" var="employeer" rows="10" rowKeyVar="row"
                    columnClasses="center-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="№" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Инженер" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Должность" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Название учреждения" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column" rendered="false">
                    <h:outputText escape="true" value="Тип" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Направление прохода" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Дата события" styleClass="column-header" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:column headerClass="column-header" rowspan="#{employeer.operationItemListCount}">
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" rowspan="#{employeer.operationItemListCount}">
            <h:outputText escape="true" value="#{employeer.fullName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" rowspan="#{employeer.operationItemListCount}">
            <h:outputText escape="true" value="#{employeer.position}" styleClass="output-text" />
        </rich:column>
        <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
        </rich:column>
        <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
        </rich:column>

        <rich:subTable value="#{employeer.operationItemList}" var="history"
                       columnClasses="center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{history.organization.shortName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header" rendered="false">
                <h:outputText escape="true" value="#{history.organization.refectoryType}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{history.passDirection}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{history.operationDate}" converter="timeConverter" styleClass="output-text" />
            </rich:column>
        </rich:subTable>

        <f:facet name="footer">
            <rich:datascroller for="employeeHistoryReportTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>