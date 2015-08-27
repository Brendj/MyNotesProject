<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="LatePaymentReportPanelGrid" binding="#{mainPage.latePaymentReportPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Настройки отчета" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid id="lateParametrsGrid" styleClass="borderless-grid" columns="2">

            <h:outputText styleClass="output-text" escape="true" value="Организация" />
            <h:panelGroup>
                <a4j:commandButton value="..." action="#{mainPage.latePaymentReportPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                    <f:setPropertyActionListener value="#{mainPage.latePaymentReportPage.getStringIdOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" escape="true"
                              value=" {#{mainPage.latePaymentReportPage.filter}}" />
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="С разбивкой по корпусам" />
            <h:panelGroup>
                <h:selectOneMenu value="#{mainPage.latePaymentReportPage.organizationTypeModify}"
                                 styleClass="input-text" style="width: 250px;">
                    <f:converter converterId="organizationTypeModifyConverter" />
                    <f:selectItems value="#{mainPage.latePaymentReportPage.organizationTypeModifyMenu.customItems}" />
                </h:selectOneMenu>
            </h:panelGroup>

            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{mainPage.latePaymentReportPage.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDateCalendar"
                             actionListener="#{mainPage.latePaymentReportPage.onReportPeriodChanged}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
            <h:selectOneMenu id="endDatePeriodSelect"
                             value="#{mainPage.latePaymentReportPage.periodTypeMenu.periodType}" styleClass="input-text"
                             style="width: 250px;">
                <f:converter converterId="periodTypeConverter" />
                <f:selectItems value="#{mainPage.latePaymentReportPage.periodTypeMenu.items}" />
                <a4j:support event="onchange" reRender="endDateCalendar"
                             actionListener="#{mainPage.latePaymentReportPage.onReportPeriodChanged}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar id="endDateCalendar" value="#{mainPage.latePaymentReportPage.endDate}"
                           datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text"
                           showWeeksBar="false">
                <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                             actionListener="#{mainPage.latePaymentReportPage.onEndDateSpecified}" />
            </rich:calendar>

            <h:outputText styleClass="output-text" escape="true" value="Количество несвоевременной оплаты за 1 день" />
            <h:panelGroup>
                <h:selectOneMenu value="#{mainPage.latePaymentReportPage.latePaymentByOneDayCountType}"
                                 styleClass="input-text" style="width: 250px;">
                    <f:converter converterId="latePaymentByOneDayCountConverter" />
                    <f:selectItems value="#{mainPage.latePaymentReportPage.latePaymentByOneDayCountTypeMenu.items}" />
                </h:selectOneMenu>
            </h:panelGroup>

            <h:outputText styleClass="output-text" escape="true" value="Количество дней несвоевременной оплаты" />
            <h:panelGroup>
                <h:selectOneMenu value="#{mainPage.latePaymentReportPage.latePaymentDaysCountType}"
                                 styleClass="input-text" style="width: 250px;">
                    <f:converter converterId="latePaymentDaysCountTypeConverter" />
                    <f:selectItems value="#{mainPage.latePaymentReportPage.latePaymentDaysCountTypeMenu.items}" />
                </h:selectOneMenu>
            </h:panelGroup>

        </h:panelGrid>
    </rich:simpleTogglePanel>
</h:panelGrid>