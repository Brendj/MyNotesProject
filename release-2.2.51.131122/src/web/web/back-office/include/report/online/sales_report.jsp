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
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{mainPage.salesReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
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
                        var="sale" rowKeyVar="row" rows="10" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Организация" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Название" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Выход" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Вид производства" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Цена за ед"  />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Скидка на ед" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Количество" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Сумма без скидки" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Сумма скидки" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Итоговая сумма" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Время первой продажи" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Время последней продажи" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText value="#{row + 1}" styleClass="output-text" />
                </rich:column>
                <rich:column styleClass="left-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.officialName}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.menuDetailName}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.menuOutput}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.menuOrigin}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.rPrice}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.discount}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.qty}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.sumPrice}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.sumPriceDiscount}" />
                </rich:column>
                <rich:column styleClass="right-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.total}" />
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.firstTimeSale}" converter="timeConverter"/>
                </rich:column>
                <rich:column styleClass="center-aligned-column">
                    <h:outputText styleClass="output-text" value="#{sale.lastTimeSale}" converter="timeConverter"/>
                </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="salesReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>