<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="smsDeliveryReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.SmsDeliveryReportPage"--%>
<h:panelGrid id="smsDeliveryReportPanelGrid" binding="#{smsDeliveryReportPage.pageComponent}" styleClass="borderless-grid">
    <h:outputText styleClass="output-text" escape="true" value="Отчет содержит актуальные данные по синхронизациям, выполненным до сегодняшнего дня." />
    <h:outputText styleClass="output-text" escape="true" value="Для включения в отчет сегодняшних синхронизаций нажмите кнопку \"Пересчитать\"" />
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{smsDeliveryReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverterWithoutTZ" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{smsDeliveryReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverterWithoutTZ" inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <%--<h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{smsDeliveryReportPage.filter}}" />
        </h:panelGroup>--%>
        <h:panelGroup id="orgFilter">
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                               <%--disabled="#{mainPage.goodRequestsNewReportPage.applyUserSettings}"--%>
                <%--<f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />--%>
                <f:setPropertyActionListener value="#{smsDeliveryReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true"
                          value=" {#{smsDeliveryReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="false" value="Показать только ОО со статусом \"Обслуживается\"" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{smsDeliveryReportPage.isActiveState}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <h:outputText escape="false" value="Время от регистрации события до отправки в ЕМП" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{smsDeliveryReportPage.moreThanTwoMinutes}" styleClass="output-text">
        </h:selectBooleanCheckbox>

        <a4j:commandButton value="Генерировать отчет" action="#{smsDeliveryReportPage.buildReport}"
                           reRender="workspaceTogglePanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty smsDeliveryReportPage.report && not empty smsDeliveryReportPage.report.htmlReport}" >
            <h:outputText escape="true" value="Общая статистика по отправленным сообщениям информирования" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${smsDeliveryReportPage.report.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
    <h:commandButton value="Выгрузить в Excel" actionListener="#{smsDeliveryReportPage.showXLS}" styleClass="command-button" />
    <a4j:commandButton value="Пересчитать" actionListener="#{smsDeliveryReportPage.recalculateSyncData}" styleClass="command-button" reRender="msgs"/>
    <rich:messages id="msgs" styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>