<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 19.03.2021
  Time: 11:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="dishMenuReportPanel" binding="#{mainPage.complexMenuReportPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="filterComplexMenuReportPanel" columns="2">

        <h:outputText escape="true" value="Контрагент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectContragentPanel">
            <h:inputText value="#{mainPage.complexMenuReportPage.filter}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px; width: 275px;" />
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,registerStampReportPanelGrid"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="#{mainPage.complexMenuReportPage.classTypeTSP}" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectOrgsPanel">
            <a4j:commandButton value="..." action="#{mainPage.complexMenuReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.complexMenuReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.complexMenuReportPage.orgFilter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Выбор типа питания" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectidTypeFoodId}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getTypesOfFood()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Выбор рациона" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectDiet}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getTypesOfDiet()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Выбор возрастной группы" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectidAgeGroup}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getAgeGroup()}"/>
        </h:selectOneMenu>

        <h:outputText styleClass="output-text" escape="true" value="Архивные" />
        <h:selectOneMenu value="#{mainPage.complexMenuReportPage.selectArchived}" >
            <f:selectItems value="#{mainPage.complexMenuReportPage.getArchiveds()}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.complexMenuReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.contragentPreordersReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.complexMenuReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.complexMenuReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.complexMenuReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.complexMenuReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.complexMenuReportPage.onEndDateSpecified}" />
        </rich:calendar>

    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.complexMenuReportPage.buildHTMLReport}"
                           reRender="contragentPreordersReportTablePanel" styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Генерировать отчет в Excel"
                         action="#{mainPage.complexMenuReportPage.exportToXLS}"
                         styleClass="command-button" />
    </h:panelGrid>


</h:panelGrid>