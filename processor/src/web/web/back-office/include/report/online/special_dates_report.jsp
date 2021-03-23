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
                <f:setPropertyActionListener value="#{mainPage.specialDatesReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />

                <a4j:support event="onclick" action="#{mainPage.specialDatesReportPage.clean}"
                             reRender="clear" />
                <a4j:support event="onclick" action="#{mainPage.specialDatesReportPage.clean}"
                             reRender="clear" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.specialDatesReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText id="group" escape="true" value="Группа" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.specialDatesReportPage.clientGroupName}" styleClass="input-text"
                         style="margin-right: 2px;" disabled="#{mainPage.specialDatesReportPage.idOfOrgs == null}"/>
            <a4j:commandButton value="..." action="#{mainPage.showClientGroupSelectPage}" reRender="modalClientGroupSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" disabled="#{mainPage.specialDatesReportPage.idOfOrgs == null}">
                <f:param name="idOfOrg" value="#{mainPage.specialDatesReportPage.idOfOrgs}" />
                <f:setPropertyActionListener value="#{mainPage.specialDatesReportPage.idOfOrgs}" target="#{mainPage.clientGroupSelectPage.idOfOrg}" />
                <f:setPropertyActionListener value="#{null}" target="#{mainPage.clientGroupSelectPage.filter}" />
            </a4j:commandButton>
            <a4j:commandButton id="clear" value="Очистить" action="#{mainPage.specialDatesReportPage.clean}"
                               reRender="#{mainPage.topMostPage.pageComponent.id}"
                               styleClass="command-button" style="width: 80px; margin-left: 4px;"
                               disabled="#{mainPage.specialDatesReportPage.idOfOrgs == null}">
                <f:param name="idOfOrg" value="#{mainPage.clientGroupSelectPage.idOfOrg}" />
                <f:setPropertyActionListener value="" target="#{mainPage.clientGroupSelectPage.filter}" />
                <f:setPropertyActionListener value="#{mainPage.specialDatesReportPage.clientGroupName}" target="" />
                <a4j:support event="onclick" action="#{mainPage.clientGroupSelectPage.cancelFilter}"
                             reRender="modalClientGroupSelectorForm" />
            </a4j:commandButton>
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
        <h:selectBooleanCheckbox id="comment" value="#{mainPage.specialDatesReportPage.showComments}" styleClass="output-text" disabled="#{mainPage.specialDatesReportPage.idOfClientGroup == null}">
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
        <c:if test="${not empty mainPage.specialDatesReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по учебным дням" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.specialDatesReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>