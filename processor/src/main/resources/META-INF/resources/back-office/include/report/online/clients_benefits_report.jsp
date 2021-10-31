<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="clientsBenefitsReportPanelGrid" binding="#{mainPage.clientsBenefitsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.clientsBenefitsReportPage.filter}" readonly="true" styleClass="input-text long-field"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Скрыть столбцы с нулевыми значениями"
                      styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.clientsBenefitsReportPage.hideMissedColumns}" styleClass="output-text" />

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildClientsBenefitsReportPage }"
                           reRender="workspaceTogglePanel, clientsBenefitsReportTable"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${not empty mainPage.clientsBenefitsReportPage.clientsBenefitsReport && not empty mainPage.clientsBenefitsReportPage.clientsBenefitsReport.items}" >

            <h:outputText escape="true" value="Отчет по льготам клиентов" styleClass="output-text" />
            <rich:dataTable id="clientsBenefitsReportTable" value="#{mainPage.clientsBenefitsReportPage.clientsBenefitsReport.items}"
                            var="e" rowKeyVar="row" rows="50" footerClass="data-table-footer"
                            columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">


                <rich:column styleClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText value="№" />
                    </f:facet>
                    <h:outputText value="#{row + 1}" styleClass="output-text" />
                </rich:column>

                <rich:column styleClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText value="Наименование правила" />
                    </f:facet>
                    <h:outputText value="#{e.rule}" styleClass="output-text" />
                </rich:column>

                <c:if test="${req.values == null}">
                    <rich:columns value="#{mainPage.clientsBenefitsReportPage.clientsBenefitsReport.columnNames}"
                                  var="columnName" styleClass="left-aligned-column" index="ind" headerClass="center-aligned-column" style="#{req.getBackgoundColor(columnName)}" >
                        <f:facet name="header" >
                            <h:outputText escape="true" value="#{columnName}" />
                        </f:facet>
                        <h:outputText style="float: left;" escape="true" value="#{e.get(columnName)}" styleClass="output-text" />
                    </rich:columns>
                </c:if>


                <f:facet name="footer">
                    <rich:datascroller for="clientsBenefitsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>