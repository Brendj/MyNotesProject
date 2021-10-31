<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="orgBalanceReportGrid" binding="#{mainPage.orgBalanceReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="3">
        <h:outputText escape="true" value="Базовая дата (не включая)" styleClass="output-text" />
        <rich:calendar value="#{mainPage.orgBalanceReportPage.baseDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildOrgBalanceReport}"
                           reRender="workspaceTogglePanel, orgBalanceReportTable"
                           styleClass="command-button" status="orgBalanceReportGenerateStatus"/>
    </h:panelGrid>
    <a4j:status id="orgBalanceReportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по организации \"#{mainPage.orgBalanceReportPage.shortName}\""
                      styleClass="output-text" />
        <rich:dataTable id="orgBalanceReportTable"
                        value="#{mainPage.orgBalanceReportPage.orgBalanceReport.org.clientGroups}" var="clientGroup"
                        columnClasses="left-aligned-column, right-aligned-column, right-aligned-column, right-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column colspan="4" />
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Платежи" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Покупки по картам" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column">
                        <h:outputText escape="true" value="Баланс по картам" styleClass="column-header" />
                    </rich:column>
                    <rich:column colspan="4" breakBefore="true" styleClass="left-aligned-column">
                        <h:outputText escape="true" value="#{mainPage.orgBalanceReportPage.shortName}"
                                      styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgBalanceReportPage.orgBalanceReport.org.totalClientPaymentSum}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgBalanceReportPage.orgBalanceReport.org.totalOrderSumByCard}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                    <rich:column styleClass="right-aligned-column">
                        <h:outputText escape="true"
                                      value="#{mainPage.orgBalanceReportPage.orgBalanceReport.org.totalBalance}"
                                      converter="copeckSumConverter" styleClass="column-header" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>

            <rich:column colspan="4">
                <h:outputText escape="true" value="#{clientGroup.groupName}" styleClass="output-text"
                              style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalClientPaymentSum}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalOrderSumByCard}" converter="copeckSumConverter"
                              styleClass="output-text" style="font-weight: bold;" />
            </rich:column>
            <rich:column>
                <h:outputText escape="true" value="#{clientGroup.totalBalance}" converter="copeckSumConverter"
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
                    <h:outputText escape="true" value="#{client.totalClientPaymentSum}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalOrderSumByCard}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
                <rich:column>
                    <h:outputText escape="true" value="#{client.totalBalance}" converter="copeckSumConverter"
                                  styleClass="output-text" />
                </rich:column>
            </rich:subTable>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>