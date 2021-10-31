<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: anvarov
  Date: 09.06.2017
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    function onstartloading(){
        jQuery(".command-button, .command-link").attr('disabled', 'disabled');
    }
    function onstoploading(){
        jQuery(".command-button, .command-link").attr('disabled', '');
    }
</script>

<h:panelGrid id="clientTransactionsReportPanelGrid" binding="#{mainPage.clientTransactionsReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.clientTransactionsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.clientTransactionsReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect" value="#{mainPage.clientTransactionsReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.clientTransactionsReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.clientTransactionsReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.clientTransactionsReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.clientTransactionsReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <rich:tabPanel switchType="ajax" selectedTab="#{mainPage.clientTransactionsReportPage.selectedTab}" width="500px">
        <rich:tab label="Организация" id="clientTransactionsReportOrgTab">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText styleClass="output-text" escape="true" value="Организация" />
                <h:panelGroup>
                    <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;">
                        <f:setPropertyActionListener value="#{mainPage.clientTransactionsReportPage.getStringIdOfOrgList}"
                                                     target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                    </a4j:commandButton>
                    <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.clientTransactionsReportPage.filter}}" id="clientTransactionsOrgFilter" />
                </h:panelGroup>
                <h:outputText escape="false" value="Включать все корпуса" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{mainPage.clientTransactionsReportPage.showAllBuildings}" styleClass="output-text">
                </h:selectBooleanCheckbox>
            </h:panelGrid>
        </rich:tab>
        <rich:tab label="Клиент" id="clientTransactionsReportClientTab">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText styleClass="output-text" escape="true" value="Клиент" />
                <h:panelGroup id="clientFilter">
                    <a4j:commandButton value="..."
                                       action="#{mainPage.showClientSelectListPage(mainPage.clientTransactionsReportPage.getClientList())}"
                                       reRender="modalClientListSelectorPanel,selectedClientList"
                                       oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" id="clientFilterButton">
                        <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                        <f:setPropertyActionListener value="#{mainPage.clientTransactionsReportPage.getStringClientList}"
                                                     target="#{mainPage.clientSelectListPage.clientFilter}" />
                    </a4j:commandButton>
                    <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                                  value=" {#{mainPage.clientTransactionsReportPage.filterClient}}" />
                </h:panelGroup>
            </h:panelGrid>
        </rich:tab>
    </rich:tabPanel>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Тип операции" styleClass="output-text" />
        <h:selectOneMenu id="operationType" value="#{mainPage.clientTransactionsReportPage.selectedOperationType}" >
            <f:selectItems value="#{mainPage.clientTransactionsReportPage.operationTypes}"/>
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.clientTransactionsReportPage.buildReportHTML}"
                           reRender="clientTransactionsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.clientTransactionsReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status onstart="onstartloading()" onstop="onstoploading()">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="clientTransactionsReportTable">
        <c:if test="${not empty mainPage.clientTransactionsReportPage.htmlReport}">
            <h:outputText escape="true" value="#{mainPage.clientTransactionsReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.clientTransactionsReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
