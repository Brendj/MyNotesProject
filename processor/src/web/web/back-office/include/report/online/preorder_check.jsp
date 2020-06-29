<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .regular-row {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #000;
    }
    .alarm-row {
        font-family: Tahoma, Arial, Sans-Serif;
        font-size: 10pt;
        color: #ff0000;
    }
</style>

<%--@elvariable id="preorderCheckReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderCheckReportPage"--%>
<h:panelGrid id="preorderCheckReportPanelGrid" binding="#{preorderCheckReportPage.pageComponent}" styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{preorderCheckReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
        </rich:calendar>

        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{preorderCheckReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
        </rich:calendar>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{preorderCheckReportPage.reload}"
                           reRender="preorderCheckReportTable"
                           styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="preorderCheckReportTable">
        <rich:dataTable id="preorderCheckTable" value="#{preorderCheckReportPage.items}" var="item" rows="50"
                        footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Дата" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Количество предзаказов" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Количество заявок" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Создано" />
                    </rich:column>
                    <rich:column headerClass="column-header">
                        <h:outputText escape="true" value="Обновлено" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.date}" styleClass="#{item.style}" converter="dateConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.preorderAmount}" styleClass="#{item.style}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.goodRequestAmount}" styleClass="#{item.style}" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.createdDate}" styleClass="#{item.style}" converter="dateTimeConverter" />
            </rich:column>
            <rich:column headerClass="column-header">
                <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="#{item.style}" converter="dateTimeConverter" />
            </rich:column>
            <rich:datascroller for="preorderCheckTable" renderIfSinglePage="false" maxPages="5"
                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                <a4j:support event="onpagechange" />
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </rich:dataTable>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>