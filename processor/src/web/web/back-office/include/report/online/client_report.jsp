<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid">
    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Список контрагентов" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <a4j:commandButton value="..." action="#{mainPage.showContragentListSelectPage}"
                                   reRender="modalContragentListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                    <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
                    <f:setPropertyActionListener value="#{mainPage.clientReportPage.contragentIds}"
                                                 target="#{mainPage.contragentListSelectPage.selectedIds}" />
                </a4j:commandButton>
                <h:outputText value=" {#{mainPage.clientReportPage.contragentFilter}}" escape="true"
                              styleClass="output-text" />
            </h:panelGroup>
        </h:panelGrid>
    </a4j:outputPanel>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientReportPage.clientFilter.clientGroupId}"
                         styleClass="input-text">
            <f:selectItems value="#{mainPage.clientReportPage.clientFilter.clientGroupItems}" />
            <a4j:support event="onchange" reRender="clientBalanceByDayReportPanelGrid" />
        </h:selectOneMenu>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildClientReport}"
                           reRender="workspaceTogglePanel, clientReportTable" styleClass="command-button"
                           status="clientReportGenerateStatus" />
        <a4j:status id="clientReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по учащимся" styleClass="output-text" />
        <rich:dataTable id="clientReportTable" value="#{mainPage.clientReportPage.clientReport.clientItems}"
                        var="client" rowKeyVar="row" rows="15" footerClass="data-table-footer"
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
                    <rich:column headerClass="center-aligned-column" colspan="4">
                        <h:outputText escape="true" value="Количество клиентов" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" colspan="3">
                        <h:outputText escape="true" value="Сумма балансов" styleClass="column-header" />
                    </rich:column>
                    <rich:column breakBefore="true" headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Общее" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Баланс > 0" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Баланс = 0" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText escape="true" value="Баланс < 0" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Общая" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Баланс > 0" styleClass="column-header" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Баланс < 0" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText value="#{client.officialName}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.clientCount}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.clientWithPositiveBalanceCount}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.clientWithNullBalanceCount}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.clientWithNegativeBalanceCount}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.balanceSum}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.posBalanceSum}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText value="#{client.negBalanceSum}" styleClass="output-text" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="clientReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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

        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showClientOrgCSVList}"
                         styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>