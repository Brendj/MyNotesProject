<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: anvarov
  Date: 04.04.18
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid binding="#{mainPage.enterEventJournalReportPage.pageComponent}" id="enterEventJournalReportPanelGrid"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.enterEventJournalReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,enterEventJournalReportPanelGrid"
                         actionListener="#{mainPage.enterEventJournalReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.enterEventJournalReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.enterEventJournalReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,enterEventJournalReportPanelGrid"
                         actionListener="#{mainPage.enterEventJournalReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.enterEventJournalReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,enterEventJournalReportPanelGrid"
                         actionListener="#{mainPage.enterEventJournalReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.enterEventJournalReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.enterEventJournalReportPage.allFriendlyOrgs}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Отображать колонку \"Кружок/секция\"" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.enterEventJournalReportPage.outputMigrants}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Сортировать по кружку/секции" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.enterEventJournalReportPage.sortedBySections}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.enterEventJournalReportPage.clientFilter.clientGroupId}"
                         styleClass="input-text" style="width: 145px;">
            <f:selectItems value="#{mainPage.enterEventJournalReportPage.clientFilter.clientGroupsCustomItems}" />
            <a4j:support event="onchange" reRender="showDeletedClients" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Клиент" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..."
                               action="#{mainPage.showClientSelectListPage(mainPage.enterEventJournalReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{mainPage.enterEventJournalReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{mainPage.enterEventJournalReportPage.filterClient}}" />
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="События" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.enterEventJournalReportPage.selectedEventFilter}"
                         styleClass="input-text" style="width: 145px;">
            <f:selectItems value="#{mainPage.enterEventJournalReportPage.eventFilter}" />
            <a4j:support event="onchange" reRender="showDeletedClients" />
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.enterEventJournalReportPage.buildReportHTML}"
                           reRender="workspaceTogglePanel, enterEventReportTable" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.enterEventJournalReportPage.generateXLS}"
                         styleClass="command-button" />
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty  mainPage.enterEventJournalReportPage.htmlReport}">
            <h:outputText escape="true" value="Журнал посещений" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.enterEventJournalReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>
