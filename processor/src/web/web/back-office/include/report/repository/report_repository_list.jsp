<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="reportRepositoryListPage" type="ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryListPage"--%>
<h:panelGrid id="reportRepListPanelGrid" binding="#{reportRepositoryListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр (#{reportRepositoryListPage.filter.status})" switchType="client" opened="true"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название правила" styleClass="output-text" />
            <rich:comboBox value="#{reportRepositoryListPage.filter.ruleName}" width="250px" styleClass="input-text">
                <f:selectItems value="#{reportRepositoryListPage.ruleNameItems}" />
            </rich:comboBox>
            <h:outputText escape="true" value="Тэг" styleClass="output-text" />
            <h:inputText value="#{reportRepositoryListPage.filter.tag}" styleClass="input-text" />
            <h:outputText escape="true" value="Название отчета" styleClass="output-text" />
            <h:inputText value="#{reportRepositoryListPage.filter.reportName}" styleClass="input-text" />
            <h:outputText escape="true" value="Номер организации" styleClass="output-text" />
            <h:inputText value="#{reportRepositoryListPage.filter.orgNum}" styleClass="input-text" />
            <h:outputText escape="true" value="Дата создания" styleClass="output-text" />
            <rich:calendar value="#{reportRepositoryListPage.filter.createdDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
            <rich:calendar value="#{reportRepositoryListPage.filter.startDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
            <rich:calendar value="#{reportRepositoryListPage.filter.endDate}" datePattern="dd.MM.yyyy"
                           converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{reportRepositoryListPage.reload}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{reportRepositoryListPage.resetFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:dataTable id="contractListTable" value="#{reportRepositoryListPage.itemList}" var="item" rows="50"
                    footerClass="data-table-footer"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Правило" />
            </f:facet>
            <h:outputText escape="true" value="#{item.ruleName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Название" />
            </f:facet>
            <h:outputText escape="true" value="#{item.reportName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тэг" />
            </f:facet>
            <h:outputText escape="true" value="#{item.tag}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер организации" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgNum}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Формат" />
            </f:facet>
            <h:outputText escape="true" value="#{item.documentFormatAsString}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createdDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Время формирования" />
            </f:facet>
            <h:outputText escape="true" value="#{item.generationTime} мс." styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата выборки от" />
            </f:facet>
            <h:outputText escape="true" value="#{item.startDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата выборки до" />
            </f:facet>
            <h:outputText escape="true" value="#{item.endDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Загрузить файл" />
            </f:facet>
            <a4j:commandLink value="#{item.reportFile}" action="#{reportRepositoryListPage.downloadReportFile}"
                             styleClass="command-link" reRender="mainMenu, workspaceForm">
                <f:setPropertyActionListener value="#{item}" target="#{reportRepositoryListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="contractListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>