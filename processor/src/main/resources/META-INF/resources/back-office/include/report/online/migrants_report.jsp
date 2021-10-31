<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Liya
  Date: 11.06.2016
  Time: 16:18
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="migrantsReportPanelGrid" binding="#{mainPage.migrantsReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.migrantsReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               disabled="#{mainPage.migrantsReportPage.applyUserSettings}"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.migrantsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.migrantsReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText escape="true" value="Показывать заявки за всё время" styleClass="output-text" />
        <h:selectBooleanCheckbox id="showAllMigrants"
                                 value="#{mainPage.migrantsReportPage.showAllMigrants}"
                                 styleClass="output-text">
            <a4j:support event="onchange" reRender="migrantsReportPanelGrid" action="#{mainPage.migrantsReportPage.initDateFilter()}"/>
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.migrantsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false" readonly="#{mainPage.migrantsReportPage.showAllMigrants}">
            <a4j:support event="onchanged" reRender="endDateCalendar,migrantsReportPanelGrid"
                         actionListener="#{mainPage.migrantsReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.migrantsReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;" readonly="#{mainPage.migrantsReportPage.showAllMigrants}">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.migrantsReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,migrantsReportPanelGrid"
                         actionListener="#{mainPage.migrantsReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.migrantsReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" readonly="#{mainPage.migrantsReportPage.showAllMigrants}">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,migrantsReportPanelGrid"
                         actionListener="#{mainPage.migrantsReportPage.onEndDateSpecified}" />
        </rich:calendar>
        <h:outputText escape="true" value="Выборка по дате" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.migrantsReportPage.selectedPeriodType}" style="width:180px;" readonly="#{mainPage.migrantsReportPage.showAllMigrants}">
            <f:selectItems value="#{mainPage.migrantsReportPage.migrantPeriodTypes}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Тип заявок" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.migrantsReportPage.migrantType}" style="width:180px;">
            <f:selectItems value="#{mainPage.migrantsReportPage.migrantTypes}" />
        </h:selectOneMenu>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.migrantsReportPage.buildReportHTML}"
                           reRender="migrantsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.migrantsReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="migrantsReportTable">
        <c:if test="${not empty mainPage.migrantsReportPage.htmlReport}">
            <h:outputText escape="true" value="#{mainPage.migrantsReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.migrantsReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>