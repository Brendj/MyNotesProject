<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="webtechnologistCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogListPage"--%>
<h:panelGrid id="webtechnologistCatalogListPagePanelGrid" binding="#{webtechnologistCatalogListPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Фильтр " switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="false" headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{webtechnologistCatalogListPage.catalogNameFilter}"
                         styleClass="input-text" />

            <h:outputText escape="true" value="GUID справочника" styleClass="output-text" />
            <h:inputText value="#{webtechnologistCatalogListPage.GUIDfilter}" maxlength="36"
                         styleClass="input-text" size="50" />
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{webtechnologistCatalogListPage.updateCatalogList}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{webtechnologistCatalogListPage.dropAndReloadCatalogList}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <rich:modalPanel id="webtechnologistCatalogElementListPanel" width="600" height="600" style="overflow: scroll;">
        <rich:dataTable id="webtechnologistCatalogListTable" value="#{webtechnologistCatalogListPage.selectedItem.items}" var="catalogElement" rows="50"
                        footerClass="data-table-footer"
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
            <h:outputText escape="true" value="#{catalogElement.lastUpdate}" styleClass="output-text" >
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
            </h:outputText>
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
        <f:facet name="controls">
            <a4j:commandLink onclick="Richfaces.hideModalPanel('webtechnologistCatalogItemListPanel')" reRender="this" style="color: white;">
                <h:outputText value="Закрыть" />
            </a4j:commandLink>
        </f:facet>
    </rich:modalPanel>
    <rich:dataTable id="webtechnologistCatalogListTable" value="#{webtechnologistCatalogListPage.itemList}" var="item" rows="10"
                      footerClass="data-table-footer"
                      columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название справочника" />
        </f:facet>
        <h:outputText escape="true" value="#{item.catalogName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="GUID" />
        </f:facet>
        <h:outputText escape="true" value="#{item.GUID}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Создал пользователь" />
        </f:facet>
        <h:outputText escape="true" value="#{item.userCreator.userName}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Состояние" />
        </f:facet>
        <h:outputText escape="true" value="#{item.deleteStateAsString}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Дата создания" />
        </f:facet>
        <h:outputText escape="true" value="#{item.createDate}" styleClass="output-text">
            <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
        </h:outputText>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Последнее обновление" />
        </f:facet>
        <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text" >
            <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
        </h:outputText>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Состав каталога" />
        </f:facet>
        <a4j:commandButton value="Просмотр" rendered="#{not empty item.items}" ajaxSingle="true" oncomplete="Richfaces.showModalPanel('webtechnologistCatalogItemListPanel');"
                         reRender="errorPanel" styleClass="command-link">
            <f:setPropertyActionListener value="#{item}" target="#{webtechnologistCatalogListPage.selectedItem}" />
        </a4j:commandButton>
        <h:outputText escape="true" value="Элементы отсуствуют" rendered="#{empty item.items}"/>
    </rich:column>

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText value="Удалить" escape="true"/>
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         reRender="uvDeleteConfirmPanel"
                         action="#{uvDeletePage.show}"
                         oncomplete="#{rich:component('uvDeleteConfirmPanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item}" target="#{uvDeletePage.currentEntityItem}" />
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


