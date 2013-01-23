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

<%--@elvariable id="contragentCompletionReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ContragentCompletionReportPage"--%>
<h:panelGrid binding="#{contragentCompletionReportPage.pageComponent}" id="enterEventReportPanelGrid" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Поставщик по умолчанию" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{contragentCompletionReportPage.defaultSupplier.contragentName}" readonly="true"
                         styleClass="input-text" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                               reRender="modalContragentSelectorPanel"
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

        <a4j:commandButton value="Генерировать отчет" action="#{contragentCompletionReportPage.generate}"
                           reRender="mainMenu, workspaceTogglePanel, contragentCompletionReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="contragentCompletionReportTable" value="#{contragentCompletionReportPage.contragentCompletionItems}"
                        var="contragentCompletionItem" rowKeyVar="row">
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Организация / Агент"/>
                </f:facet>
                <h:outputText value="#{contragentCompletionItem.educationalInstitutionName}"/>
            </rich:column>
            <rich:columns  var="contragent" value="#{contragentCompletionReportPage.contragentList}">
                <f:facet name="header">
                    <h:outputText value="#{contragent.contragentName}"/>
                </f:facet>
                <h:outputText value="#{contragentCompletionItem.getContragentPayValue(contragent.idOfContragent) /100}">
                    <f:convertNumber pattern="#0.00"/>
                </h:outputText>
            </rich:columns>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Итого"/>
                </f:facet>
                <h:outputText value="#{contragentCompletionItem.totalSumByOrg / 100}">
                    <f:convertNumber pattern="#0.00"/>
                </h:outputText>
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>
</h:panelGrid>