<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="salesReportPanelGrid" binding="#{mainPage.salesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.salesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.salesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.salesReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildSalesReport}"
                           reRender="mainMenu, workspaceTogglePanel, salesReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по продажам" styleClass="output-text" />
        <rich:dataTable id="salesReportTable" value="#{mainPage.salesReportPage.salesReport.salesItems}"
                        var="sale" rowKeyVar="row">
            <rich:column headerClass="center-aligned-column" style="width: 50px; text-align:center">
                <f:facet name="header">
                     <h:outputText styleClass="output-text" escape="true" value="№" />
                </f:facet>
                <h:outputText styleClass="output-text" value="#{row + 1}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:left">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Организация" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.officialName}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:left">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Название" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.menuDetailName}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Выход" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.menuOutput}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Вид производства" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.menuOrigin}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Цена за ед" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.rPrice}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Скидка на ед" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.discount}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Кол-во" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.qty}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Сумма без скидки" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.sumPrice}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Сумма скидки" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.sumPriceDiscount}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Итоговая сумма" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.total}" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Время первой продажи" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.firstTimeSale}" converter="timeConverter" />
            </rich:column>
            <rich:column headerClass="center-aligned-column" style="text-align:right">
                <f:facet name="header">
                    <h:panelGroup>
                        <h:outputText styleClass="output-text" escape="true" value="Время последней продажи" />
                    </h:panelGroup>
                </f:facet>
                <h:outputText styleClass="output-text" value="#{sale.lastTimeSale}" converter="timeConverter" />
            </rich:column>
        </rich:dataTable>
        <h:commandButton value="Выгрузить в SCV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>