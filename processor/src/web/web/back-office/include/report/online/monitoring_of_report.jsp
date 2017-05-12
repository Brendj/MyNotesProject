<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: almaz anvarov
  Date: 05.05.2017
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="monitoringOfReportPanelGrid" binding="#{mainPage.monitoringOfReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.monitoringOfReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.monitoringOfReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.monitoringOfReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.monitoringOfReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,monitoringOfReportPanelGrid"
                         actionListener="#{mainPage.monitoringOfReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect" value="#{mainPage.monitoringOfReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.monitoringOfReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,monitoringOfReportPanelGrid"
                         actionListener="#{mainPage.monitoringOfReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.monitoringOfReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,monitoringOfReportPanelGrid"
                         actionListener="#{mainPage.monitoringOfReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.monitoringOfReportPage.buildReportHTML}"
                           reRender="migrantsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.monitoringOfReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="migrantsReportTable">
        <c:if test="${not empty mainPage.monitoringOfReportPage.htmlReport}">
            <h:outputText escape="true" value="#{mainPage.migrantsReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.monitoringOfReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
