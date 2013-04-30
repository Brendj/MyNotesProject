<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="goodRequestReportPanelGrid" binding="#{mainPage.goodRequestReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{mainPage.goodRequestReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{mainPage.goodRequestReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Товар" />
        <h:inputText value="#{mainPage.goodRequestReportPage.goodName}" styleClass="input-text" size="50" />

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.goodRequestReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText styleClass="output-text" escape="true" value="Поставщик" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.goodRequestReportPage.filter}}" />
        </h:panelGroup>

        <h:outputText escape="true" value="Скрывать даты с пустыми значениями"
                      styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.goodRequestReportPage.hideMissedColumns}" styleClass="output-text" />

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildGoodRequestReport}"
                           reRender="mainMenu, workspaceTogglePanel, goodRequestsReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.goodRequestReportPage.goodRequestsReport && not empty mainPage.goodRequestReportPage.goodRequestsReport.goodRequestItems}" >
        <h:outputText escape="true" value="Отчет по заявкам организаций" styleClass="output-text" />
        <rich:dataTable id="goodRequestsReportTable" value="#{mainPage.goodRequestReportPage.goodRequestsReport.goodRequestItems}"
                        var="req" rowKeyVar="row" rows="10" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">


            <rich:column styleClass="center-aligned-column">
                <f:facet name="header">
                    <h:outputText value="№" />
                </f:facet>
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>

            <c:if test="${req.values == null}">
                <rich:columns value="#{mainPage.goodRequestReportPage.goodRequestsReport.columnNames}"
                              var="columnName" styleClass="left-aligned-column" index="ind" headerClass="center-aligned-column" >
                    <f:facet name="header" >
                        <h:outputText escape="true" value="#{columnName}" />
                    </f:facet>
                    <h:outputText style="float: left;" escape="true" value="#{req.getValue(columnName)}" styleClass="output-text" />
                </rich:columns>
            </c:if>


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
        </rich:dataTable>
        </c:if>
        <!--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />-->
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>