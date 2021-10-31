<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="totalServicesReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TotalServicesReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{totalServicesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{totalServicesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{totalServicesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{totalServicesReportPage.filter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
        <h:outputText escape="true" value="Показывать информацию по корпусам" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{totalServicesReportPage.showBuildingDetails}"
                                 styleClass="output-text">
            <a4j:support event="onclick" reRender="reportPanelGrid" ajaxSingle="true"
                         actionListener="#{totalServicesReportPage.showBuildingDetails}"/>
        </h:selectBooleanCheckbox>
        <a4j:commandButton value="Генерировать отчет" action="#{totalServicesReportPage.buildReportHTML}"
                           reRender="workspaceTogglePanel, itemsReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Свод по услугам" styleClass="output-text" />
        <rich:dataTable id="itemsReportTable" value="#{totalServicesReportPage.totalReport.items}"
        var="item" rowKeyVar="row" rows="20" footerClass="data-table-footer"
        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3">
                        <h:outputText styleClass="column-header" escape="true" value="Организация" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3">
                        <h:outputText styleClass="column-header" escape="true" value="Число учащихся" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Число льготников" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Зафиксирован проход" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Получили льготное питание всего" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3">
                        <h:outputText styleClass="column-header" escape="true" value="Получили льготное питание временно обучающиеся в данной ОО" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Получили комплексное питание" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Получили питание в буфете" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="3" colspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Получили питание (льготное + платное)" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.shortName}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column ">
                <h:outputText styleClass="output-text" value="#{item.totalClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.planBenefitClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_planBenefitClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.currentClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_currentClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.realBenefitClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_realBenefitClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.realBenefitClientsOtherOrgsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.realPaidClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_realPaidClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.realSnackPaidClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_realSnackPaidClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.uniqueClientsCount}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.per_uniqueClientsCount}" />
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
        <h:commandButton value="Выгрузить в CSV" action="#{totalServicesReportPage.buildReportCSV}" styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>