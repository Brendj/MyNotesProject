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
                           reRender="workspaceTogglePanel, statusSyncReportTable"
                           styleClass="command-button" status="statusSyncReportGenerateStatus" />
        <a4j:status id="statusSyncReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
        <h:outputText escape="true" value="Статус синхронизации" styleClass="output-text" />
        <rich:dataTable id="statusSyncReportTable" value="#{mainPage.statusSyncReportPage.statusSyncReport.syncItems}"
                        var="statusSync" rowKeyVar="row" rows="20" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Идентификатор учреждения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Название учреждения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Cинхронизация" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Время последней синхронизации" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Наличие ошибок в полной синхронизации" />
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
            <rich:column styleClass="center-aligned-column">
                <a4j:commandButton value="Посмотреть" action="#{mainPage.statusSyncReportPage.doCurrentStatusSync}"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('syncErrorsWindow')}.show();"
                                   reRender="syncErrorsTable" styleClass="command-button"
                                   rendered="#{not empty statusSync.syncErrors}">
                    <f:setPropertyActionListener value="#{statusSync}" target="#{mainPage.statusSyncReportPage.currentStatusSync}"/>
                </a4j:commandButton>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="statusSyncReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>