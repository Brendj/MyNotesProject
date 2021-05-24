<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<h:panelGrid id="contragentPreordersReportPanelGrid" binding="#{mainPage.contragentPreordersReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.contragentPreordersReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.contragentPreordersReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.contragentPreordersReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.contragentPreordersReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.contragentPreordersReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.contragentPreordersReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.contragentPreordersReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectContragentPanel">
            <h:inputText value="#{mainPage.contragentPreordersReportPage.filter}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px; width: 275px;" />
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,registerStampReportPanelGrid"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="#{mainPage.contragentPreordersReportPage.classTypeTSP}" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div" id="contragentPreordersReportPageSelectOrgsPanel">
            <a4j:commandButton value="..." action="#{mainPage.contragentPreordersReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               style="width: 25px;">
                <f:setPropertyActionListener value="#{mainPage.contragentPreordersReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.contragentPreordersReportPage.orgFilter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Показывать только неоплаченные позиции" />
        <h:selectBooleanCheckbox value="#{mainPage.contragentPreordersReportPage.showOnlyUnpaidItems}" styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>
    <h:panelGrid columns="2">
    <a4j:commandButton value="Генерировать отчет" action="#{mainPage.contragentPreordersReportPage.buildHTMLReport}"
                       reRender="contragentPreordersReportTablePanel" styleClass="command-button" status="reportGenerateStatus" />
    <h:commandButton value="Генерировать отчет в Excel"
                     action="#{mainPage.contragentPreordersReportPage.exportToXLS}"
                     styleClass="command-button" />
</h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="contragentPreordersReportTablePanel">
        <c:if test="${not empty mainPage.contragentPreordersReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по предварительным заказам" styleClass="output-text" />
            <f:verbatim>
                <div class="htmlReportContent"> ${mainPage.contragentPreordersReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>