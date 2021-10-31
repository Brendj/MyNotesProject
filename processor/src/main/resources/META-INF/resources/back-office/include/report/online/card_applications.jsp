<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="cardApplicationReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CardApplicationReportPage"--%>
<h:panelGrid id="cardApplicationPanelGrid" binding="#{cardApplicationReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{cardApplicationReportPage.showOrgSelectPage()}"
                               reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{cardApplicationReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Клиент (учащийся)" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectListPage(cardApplicationReportPage.getClientList(), cardApplicationReportPage.idOfOrg)}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{cardApplicationReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{cardApplicationReportPage.stringClientList}}" />
        </h:panelGroup>

        <h:outputText value="Включить фильтр по датам: " styleClass="output-text"/>
        <h:selectBooleanCheckbox value="#{cardApplicationReportPage.enableDateFilter}">
            <a4j:support event="onchange" reRender="cardApplicationPanelGrid"/>
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Начальная дата" styleClass="output-text" />
        <rich:calendar value="#{cardApplicationReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" disabled="#{not cardApplicationReportPage.enableDateFilter}">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{cardApplicationReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{cardApplicationReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;"
                         disabled="#{not cardApplicationReportPage.enableDateFilter}">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{cardApplicationReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,cardApplicationPanelGrid"
                         actionListener="#{cardApplicationReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Конечная дата" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{cardApplicationReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" disabled="#{not cardApplicationReportPage.enableDateFilter}">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{cardApplicationReportPage.onEndDateSpecified}" />
        </rich:calendar>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{cardApplicationReportPage.buildReportHTML}"
                           reRender="cardApplicationReportTable" styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{cardApplicationReportPage.exportToXLS}"
                         styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="cardApplicationReportTable">
        <c:if test="${not empty cardApplicationReportPage.htmlReport}">
            <h:outputText escape="true" value="#{cardApplicationReportPage.reportName}" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${cardApplicationReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>