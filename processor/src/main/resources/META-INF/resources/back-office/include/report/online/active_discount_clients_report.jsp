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

<%--@elvariable id="activeDiscountClientsReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.ActiveDiscountClientsReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{activeDiscountClientsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Начальная дата" />
        <rich:calendar value="#{activeDiscountClientsReportPage.startDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        <h:outputText styleClass="output-text" escape="true" value="Конечная дата" />
        <rich:calendar value="#{activeDiscountClientsReportPage.endDate}" datePattern="dd.MM.yyyy"
                       converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <%--<h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:selectOneMenu value="#{activeDiscountClientsReportPage.district}" styleClass="input-text">
            <f:selectItems value="#{activeDiscountClientsReportPage.districts}" />
        </h:selectOneMenu>--%>

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{activeDiscountClientsReportPage.filter}}" />
        </h:panelGroup>

        <a4j:commandButton value="Генерировать отчет" action="#{activeDiscountClientsReportPage.executeReport}"
                           reRender="workspaceTogglePanel, reportPanel"
                           styleClass="command-button" status="reportGenerateStatus" />
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>

    </h:panelGrid>
    <%--<h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty activeDiscountClientsReportPage.report && not empty activeDiscountClientsReportPage.report.htmlReport}" >
            <h:outputText escape="true" value="Отчет по питающимся льготникам" styleClass="output-text" />

            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent">
                        ${activeDiscountClientsReportPage.report.htmlReport}
                </div>
            </f:verbatim>

        </c:if>
    </h:panelGrid>--%>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">

        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />

        <c:if test="${!empty activeDiscountClientsReportPage.report && !empty activeDiscountClientsReportPage.report.items && !empty activeDiscountClientsReportPage.report.columnNames}" >
            <h:outputText escape="true" value="Отчет по питающимся льготникам" styleClass="output-text" />
            <rich:dataTable id="itemsReportTable" value="#{activeDiscountClientsReportPage.report.items}"
                            var="item" rowKeyVar="row" rows="100" footerClass="data-table-footer"
                            columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">

                <rich:column styleClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText value="" />
                    </f:facet>
                    <h:outputText value="" styleClass="output-text" />
                </rich:column>

                <rich:columns value="#{activeDiscountClientsReportPage.report.columnNames}" var="columnName"
                              styleClass="left-aligned-column" index="ind" headerClass="center-aligned-column">
                    <f:facet name="header">
                        <h:outputText escape="true" value="#{columnName}" />
                    </f:facet>
                    <h:outputText style="float: left;" escape="true"
                                  value="#{item.getRowValue(columnName)}"
                                  styleClass="output-text" />
                </rich:columns>

                <f:facet name="footer">
                    <rich:datascroller for="itemsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
        <%--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showSalesCSVList}" styleClass="command-button" />--%>
    </h:panelGrid>

</h:panelGrid>