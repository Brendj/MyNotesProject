<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 23.01.13
  Time: 14:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="contragentCompletionReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ContragentCompletionReportPage"--%>
<h:panelGrid binding="#{contragentCompletionReportPage.pageComponent}" id="contragentCompletionReportPanelGrid" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Поставщик" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{contragentCompletionReportPage.defaultSupplier.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{contragentCompletionReportPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel, orgPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="0" target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2" target="#{mainPage.classTypes}" />
            </a4j:commandButton>
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{contragentCompletionReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{contragentCompletionReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup id="orgPanel">
            <a4j:commandButton value="..." action="#{contragentCompletionReportPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <%--<f:setPropertyActionListener value="1" target="#{mainPage.orgListSelectPage.filterMode}" />--%>
                <f:setPropertyActionListener value="#{contragentCompletionReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{contragentCompletionReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
        <h:panelGroup>
            <h:selectOneMenu value="#{contragentCompletionReportPage.organizationTypeModify}" styleClass="input-text"
                             style="width: 250px;">
                <f:converter converterId="organizationTypeModifyConverter" />
                <f:selectItems value="#{contragentCompletionReportPage.organizationTypeModifyMenu.customItems}" />
            </h:selectOneMenu>
        </h:panelGroup>

        <h:outputText escape="false" value="Показать нулевые значения" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{contragentCompletionReportPage.showNullValues}" styleClass="output-text">
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:panelGrid columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{contragentCompletionReportPage.buildReportHTML}"
                           reRender="contragentCompletionReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Генерировать отчет в Excel" actionListener="#{contragentCompletionReportPage.showCSVList}" styleClass="command-button" />
    </h:panelGrid>

    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid styleClass="borderless-grid" id="contragentCompletionReportTable">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty contragentCompletionReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по обороту" styleClass="output-text" />
            <f:verbatim>
                <%--<style type="text/css">--%>
                <%--div.htmlReportContent :empty {--%>
                <%--display: none;--%>
                <%--}--%>
                <%--</style>--%>
                <div class="htmlReportContent"> ${contragentCompletionReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>

</h:panelGrid>