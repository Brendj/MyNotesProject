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
<h:panelGrid id="typesOfCardReportPanel" binding="#{mainPage.typesOfCardReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Дата" />
        <rich:calendar value="#{mainPage.typesOfCardReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value="Группа" styleClass="output-text" />
        <h:selectOneMenu value="#{mainPage.clientListPage.clientFilter.clientGroupId}"
                         styleClass="input-text">
            <f:selectItems value="#{mainPage.clientListPage.clientFilter.clientGroupItems}" />
            <a4j:support event="onchange" reRender="typesOfCardReportPanel" />
        </h:selectOneMenu>
        <h:outputText escape="true" value="Итоговые данные по округу" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.typesOfCardReportPage.includeSummaryByDistrict}"
                                 styleClass="output-text">
            <a4j:support event="onchanged" reRender="typesOfCardReportPanel" />
        </h:selectBooleanCheckbox>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.typesOfCardReportPage.buildReportHTML}"
                           reRender="typesOfCardReportPanel" styleClass="command-button"
                           status="reportGenerateStatus" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{mainPage.typesOfCardReportPage.generateXLS}"
                         styleClass="command-button" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty  mainPage.typesOfCardReportPage.htmlReport}">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${mainPage.typesOfCardReportPage.htmlReport} </div>
            </f:verbatim>
            <h:outputText escape="true" value="Отчет по типам карт" styleClass="output-text" />
        </c:if>
    </h:panelGrid>
</h:panelGrid>