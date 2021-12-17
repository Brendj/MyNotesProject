<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="adjustmentPaymentReportPanelGrid" binding="#{mainPage.adjustmentPaymentReportPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true" headerClass="filter-panel-header"
                            width="800">
        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.adjustmentPaymentReportPage.filter}" readonly="true"
                             styleClass="input-text long-field" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <%--<h:outputText value="Показывать резервников: " styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{mainPage.adjustmentPaymentReportPage.showReserve}">
                <a4j:support event="onchange" reRender="adjustmentPaymentReportPanelGrid" />
            </h:selectBooleanCheckbox>--%>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.adjustmentPaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar,adjustmentPaymentReportPanelGrid"
                             actionListener="#{mainPage.adjustmentPaymentReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.adjustmentPaymentReportPage.periodTypeMenu.periodType}"
                             styleClass="input-text" style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.adjustmentPaymentReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar,adjustmentPaymentReportPanelGrid"
                             actionListener="#{mainPage.adjustmentPaymentReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.adjustmentPaymentReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect,adjustmentPaymentReportPanelGrid"
                             actionListener="#{mainPage.adjustmentPaymentReportPage.onEndDateSpecified}" />
            </rich:calendar>
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" columns="3">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.adjustmentPaymentReportPage.buildReportHTML}"
                           reRender="adjustmentPaymentReportPanelGrid" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel"
                         actionListener="#{mainPage.adjustmentPaymentReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:commandButton value="Очистить" action="#{mainPage.adjustmentPaymentReportPage.clear}"
                           reRender="adjustmentPaymentReportPanelGrid, lateParametrsGrid" styleClass="command-button"
                           status="reportGenerateStatus" />
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.adjustmentPaymentReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по ручной корректировке оплаты льготного питания"
                          styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.adjustmentPaymentReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>