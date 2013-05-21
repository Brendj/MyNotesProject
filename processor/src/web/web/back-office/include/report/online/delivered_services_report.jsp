<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="deliveredServicesReportPanelGrid" binding="#{mainPage.deliveredServicesReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.deliveredServicesReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildDeliveredServicesReport}"
                           reRender="mainMenu, workspaceTogglePanel, deliveredServicesReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.deliveredServicesReportPage.deliveredServicesReport && not empty mainPage.deliveredServicesReportPage.deliveredServicesReport.htmlReport}" >
            <h:outputText escape="true" value="Отчет по оказанным услугам" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                ${mainPage.deliveredServicesReportPage.deliveredServicesReport.htmlReport}
                </div>
            </f:verbatim>

            <%--<rich:dataTable id="deliveredServicesReportTable" value="#{mainPage.deliveredServicesReportPage.deliveredServicesReport.items}"
                            var="it" rowKeyVar="row" rows="10" footerClass="data-table-footer"
                            columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">




                <f:facet name="footer">
                    <rich:datascroller for="goodRequestsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
                                       stepControls="auto" boundaryControls="hide">
                        <f:facet name="previous">
                            <h:graphicImage value="/images/16x16/left-arrow.png" />
                        </f:facet>
                        <f:facet name="next">
                            <h:graphicImage value="/images/16x16/right-arrow.png" />
                        </f:facet>
                    </rich:datascroller>
                </f:facet>
            </rich:dataTable>  --%>
        </c:if>
        <!--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />-->
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>