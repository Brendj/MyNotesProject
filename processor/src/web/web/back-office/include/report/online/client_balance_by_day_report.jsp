<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="clientBalanceByDayReportPanelGrid" binding="#{mainPage.clientBalanceByDayReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Дата" />
        <rich:calendar value="#{mainPage.clientBalanceByDayReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Поставщик" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.clientBalanceByDayReportPage.contragent.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.clientBalanceByDayReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.clientBalanceByDayReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.clientBalanceByDayReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientBalanceByDayReportPage.clientFilter.clientGroupId}"
                         styleClass="input-text">
            <f:selectItems value="#{mainPage.clientBalanceByDayReportPage.clientFilter.clientGroupItems}" />
            <a4j:support event="onchange" reRender="clientBalanceByDayReportPanelGrid" />
        </h:selectOneMenu>
    </h:panelGrid>
    <h:panelGroup>
        <h:outputText escape="true" value="Текущий баланс" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientBalanceByDayReportPage.clientFilter.clientBalanceCondition}"
                         styleClass="input-text" style="margin-left: 10px; width: 100px;">
            <f:selectItems value="#{mainPage.clientBalanceByDayReportPage.clientFilter.clientBalanceMenu.items}" />
            <a4j:support event="onchange" reRender="clientBalanceByDayReportPanelGrid" />
        </h:selectOneMenu>
    </h:panelGroup>
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.clientBalanceByDayReportPage.exportToHtmlOnePerUser}"
                           reRender="clientBalanceByDayReportTable, clientBalanceByDayReportTableDatascroller"
                           styleClass="command-button" status="clientBalanceByDayReportGenerateStatus" />
        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.clientBalanceByDayReportPage.exportToXLSOnePerUser}"
                         styleClass="command-button" />
    </h:panelGrid>
    <a4j:status id="clientBalanceByDayReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="clientBalanceByDayReportTable"
                        value="#{mainPage.clientBalanceByDayReportPage.clientsBalance}" var="complex" rowKeyVar="row"
                        rows="20" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Наименование ОО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Наименование группы" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Номер л/с" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Текущий овердрафт" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Баланс" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Дата и время последних изменений" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.orgShortName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.groupName}" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link"
                                 reRender="mainMenu, workspaceForm">
                    <h:outputText styleClass="output-text" value="#{complex.contractId}"
                                  converter="contractIdConverter" />
                    <f:setPropertyActionListener value="#{complex.idOfClient}"
                                                 target="#{mainPage.selectedIdOfClient}" />
                </a4j:commandLink>
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.limit}" converter="copeckSumConverter" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.totalBalance}" converter="copeckSumConverter" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.date}" />
            </rich:column>
            <f:facet name="footer">
                <rich:columnGroup rendered="#{not empty mainPage.clientBalanceByDayReportPage.clientsBalance}">
                    <rich:column styleClass="right-aligned-column" colspan="4">
                        <h:outputText styleClass="column-header" escape="true" value="ИТОГО" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText value="#{mainPage.clientBalanceByDayReportPage.totalBalance}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
        </rich:dataTable>
        <rich:datascroller id="clientBalanceByDayReportTableDatascroller" for="clientBalanceByDayReportTable"
                           renderIfSinglePage="false" maxPages="10" fastControls="hide" stepControls="auto"
                           boundaryControls="hide"
                           style="border: solid 1px #C0C0C0; !important; background-color: white !important">
            <f:facet name="previous">
                <h:graphicImage value="/images/16x16/left-arrow.png" />
            </f:facet>
            <f:facet name="next">
                <h:graphicImage value="/images/16x16/right-arrow.png" />
            </f:facet>
        </rich:datascroller>
    </h:panelGrid>

</h:panelGrid>