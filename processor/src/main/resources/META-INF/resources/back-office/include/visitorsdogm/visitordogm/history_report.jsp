<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="visitorDogmHistoryReportPage" type="ru.axetta.ecafe.processor.web.ui.visitordogm.VisitorDogmHistoryReportPage"--%>
<h:panelGrid id="visitorDogmHistoryReportGrid" binding="#{visitorDogmHistoryReportPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{visitorDogmHistoryReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{visitorDogmHistoryReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <a4j:commandButton value="Генерировать отчет" action="#{visitorDogmHistoryReportPage.buildReport}"
                           reRender="workspaceTogglePanel, visitorDogmHistoryReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:dataTable id="visitorDogmHistoryReportTable" value="#{visitorDogmHistoryReportPage.cardEventOperationItems}" var="visitorDogm" rows="10" rowKeyVar="row"
                    columnClasses="center-aligned-column, center-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="№" styleClass="column-header" />
                </rich:column>
                <rich:column headerClass="center-aligned-column">
                    <h:outputText escape="true" value="Сотрудник" styleClass="column-header" />
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

        <rich:column headerClass="column-header" rowspan="#{visitorDogm.operationItemListCount}">
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" rowspan="#{visitorDogm.operationItemListCount}">
            <h:outputText escape="true" value="#{visitorDogm.fullName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header" rowspan="#{visitorDogm.operationItemListCount}">
            <h:outputText escape="true" value="#{visitorDogm.position}" styleClass="output-text" />
        </rich:column>
        <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
        </rich:column>
        <rich:column rowspan="1" style="height: 0 !important; line-height: 0;padding: 0;margin: 0; border: 0">
        </rich:column>

        <rich:subTable value="#{visitorDogm.operationItemList}" var="history"
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
            <rich:datascroller for="visitorDogmHistoryReportTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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