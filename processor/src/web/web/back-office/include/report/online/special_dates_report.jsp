<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Liya
  Date: 17.04.2016
  Time: 9:23
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="specialDatesReportPanelGrid" binding="#{mainPage.specialDatesReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.specialDatesReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               disabled="#{mainPage.specialDatesReportPage.applyUserSettings}"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.supplierFilter}" />
                <f:setPropertyActionListener value="#{mainPage.specialDatesReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.specialDatesReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.specialDatesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,specialDatesReportPanel"
                         actionListener="#{mainPage.specialDatesReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.specialDatesReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.specialDatesReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,specialDatesReportPanel"
                         actionListener="#{mainPage.specialDatesReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.specialDatesReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,specialDatesReportPanel"
                         actionListener="#{mainPage.specialDatesReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText escape="false" value="Показать комментарий" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.specialDatesReportPage.showComments}" styleClass="output-text">
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.specialDatesReportPage.buildReportHTML}"
                           reRender="specialDatesReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.specialDatesReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="specialDatesReportTable">
        <f:verbatim>
            <style type="text/css">
                div.htmlReportContent :empty {
                    display: none;
                }
            </style>
            <div class="htmlReportContent">
                    ${mainPage.specialDatesReportPage.htmlReport}
            </div>
        </f:verbatim>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>