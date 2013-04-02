<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 20.12.11
  Time: 13:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid binding="#{mainPage.enterEventReportPage.pageComponent}" id="enterEventReportPanelGrid" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.enterEventReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.enterEventReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Организация" />

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" id="enterEventFilter" escape="true" value=" {#{mainPage.enterEventReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildEnterEventReport}"
                           reRender="mainMenu, workspaceTogglePanel, enterEventReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по турникетам" styleClass="output-text" />
        <rich:dataTable id="enterEventReportTable" value="#{mainPage.enterEventReportPage.enterEventReport.enterEventItems}"
                        var="enterEvent" rowKeyVar="row" rows="15"  reRender="menuViewListScroller" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="№" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Номер учреждения" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Название учреждения" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Наименование входа" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Адрес турникета" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Направление прохода" styleClass="column-header"/>
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Код события" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Номер договора" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Фамилия и Имя учащегося" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Дата события" styleClass="column-header"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.idoforg}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.officialname}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.entername}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.turnstileaddr}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.passdirection}" styleClass="output-text" style="color:#{enterEvent.color}"/>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.eventCode}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.docserialnum}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.personFullName}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{enterEvent.evtdatetime}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="enterEventReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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

        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showEnterEventCSVList}"
                         styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>