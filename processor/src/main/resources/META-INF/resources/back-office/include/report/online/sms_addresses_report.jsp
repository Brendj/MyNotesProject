<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2015. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

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

<%--@elvariable id="smsAddressesReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.SmsAddressesReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{smsAddressesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{smsAddressesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{smsAddressesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Клиент" />
        <h:panelGroup id="clientFilter">
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectListPage(smsAddressesReportPage.getClientList())}"
                               reRender="modalClientListSelectorPanel,selectedClientList"
                               oncomplete="if (#{facesContext.maximumSeverity == null})
                                        #{rich:component('modalClientListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="1" target="#{mainPage.clientSelectListPage.clientFilter}" />
                <f:setPropertyActionListener value="#{smsAddressesReportPage.getStringClientList}"
                                             target="#{mainPage.clientSelectListPage.clientFilter}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" id="selectedClientList"
                          value=" {#{smsAddressesReportPage.filter}}" />
        </h:panelGroup>

        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{smsAddressesReportPage.doGenerate}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{smsAddressesReportPage.doGenerateXLS()}" styleClass="command-button" />
    </h:panelGrid>


    <h:panelGrid styleClass="borderless-grid" id="reportPanel" columnClasses="center-aligned-column">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty smsAddressesReportPage.htmlReport}" >
            <f:verbatim>
                <div>${smsAddressesReportPage.htmlReport}</div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>