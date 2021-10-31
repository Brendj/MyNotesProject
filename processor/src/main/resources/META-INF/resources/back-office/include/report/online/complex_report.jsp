<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="complexReportPanelGrid" binding="#{mainPage.complexReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.complexReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.complexReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{mainPage.complexReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.complexReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildComplexReport}"
                           reRender="workspaceTogglePanel, complexReportTable"
                           styleClass="command-button" status="cReportGenerateStatus" />
        <a4j:status id="cReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText styleClass="output-text" escape="true" value="Отчет по комплексам" />
        <rich:dataTable id="complexReportTable" value="#{mainPage.complexReportPage.complexReport.complexItems}"
                        var="complex" rowKeyVar="row">

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
                        <h:outputText styleClass="column-header" escape="true" value="Цена за ед" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Скидка за ед" />
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
                        <h:outputText styleClass="ocolumn-header" escape="true" value="Итоговая сумма" />
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
                <h:outputText styleClass="output-text" value="#{complex.officialName}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.menuDetailName}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.rPrice}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.discount}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.qty}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.sumPrice}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.sumPriceDiscount}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.total}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.firstTimeSale}" converter="timeConverter" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{complex.lastTimeSale}" converter="timeConverter" />
            </rich:column>
        </rich:dataTable>
        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showComplexCSVList}" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>