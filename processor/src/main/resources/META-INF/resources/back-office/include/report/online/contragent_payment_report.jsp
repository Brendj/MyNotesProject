<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="contragentPaymentReportPanelGrid" binding="#{mainPage.contragentPaymentReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
        <rich:calendar value="#{mainPage.contragentPaymentReportPage.startDate}" datePattern="dd.MM.yyyy HH:mm"
                       converter="timeMinuteConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDateCalendar"
                         actionListener="#{mainPage.contragentPaymentReportPage.onReportPeriodChanged}" />
        </rich:calendar>

        <h:outputText styleClass="output-text" escape="true" value="Интервал выборки" />
        <h:selectOneMenu id="endDatePeriodSelect"
                         value="#{mainPage.contragentPaymentReportPage.periodTypeMenu.periodType}"
                         styleClass="input-text" style="width: 250px;">
            <f:converter converterId="periodTypeConverter" />
            <f:selectItems value="#{mainPage.contragentPaymentReportPage.periodTypeMenu.items}" />
            <a4j:support event="onchange" reRender="endDateCalendar"
                         actionListener="#{mainPage.contragentPaymentReportPage.onReportPeriodChanged}" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
        <rich:calendar id="endDateCalendar" value="#{mainPage.contragentPaymentReportPage.endDate}"
                       datePattern="dd.MM.yyyy HH:mm" converter="timeMinuteConverter" inputClass="input-text" showWeeksBar="false">
            <a4j:support event="onchanged" reRender="endDatePeriodSelect"
                         actionListener="#{mainPage.contragentPaymentReportPage.onEndDateSpecified}" />
        </rich:calendar>

        <h:outputText escape="true" value="Агент по приему платежей" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPageOwn(false)}"
                               reRender="modalContragentListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.contragentListSelectPage.classTypesString}" />
                <f:setPropertyActionListener value="#{mainPage.contragentPaymentReportPage.contragentPaymentReceiverIds}" target="#{mainPage.contragentListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{mainPage.contragentPaymentReportPage.contragentPaymentReceiverFilter}}" escape="true" styleClass="output-text" />
        </h:panelGroup>

        <h:outputText escape="true" value="Контрагент-получатель" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <a4j:commandButton value="..."
                               action="#{mainPage.showContragentSelectPageOwn(true)}"
                               reRender="modalContragentListSelectorPanel, orgPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="2" target="#{mainPage.contragentListSelectPage.classTypesString}" />
                <f:setPropertyActionListener value="#{mainPage.contragentPaymentReportPage.contragentReceiverIds}" target="#{mainPage.contragentListSelectPage.selectedIds}" />
            </a4j:commandButton>
            <h:outputText value=" {#{mainPage.contragentPaymentReportPage.contragentReceiverFilter}}" escape="true" styleClass="output-text" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup id="orgPanel">
            <a4j:commandButton value="..." action="#{mainPage.contragentPaymentReportPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />
                <f:setPropertyActionListener value="#{mainPage.contragentPaymentReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{mainPage.contragentPaymentReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
        <h:panelGroup>
            <h:selectOneMenu value="#{mainPage.contragentPaymentReportPage.organizationTypeModify}" styleClass="input-text"
                             style="width: 250px;">
                <f:converter converterId="organizationTypeModifyConverter" />
                <f:selectItems value="#{mainPage.contragentPaymentReportPage.organizationTypeModifyMenu.customItems}" />
            </h:selectOneMenu>
        </h:panelGroup>

        <h:outputText escape="true" value="Терминал" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentPaymentReportPage.terminal}" styleClass="input-text" />

        <h:outputText escape="true" value="Идентификатор платежа" styleClass="output-text" />
        <h:inputText value="#{mainPage.contragentPaymentReportPage.paymentIdentifier}" styleClass="input-text" />

    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.contragentPaymentReportPage.buildReport}"
                           reRender="workspaceTogglePanel" styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Генерировать отчет в Excel"
                         actionListener="#{mainPage.contragentPaymentReportPage.exportToXLS}"
                         styleClass="command-button" />
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
        <c:if test="${not empty mainPage.contragentPaymentReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по платежам" styleClass="output-text" />
            <f:verbatim>
                <div class="htmlReportContent"> ${mainPage.contragentPaymentReportPage.htmlReport} </div>
            </f:verbatim>
            <c:if test="${not empty mainPage.contragentPaymentReportPage.emptyData}">
                <h:outputText escape="true" value=" #{mainPage.contragentPaymentReportPage.emptyData}" styleClass="input-text"/>
            </c:if>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

</h:panelGrid>