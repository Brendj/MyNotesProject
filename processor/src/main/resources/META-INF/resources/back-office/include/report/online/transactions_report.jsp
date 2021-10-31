<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
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

<%--@elvariable id="transactionsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.TransactionsReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{transactionsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{transactionsReportPage.filter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
        <h:outputText escape="false" value="Построить по всем дружественным организациям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{transactionsReportPage.allFriendlyOrgs}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{transactionsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{transactionsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <a4j:commandButton value="Генерировать отчет" action="#{transactionsReportPage.doGenerate}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{transactionsReportPage.doGenerateXLS}" styleClass="command-button" />
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty transactionsReportPage.report && not empty transactionsReportPage.report.htmlReport}" >
            <h:outputText escape="true" value="Отчет по транзакциям" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${transactionsReportPage.report.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
</h:panelGrid>