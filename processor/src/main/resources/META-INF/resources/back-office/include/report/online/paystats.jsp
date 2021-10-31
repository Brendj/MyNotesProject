<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="payStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PayStatsPage"--%>
<h:panelGrid id="payStatsPanelGrid" binding="#{payStatsPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{payStatsPage.fromDate}" datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter"
                       inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{payStatsPage.toDate}" datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter"
                       inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Поставщик" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{payStatsPage.contragent.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{payStatsPage.updateData}"
                           reRender="workspaceTogglePanel, payStatsPageReportTable"
                           styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:outputText rendered="#{payStatsPage.statItems!=null}" styleClass="output-text" escape="true" value="Отчет по платежам агентов за период: #{payStatsPage.fromDateAsString}-#{payStatsPage.toDateAsString}"/>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="payStatsPageReportTable" value="#{payStatsPage.statItems}" var="item" rowKeyVar="row"
                        rows="50" footerClass="data-table-footer">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Контрагент" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Метод платежа" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Количество" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Средняя сумма" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Всего сумма" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.contragentName}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.payMethod}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.count}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.avg}" />
            </rich:column>
            <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.total}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="payStatsPageReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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