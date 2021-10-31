<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2018. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="consolidatedSellingReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ConsolidatedSellingReportPage"--%>
<h:panelGrid id="consolidatedSellingReportPanelGrid" binding="#{consolidatedSellingReportPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{consolidatedSellingReportPage.contragent.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px; width: 275px;" />
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel,consolidatedSellingReportPanelGrid"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..." action="#{consolidatedSellingReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               style="width: 25px;" >
                <f:setPropertyActionListener value="#{consolidatedSellingReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{consolidatedSellingReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Все организации" />
        <h:selectBooleanCheckbox value="#{consolidatedSellingReportPage.showAllOrgs}" styleClass="output-text">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{consolidatedSellingReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,consolidatedSellingReportTable"
                         actionListener="#{consolidatedSellingReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{consolidatedSellingReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{consolidatedSellingReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,consolidatedSellingReportTable"
                         actionListener="#{consolidatedSellingReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{consolidatedSellingReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,consolidatedSellingReportTable"
                         actionListener="#{consolidatedSellingReportPage.onEndDateSpecified}" />
        </rich:calendar>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{consolidatedSellingReportPage.exportToHtml}"
                           reRender="consolidatedSellingReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{consolidatedSellingReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="consolidatedSellingReportTable">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${consolidatedSellingReportPage.htmlReport}
                </div>
            </f:verbatim>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>