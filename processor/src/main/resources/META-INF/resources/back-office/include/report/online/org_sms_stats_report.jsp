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

<%--@elvariable id="orgSmsStatsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.OrgSmsStatsReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{orgSmsStatsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <%--<h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{orgSmsStatsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{orgSmsStatsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />--%>

        <%--<h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:selectOneMenu value="#{activeDiscountClientsReportPage.district}" styleClass="input-text">
            <f:selectItems value="#{activeDiscountClientsReportPage.districts}" />
        </h:selectOneMenu>--%>

        <a4j:commandButton value="Генерировать отчет" action="#{orgSmsStatsReportPage.doGenerate}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty orgSmsStatsReportPage.report && not empty orgSmsStatsReportPage.report.htmlReport}" >
            <h:outputText escape="true" value="Общая статистика по информированию" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${orgSmsStatsReportPage.report.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>
    <h:commandButton value="Выгрузить в Excel" actionListener="#{orgSmsStatsReportPage.doGenerateXLS}" styleClass="command-button" />
</h:panelGrid>