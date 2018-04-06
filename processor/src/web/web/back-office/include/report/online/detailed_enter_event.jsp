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
<h:panelGrid id="detailedEnterEventReportPanel" binding="#{mainPage.detailedEnterEventReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.detailedEnterEventReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.detailedEnterEventReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Организация" />

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{mainPage.detailedEnterEventReportPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="enterEventFilter" escape="true" value=" {#{mainPage.detailedEnterEventReportPage.filter}}" />
        </h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.detailedEnterEventReportPage.buildReportHTML}"
                           reRender="detailedEnterEventReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.detailedEnterEventReportPage.generateXLS}"
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
        <c:if test="${not empty  mainPage.detailedEnterEventReportPage.htmlReport}">
            <h:outputText escape="true" value="Отчет по типам карт" styleClass="output-text" />
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.detailedEnterEventReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Подготовка отчета завершена успешно" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>

