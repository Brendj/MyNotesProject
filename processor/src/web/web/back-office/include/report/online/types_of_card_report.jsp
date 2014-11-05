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
<%--@elvariable id="typesOfCardReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.types.card.TypesOfCardReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{typesOfCardReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Дата" />
        <rich:calendar value="#{typesOfCardReportPage.startDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                       inputClass="input-text" showWeeksBar="false" />

        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{typesOfCardReportPage.clientListPage.clientFilter.clientGroupId}"
                         styleClass="input-text">
            <f:selectItems value="#{typesOfCardReportPage.clientListPage.clientFilter.clientGroupItems}" />
            <a4j:support event="onchange" reRender="showDeletedClients" />
        </h:selectOneMenu>

        <a4j:commandButton value="Генерировать отчет" action="#{typesOfCardReportPage.doGenerate}"
                           reRender="workspaceTogglePanel, reportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{typesOfCardReportPage.doGenerateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty transactionsReportPage.report && not empty typesOfCardReportPage.report.htmlReport}">
            <h:outputText escape="true" value="Отчет по транзакциям" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${typesOfCardReportPage.report.htmlReport} </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
</h:panelGrid>