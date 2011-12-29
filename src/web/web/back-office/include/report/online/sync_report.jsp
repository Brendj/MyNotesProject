<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="syncReportPanelGrid" binding="#{mainPage.syncReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.syncReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.syncReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.syncReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildSyncReport}"
                           reRender="mainMenu, workspaceTogglePanel, syncReportTable"
                           styleClass="command-button" status="sReportGenerateStatus" />
        <a4j:status id="sReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по синхронизации" styleClass="output-text" />
        <rich:dataTable id="syncReportTable" value="#{mainPage.syncReportPage.syncReport.syncItems}"
                        var="sync" rowKeyVar="row">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Номер учреждения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Название учреждения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Время начала синхронизации" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Время завершения синхронизации" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{row + 1}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="left-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sync.idOfOrg}" />
                </rich:column>
                <rich:column styleClass="left-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sync.officialName}" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sync.syncStartTime}" converter="timeConverter" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sync.syncEndTime}" converter="timeConverter" />
                </rich:column>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>