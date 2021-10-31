<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="ordersMonitoringReportPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.OrdersMonitoringReportPage"--%>
<h:panelGrid styleClass="borderless-grid">
    <h:panelGrid columns="2">
        <h:outputText value="Статус: " styleClass="output-text"/>
        <h:panelGroup>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{ordersMonitoringReportPage.missingCount}"
                             action="#{mainPage.showGoodRequestReportPage}" styleClass="command-link">
                <f:setPropertyActionListener value="-1" target="#{mainPage.goodRequestReportPage.requestsFilter}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.goodRequestReportPage.daysLimit}" />
                <f:setPropertyActionListener value="#{ordersMonitoringReportPage.missingStartDate}" target="#{mainPage.goodRequestReportPage.startDate}" />
            </a4j:commandLink>
            <h:outputText value=" / #{ordersMonitoringReportPage.overallCount}" styleClass="output-text"/>
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Обновить отчет" action="#{ordersMonitoringReportPage.doGenerateReport}"
                           reRender="mainMenu, workspaceTogglePanel, ordersMonitoringReportTable"
                           styleClass="command-button" status="statusSyncReportGenerateStatus" />
        <a4j:status id="statusSyncReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>