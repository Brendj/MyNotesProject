<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="preorderStatsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.PreorderStatsReportPage"--%>
<h:panelGrid id="preorderStatsReportPanelGrid" binding="#{preorderStatsReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{preorderStatsReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel" disabled="#{preorderStatsReportPage.preorderOrgs}"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{preorderStatsReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{preorderStatsReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Все организации с функционалом \"Предзаказ\"" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{preorderStatsReportPage.preorderOrgs}" styleClass="output-text">
            <a4j:support event="onchange" reRender="preorderStatsReportPanelGrid" action="#{preorderStatsReportPage.preorderOrgsChange}" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{preorderStatsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,preorderStatsReportPanelGrid"
                         actionListener="#{preorderStatsReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{preorderStatsReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{preorderStatsReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,preorderStatsReportPanelGrid"
                         actionListener="#{preorderStatsReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{preorderStatsReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,preorderStatsReportPanelGrid"
                         actionListener="#{preorderStatsReportPage.onEndDateSpecified}" />
        </rich:calendar>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{preorderStatsReportPage.exportToHtml}"
                           reRender="preorderStatsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{preorderStatsReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="preorderStatsReportTable">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${preorderStatsReportPage.htmlReport}
                </div>
            </f:verbatim>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>