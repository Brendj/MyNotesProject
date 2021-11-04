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
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.syncReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.syncReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildSyncReport}"
                           reRender="workspaceTogglePanel, syncReportTable"
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
                        var="sync" rowKeyVar="row" rows="15" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Учреждение" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Время начала синхронизации" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Время завершения синхронизации" />
                    </rich:column>
                    <rich:column breakBefore="true" headerClass="center-aligned-column" rowspan="1" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Идентификатор" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1" colspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Наименование" />
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
            <f:facet name="footer">
                <rich:datascroller for="syncReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>