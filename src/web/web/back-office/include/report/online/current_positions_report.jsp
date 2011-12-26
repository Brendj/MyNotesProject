<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="currentPositionsReportPanelGrid" binding="#{mainPage.currentPositionsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildCurrentPositionsReport}"
                           reRender="mainMenu, workspaceTogglePanel, currentPositionsReportTable"
                           styleClass="command-button" status="currentPositionsReportGenerateStatus" />
        <a4j:status id="currentPositionsReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
        <a4j:commandButton value="Рассчитать текущие позиции" action="#{mainPage.countCurrentPositions}"
                           reRender="mainMenu, workspaceTogglePanel, currentPositionsReportTable"
                           styleClass="command-button" status="currentPositionsReportGenerateStatus"
                           rendered="#{mainPage.eligibleToCountCurrentPositions}" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Просмотр текущих позиций" styleClass="output-text" />
        <rich:dataTable id="currentPositionsReportTable" value="#{mainPage.currentPositionsReportPage.currentPositionList}"
                        var="currentPosition" rowKeyVar="row">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Идентификатор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Дебитор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Кредитор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="output-text" escape="true" value="Сумма" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column style="width: 50px; text-align:center">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column style="width: 50px; text-align:center">
                <h:outputText styleClass="output-text" value="#{currentPosition.idOfPosition}" />
            </rich:column>
            <rich:column style="width: 50px; text-align:center">
                <h:outputText styleClass="output-text" value="#{currentPosition.contragentDebtorName}" />
            </rich:column>
            <rich:column style="width: 50px; text-align:center">
                <h:outputText styleClass="output-text" value="#{currentPosition.contragentCreditorName}" />
            </rich:column>
            <rich:column style="width: 50px; text-align:center">
                <h:outputText styleClass="output-text" value="#{currentPosition.summa}" />
            </rich:column>
        </rich:dataTable>
        <h:commandButton value="Выгрузить в SCV" action="#{mainPage.showCurrentPositionCSVList}" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>