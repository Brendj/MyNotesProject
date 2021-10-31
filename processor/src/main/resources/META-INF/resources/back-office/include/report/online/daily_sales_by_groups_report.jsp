<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="dailySalesByGroupsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.DailySalesByGroupsReportPage"--%>
<h:panelGrid id="dailySalesByGroupsReportPagePanelGrid" binding="#{dailySalesByGroupsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{dailySalesByGroupsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" >
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{dailySalesByGroupsReportPage.onReportPeriodChanged}" />
                       </rich:calendar>
        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{dailySalesByGroupsReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 190px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{dailySalesByGroupsReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{dailySalesByGroupsReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar id="endDateCalendar" value="#{dailySalesByGroupsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" >
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{dailySalesByGroupsReportPage.onEndDateSpecified}" />
                       </rich:calendar>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="0" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{dailySalesByGroupsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                               </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{dailySalesByGroupsReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Включать комплексы" styleClass="output-text" rendered="false"/>
        <h:inputText value="#{dailySalesByGroupsReportPage.includeComplex}" styleClass="output-text" rendered="false"/>

        <h:outputText escape="true" value="Группировать по группам меню" styleClass="output-text" rendered="false"/>
        <h:inputText value="#{dailySalesByGroupsReportPage.groupByMenuGroup}" styleClass="output-text" rendered="false"/>

        <h:outputText escape="true" value="Группы меню" styleClass="output-text" rendered="false"/>
        <h:inputText value="#{dailySalesByGroupsReportPage.menuGroups}" styleClass="output-text" rendered="false"/>

        <h:outputText escape="true" value="Включать все корпуса организации" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{dailySalesByGroupsReportPage.includeFriendlyOrgs}"
                                     styleClass="output-text" />

        <h:outputText escape="true" value="Показывать только организации с функционалом \"Предзаказ\"" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{dailySalesByGroupsReportPage.preordersOnly}"
                                 styleClass="output-text" />

        <a4j:commandButton value="Генерировать отчет" action="#{dailySalesByGroupsReportPage.buildReport}"
                           reRender="workspaceTogglePanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty dailySalesByGroupsReportPage.htmlReport}" >
            <h:outputText escape="true" value="Отчет по оказанным услугам" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${dailySalesByGroupsReportPage.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
    <h:commandButton value="Выгрузить в Excel" actionListener="#{dailySalesByGroupsReportPage.showCSVList}" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>