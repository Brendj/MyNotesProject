<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
.region {
    font-weight: bold;
    background-color: #E3F6FF;
}
.overall {
    font-weight: bold;
    background-color: #D5E7F0;
}
</style>

<%--@elvariable id="activeClientsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ActiveClientsReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{activeClientsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{activeClientsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{activeClientsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{activeClientsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{activeClientsReportPage.filter}}" />
        </h:panelGroup>

        <a4j:commandButton value="Генерировать отчет" action="#{activeClientsReportPage.executeReport}"
                           reRender="workspaceTogglePanel, itemsReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Сохранить в Excel" actionListener="#{activeClientsReportPage.exportToXLS}"
                           styleClass="command-button"/>
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Активные клиенты" styleClass="output-text" />
        <rich:dataTable id="itemsReportTable" value="#{activeClientsReportPage.report.items}"
                        var="item" rowKeyVar="row" rows="20" footerClass="data-table-footer"
                        columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" colspan="3">
                        <h:outputText styleClass="column-header" escape="true" value="Название учреждения" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество клиентов" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество клиентов, совершавших покупки/пополнения за период" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество клиентов-бесплатников" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество клиентов-бесплатников, совершавших покупки/пополнения за период" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество клиентов, совершавших проход за период" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Количество сотрудников" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="Активных клиентов" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="#{item.style}" colspan="#{item.style.length() > 0 ? 3 : 1}">
                <h:outputText styleClass="output-text" value="#{item.shortname}" />
            </rich:column>
            <rich:column styleClass="#{item.style}" rendered="#{item.style.length() > 0 ? false : true}">
                <h:outputText styleClass="output-text" value="#{item.num}" />
            </rich:column>
            <rich:column styleClass="#{item.style}" rendered="#{item.style.length() > 0 ? false : true}">
                <h:outputText styleClass="output-text" value="#{item.region}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.totalCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.paymentCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.discountCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.realDiscountCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.entersCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.employeesCount}" />
            </rich:column>
            <rich:column styleClass="#{item.style}">
                <h:outputText styleClass="output-text" value="#{item.active}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="itemsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
        <%--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />--%>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>