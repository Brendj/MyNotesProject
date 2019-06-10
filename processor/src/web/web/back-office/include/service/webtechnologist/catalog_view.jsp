<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="webTechnologistCatalogViewPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogViewPage"--%>
<h:panelGrid id="webTechnologistCatalogListPagePanelGrid" binding="#{webTechnologistCatalogViewPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.catalogName}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="GUID справочника" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.GUID}" maxlength="36" styleClass="input-text"
                     readonly="true" size="50" />
        <h:outputText escape="true" value="Создан пользователем" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.userCreator}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Состояние" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.deleteState}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Дата создания" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.createDate}" styleClass="input-text" readonly="true">
            <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
        </h:inputText>
        <h:outputText escape="true" value="Дата последнего обновления" styleClass="output-text" />
        <h:inputText value="#{webTechnologistCatalogViewPage.lastUpdate}" styleClass="input-text" readonly="true">
            <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
        </h:inputText>
    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <!-- TODO finish edit_Page -->
        <a4j:commandButton value="Применить" action="#{webTechnologistCatalogViewPage.}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>
    <rich:dataTable id="webtechnologistCatalogListElementsTable" value="#{webTechnologistCatalogViewPage.items}"
                    var="catalogElement" rows="50" footerClass="data-table-footer"
                    columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Описание" />
            </f:facet>
            <h:outputText escape="true" value="#{catalogElement.description}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID" />
            </f:facet>
            <h:outputText escape="true" value="#{catalogElement.GUID}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Состояние" />
            </f:facet>
            <h:outputText escape="true" value="#{catalogElement.deleteStateAsString}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания" />
            </f:facet>
            <h:outputText escape="true" value="#{catalogElement.createDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последнее обновление" />
            </f:facet>
            <h:outputText escape="true" value="#{catalogElement.lastUpdate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
            </h:outputText>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="webtechnologistCatalogListElementsTable" renderIfSinglePage="false" maxPages="5"
                               fastControls="hide" stepControls="auto" boundaryControls="hide">
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