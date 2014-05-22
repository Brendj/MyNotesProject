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
        <a4j:commandButton value="Обновить" action="#{mainPage.buildCurrentPositionsReport}"
                           reRender="workspaceTogglePanel, currentPositionsReportTable"
                           styleClass="command-button" status="currentPositionsReportGenerateStatus" />
        <a4j:status id="currentPositionsReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Просмотр текущих позиций" styleClass="output-text" />
        <rich:dataTable id="currentPositionsReportTable" value="#{mainPage.currentPositionsReportPage.currentPositionList}"
                        var="currentPosition" rowKeyVar="row" rows="50" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Идентификатор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Дебитор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Кредитор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Сумма" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
           <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
           <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{currentPosition.idOfPosition}" />
            </rich:column>
           <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{currentPosition.contragentDebtorName}" />
            </rich:column>
           <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{currentPosition.contragentCreditorName}" />
            </rich:column>
           <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{currentPosition.summa}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="currentPositionsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showCurrentPositionCSVList}" styleClass="command-button" />
        <a4j:commandButton value="Выполнить перерасчет текущих позиций (длительная операция)" action="#{mainPage.countCurrentPositions}"
                           reRender="workspaceTogglePanel, currentPositionsReportTable"
                           styleClass="command-button" status="currentPositionsReportGenerateStatus"
                           rendered="#{mainPage.eligibleToCountCurrentPositions}" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>