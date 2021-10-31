<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="orgOrderReportGrid" binding="#{mainPage.orgOrderReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.orgOrderReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.orgOrderReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата (не включая)" styleClass="output-text" />
        <rich:calendar value="#{mainPage.orgOrderReportPage.endDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Скрывать клиентов, по которым нет данных"
                      styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.orgOrderReportPage.hideEmptyClients}" styleClass="output-text" />
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.orgOrderReportPage.buildOrgOrderReport}"
                           reRender="workspaceTogglePanel, orgOrderReportTable" styleClass="command-button"
                           status="orgOrderReportGenerateStatus"/>
        <a4j:status id="orgOrderReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="orgOrderReportTable" value="#{mainPage.orgOrderReportPage.orgOrderReport.org.clientGroups}"
                        var="clientGroup" rows="5"
                        columnClasses="left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Номер л/с" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Покупки по картам" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Покупки наличными" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Сумма покупок по бесплатному питанию"
                                      styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Сумма скидок"
                                      styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Сумма дотаций" styleClass="column-header" />
                    </rich:column>
                    <rich:column colspan="1" breakBefore="true" styleClass="left-aligned-column">
                        <h:outputText escape="true" value="#{mainPage.orgOrderReportPage.shortName}"
                                      styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalOrderSumByCard}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalOrderSumByCash}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalSocDiscount}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalTrdDiscount}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalGrantSum}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>

            <rich:column colspan="1">
                <h:outputText escape="true" value="#{clientGroup.groupName}" styleClass="output-text"
                              style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalOrderSumByCard}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalOrderSumByCash}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalSocDiscount}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalTrdDiscount}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalGrantSum}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>

            <rich:subTable value="#{clientGroup.clients}" var="client"
                           columnClasses="right-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column">
                <rich:column>
                    <h:outputText escape="true" value="#{client.contractId}" converter="contractIdConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalOrderSumByCard}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalOrderSumByCash}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalSocDiscount}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalTrdDiscount}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalGrantSum}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
            </rich:subTable>
        </rich:dataTable>
        <rich:datascroller id="orgOrderReportTableDatascroller" for="orgOrderReportTable"
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
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>