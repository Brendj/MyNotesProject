<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .region {
        font-weight: bold;
        background-color: #E3F6FF;
    }

    .overall {
        font-weight: bold;
        background-color: #D5E7F0;
    }
</style>
<h:panelGrid id="autoEnterEventReportPanel" binding="#{mainPage.autoEnterEventReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.autoEnterEventReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text"
                       showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar,autoEnterEventReportPanel"
                         actionListener="#{mainPage.autoEnterEventReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.autoEnterEventReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.autoEnterEventReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar,autoEnterEventReportPanel"
                         actionListener="#{mainPage.autoEnterEventReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.autoEnterEventReportPage.endDate}"
                       datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect,autoEnterEventReportPanel"
                         actionListener="#{mainPage.autoEnterEventReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.autoEnterEventReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.autoEnterEventReportPage.allFriendlyOrgs}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.autoEnterEventReportPage.clientFilter.clientGroupId}" styleClass="input-text"
                         style="width: 145px;">
            <f:selectItems value="#{mainPage.autoEnterEventReportPage.clientFilter.clientGroupsCustomItems}" />
            <a4j:support event="onchange" reRender="showDeletedClients" />
        </h:selectOneMenu>
    </h:panelGrid>


    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.autoEnterEventReportPage.buildReportHTML}"
                           reRender="autoEnterEventReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.autoEnterEventReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty  mainPage.autoEnterEventReportPage.htmlReport}">
            <h:outputText escape="true" value="Сводный отчет по посещению" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.autoEnterEventReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>