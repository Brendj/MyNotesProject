<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="groupItemCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.GroupItemCatalogListPage"--%>
<h:panelGrid id="webTechnologistgroupItemCatalogItemListPagePanelGrid" binding="#{groupItemCatalogListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название элемента" styleClass="output-text" />
            <h:inputText value="#{groupItemCatalogListPage.descriptionFilter}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{groupItemCatalogListPage.updateCatalogList()}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="WebTechnologgroupItemCatalogItemProcessStatus"  />
            <a4j:commandButton value="Очистить" action="#{groupItemCatalogListPage.dropAndReloadCatalogList()}"
                               status="WebTechnologgroupItemCatalogItemProcessStatus" reRender="workspaceTogglePanel"
                               styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="WebTechnologgroupItemCatalogItemProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <!-- ********* CREATE MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistgroupItemItemCreatePanel" minWidth="150" minHeight="70" resizeable="false" domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Создание Элемента" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="webtechnologistgroupItemItemCreatePanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Описание элемента" styleClass="output-text" />
            <h:inputText value="#{groupItemCatalogListPage.descriptionForNewItem}" styleClass="input-text" />
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistgroupItemItemCreatePanel')" value="Закрыть"
                               status="WebTechnologgroupItemCatalogItemProcessStatus"  action="#{groupItemCatalogListPage.clearDescriptionForNewCatalog()}" reRender="webtechnologistgroupItemItemCreatePanel"/>
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistgroupItemItemCreatePanel')"
                               reRender="webtechnologistgroupItemCatalogItemListTable, webtechnologistgroupItemItemCreatePanel" action="#{groupItemCatalogListPage.createNewItem()}"
                               status="WebTechnologgroupItemCatalogItemProcessStatus" value="Создать" />
        </h:panelGrid>
    </rich:modalPanel>
    <!-- Main page -->
    <rich:dataTable id="webtechnologistgroupItemCatalogItemListTable" value="#{groupItemCatalogListPage.catalogListItem}" var="item" rows="30"
                    footerClass="data-table-footer"
                    columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Описание" />
            </f:facet>
            <h:inputText value="#{item.description}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" />
            </f:facet>
            <a4j:commandLink  styleClass="command-link"
                              action="#{groupItemCatalogListPage.deleteItem()}"
                              reRender="webtechnologistgroupItemCatalogItemListTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}"
                                             target="#{groupItemCatalogListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="webtechnologistgroupItemCatalogItemListTable" renderIfSinglePage="false" maxPages="5"
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
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Создать новый элемент"
                           onclick="Richfaces.showModalPanel('webtechnologistgroupItemItemCreatePanel');"
                           id="showCreateGroupItemItemModalPanelButton" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить изменения"
                           action="#{groupItemCatalogListPage.applyChanges()}"
                           reRender="webtechnologistgroupItemCatalogItemListTable"
                           id="GroupItemApplyChangeButton"
                           status="WebTechnologgroupItemCatalogItemProcessStatus"/>

        <a4j:commandButton value="Восстановить значения"
                           action="#{groupItemCatalogListPage.refreshItems()}"
                           reRender="webtechnologistgroupItemCatalogItemListTable"
                           id="GroupItemRestoreButton"
                           status="WebTechnologgroupItemCatalogItemProcessStatus"/>
    </h:panelGrid>
</h:panelGrid>