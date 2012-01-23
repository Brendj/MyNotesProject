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
    <h:panelGrid styleClass="borderless-grid" columns="5">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{mainPage.orgOrderReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата (не включая)" styleClass="output-text" />
        <rich:calendar value="#{mainPage.orgOrderReportPage.endDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildOrgOrderReport}"
                           reRender="mainMenu, workspaceTogglePanel, orgOrderReportTable" styleClass="command-button" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по организации \"#{mainPage.orgOrderReportPage.shortName}\""
                      styleClass="output-text" />
        <rich:dataTable id="orgOrderReportTable" value="#{mainPage.orgOrderReportPage.orgOrderReport.org.clientGroups}"
                        var="clientGroup"
                        columnClasses="left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column colspan="4" />
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Покупки по картам" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Покупки наличными" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Сумма покупок по бесплатному питанию"
                                      styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Сумма дотаций" styleClass="output-text" />
                    </rich:column>
                    <rich:column colspan="4" breakBefore="true" styleClass="left-aligned-column">
                        <h:outputText escape="true" value="#{mainPage.orgOrderReportPage.shortName}"
                                      styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalOrderSumByCard}"
                                      converter="copeckSumConverter" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalOrderSumByCash}"
                                      converter="copeckSumConverter" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalDiscount}"
                                      converter="copeckSumConverter" styleClass="output-text" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgOrderReportPage.orgOrderReport.org.totalGrantSum}"
                                      converter="copeckSumConverter" styleClass="output-text" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>

            <rich:column colspan="4">
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
                <h:outputText escape="true" value="#{clientGroup.totalDiscount}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalGrantSum}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>

            <rich:subTable value="#{clientGroup.clients}" var="client"
                           columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column">
                <rich:column>
                    <h:outputText escape="true" value="#{client.contractId}" converter="contractIdConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.person.surname}" styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.person.firstName}" styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.person.secondName}" styleClass="output-text" />
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
                    <h:outputText escape="true" value="#{client.totalDiscount}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalGrantSum}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
            </rich:subTable>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>