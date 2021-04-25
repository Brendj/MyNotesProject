<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 20.04.2021
  Time: 10:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="foodDaysCalendarReportPanelGrid" binding="#{mainPage.foodDaysCalendarReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.foodDaysCalendarReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               disabled="#{mainPage.foodDaysCalendarReportPage.applyUserSettings}"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.foodDaysCalendarReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                <a4j:support event="onclick" action="#{mainPage.clearGroupListSelectedItemsList}"
                             reRender="groupSelectpanel" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.foodDaysCalendarReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText id="groupList" escape="true" value="Выбор групп" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id = "groupSelectpanel">
            <a4j:commandButton value="..." action="#{mainPage.showClientGroupListSelectPage}" reRender="modalClientGroupSelectorListPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorListPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:param name="idOfOrg" value="#{mainPage.foodDaysCalendarReportPage.getStringIdOfOrgList}" />
                <f:setPropertyActionListener value="#{null}" target="#{mainPage.clientGroupListSelectPage.filter}" />
            </a4j:commandButton>
            <h:outputText value="{#{mainPage.clientGroupListSelectPage.selectedItems}}" styleClass="input-text"
                         style="margin-left: 4px;" />
        </h:panelGroup>

        <h:outputText escape="false" value="Построить по всем зданиям ОО" styleClass="output-text" />
        <h:selectBooleanCheckbox id="comment" value="#{mainPage.foodDaysCalendarReportPage.allOrg}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.foodDaysCalendarReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.foodDaysCalendarReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.foodDaysCalendarReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.foodDaysCalendarReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.foodDaysCalendarReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.foodDaysCalendarReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,foodDaysCalendarReportPanel"
                         actionListener="#{mainPage.foodDaysCalendarReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.foodDaysCalendarReportPage.buildReportHTML}"
                           reRender="foodDaysCalendarReportTable"
                           styleClass="command-button">
        <f:setPropertyActionListener value="#{mainPage.clientGroupListSelectPage.items}" target="#{mainPage.foodDaysCalendarReportPage.clientGroup}" />
        </a4j:commandButton>
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.foodDaysCalendarReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="foodDaysCalendarReportTable">
        <c:if test="${not empty mainPage.foodDaysCalendarReportPage.htmlReport}">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.foodDaysCalendarReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    
</h:panelGrid>