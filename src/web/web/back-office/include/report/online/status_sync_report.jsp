<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildStatusSyncReport}"
                           reRender="mainMenu, workspaceTogglePanel, statusSyncReportTable"
                           styleClass="command-button" status="statusSyncReportGenerateStatus" />
        <a4j:status id="statusSyncReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <%-- <h:panelGrid styleClass="borderless-grid" width="100%"> --%>
        <h:outputText escape="true" value="Статус синхронизации" styleClass="output-text" />
        <rich:dataTable id="statusSyncReportTable" value="#{mainPage.statusSyncReportPage.statusSyncReport.syncItems}"
                        var="statusSync" rowKeyVar="row">

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
                        <h:outputText styleClass="output-text" escape="true" value="Cинхронизация" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Время последней синхронизации" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{statusSync.idOfOrg}" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{statusSync.officialName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:graphicImage value="/images/16x16/#{statusSync.snchrnzd}.png" alt="#{statusSync.snchrnzd}"/>
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{statusSync.lastSyncTime}" converter="timeConverter" />
            </rich:column>
        </rich:dataTable>
    <%-- </h:panelGrid> --%>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>