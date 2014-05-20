<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="aggregateCostsAndSalesReportPage" type="ru.axetta.ecafe.processor.web.ui.monitoring.AggregateCostsAndSalesReportPage"--%>
<%--@elvariable id="orgListSelectPage" type="ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage"--%>
<h:panelGrid id="aggregateCostsAndSalesReportPanelGrid" binding="#{aggregateCostsAndSalesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{aggregateCostsAndSalesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar value="#{aggregateCostsAndSalesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="3" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{aggregateCostsAndSalesReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>

            <h:outputText styleClass="output-text" escape="true" value=" {#{aggregateCostsAndSalesReportPage.filter}}" />
        </h:panelGrid>
        <a4j:commandButton value="Генерировать отчет" action="#{aggregateCostsAndSalesReportPage.buildReport}"
                           reRender="workspaceTogglePanel, costsAndSalesReportTable"
                           styleClass="command-button" status="sReportGenerateStatus" />
        <a4j:status id="sReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по агрегированным показателям стоимостей и продаж" styleClass="output-text" />
        <rich:dataTable id="costsAndSalesReportTable" value="#{aggregateCostsAndSalesReportPage.aggregateCostsAndSalesReport.costsAndSalesItems}"
                        var="costsAndSales" rowKeyVar="row" rows="15" footerClass="data-table-footer"
                        columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="№" />
                </f:facet>
                <h:outputText escape="true" value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="ИД организации" />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.idOfOrg}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Тип организации" />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.orgType}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Название учреждения" />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.officialName}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Объем продаж, руб." />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.formattedSalesVolume}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Средний чек, руб." />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.formattedAverageReceipt}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Средняя месячная трата, руб." />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.formattedAverageMonthlyExpense}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Средняя стоимость комплексов, руб." />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.formattedAverageComplexPrice}" styleClass="output-text" />
            </rich:column>

            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Стоимость базовой корзины платных блюд, руб." />
                </f:facet>
                <h:outputText escape="true" value="#{costsAndSales.formattedBasicBacketPrice}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="costsAndSalesReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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