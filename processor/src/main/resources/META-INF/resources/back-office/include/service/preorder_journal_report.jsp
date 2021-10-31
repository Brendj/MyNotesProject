<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="preorderJournalReportPage" type="ru.axetta.ecafe.processor.web.ui.service.PreorderJournalReportPage"--%>
<h:panelGrid id="preorderJournalPanelGrid" binding="#{preorderJournalReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Список организаций" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{preorderJournalReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{preorderJournalReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Физ. лицо" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectListPage(preorderJournalReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{preorderJournalReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{preorderJournalReportPage.stringClientList}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{preorderJournalReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{preorderJournalReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{preorderJournalReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{preorderJournalReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,preorderJournalPanelGrid"
                         actionListener="#{preorderJournalReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{preorderJournalReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{preorderJournalReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{preorderJournalReportPage.buildReportHTML}"
                           reRender="preorderJournalPanelGrid" styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{preorderJournalReportPage.exportToXLS}"
                         styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="preordersReportTable">
        <c:if test="${not empty preorderJournalReportPage.htmlReport}">
            <h:outputText escape="true" value="#{preorderJournalReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${preorderJournalReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
