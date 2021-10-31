<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель посмотра списка правил обработки автоматических отчетов --%>
<rich:dataTable id="reportRuleTable" binding="#{mainPage.reportRuleListPage.pageComponent}"
                value="#{mainPage.reportRuleListPage.rules}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.idOfReportHandleRule}" action="#{mainPage.showReportRuleViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfReportRule}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.ruleName}" action="#{mainPage.showReportRuleViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfReportRule}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тэг" />
        </f:facet>
        <h:outputText escape="true" value="#{item.tag}" styleClass="output-text"/>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Включено" />
        </f:facet>
        <h:selectBooleanCheckbox disabled="true" readonly="true" value="#{item.enabled}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип отчета" />
        </f:facet>
        <h:outputText escape="true" value="#{item.reportType}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Формат отчета" />
        </f:facet>
        <h:outputText escape="true" value="#{item.documentFormat}" styleClass="output-text"
                      converter="reportFormatConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тема письма" />
        </f:facet>
        <h:outputText escape="true" value="#{item.subject}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Список адресов" />
        </f:facet>
        <h:outputText escape="true" value="#{item.routeAddresses}" styleClass="output-text"
                      converter="addressListConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showReportRuleEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfReportRule}" />
        </a4j:commandLink>
    </rich:column>
    <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditReports}">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('reportRuleDeletePanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.removedIdOfReportRule}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="reportRuleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
