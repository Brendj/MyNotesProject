<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="categoryItemCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.CategoryItemCatalogListPage"--%>
<h:panelGrid id="webTechnologistcategoryItemCatalogItemListPagePanelGrid" binding="#{categoryItemCatalogListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название элемента" styleClass="output-text" />
            <h:inputText value="#{categoryItemCatalogListPage.descriptionFilter}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{categoryItemCatalogListPage.updateCatalogList()}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus"  />
            <a4j:commandButton value="Очистить" action="#{categoryItemCatalogListPage.dropAndReloadCatalogList()}"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus" reRender="workspaceTogglePanel"
                               styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="WebTechnologcategoryItemCatalogItemProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <!-- ********* CREATE MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistcategoryItemItemCreatePanel" minWidth="150" minHeight="70" resizeable="false" domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Создание Элемента" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="webtechnologistcategoryItemItemCreatePanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Описание элемента" styleClass="output-text" />
            <h:inputText value="#{categoryItemCatalogListPage.descriptionForNewItem}" styleClass="input-text" />
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistcategoryItemItemCreatePanel')" value="Закрыть"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus"  action="#{categoryItemCatalogListPage.clearDescriptionForNewCatalog()}" reRender="webtechnologistcategoryItemItemCreatePanel"/>
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistcategoryItemItemCreatePanel')"
                               reRender="webtechnologistcategoryItemCatalogItemListTable, webtechnologistcategoryItemItemCreatePanel" action="#{categoryItemCatalogListPage.createNewItem()}"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus" value="Создать" />
        </h:panelGrid>
    </rich:modalPanel>
    <!-- Main page -->
    <rich:dataTable id="webtechnologistcategoryItemCatalogItemListTable" value="#{categoryItemCatalogListPage.catalogListItem}" var="item" rows="30"
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
                <h:outputText escape="true" value="Создал пользователь" />
            </f:facet>
            <h:outputText escape="true" value="#{item.user.userName}" styleClass="output-text" />
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
            <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" />
            </f:facet>
            <a4j:commandLink  styleClass="command-link"
                              action="#{categoryItemCatalogListPage.deleteItem()}"
                              reRender="webtechnologistcategoryItemCatalogItemListTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}"
                                             target="#{categoryItemCatalogListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="webtechnologistcategoryItemCatalogItemListTable" renderIfSinglePage="false" maxPages="5"
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
                           onclick="Richfaces.showModalPanel('webtechnologistcategoryItemItemCreatePanel');"
                           id="showCreatecategoryItemItemModalPanelButton" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить изменения"
                           action="#{categoryItemCatalogListPage.applyChanges()}"
                           reRender="webtechnologistcategoryItemCatalogItemListTable"
                           id="categoryItemApplyChangeButton"
                           status="WebTechnologcategoryItemCatalogItemProcessStatus"/>

        <a4j:commandButton value="Восстановить значения"
                           action="#{categoryItemCatalogListPage.refreshItems()}"
                           reRender="webtechnologistcategoryItemCatalogItemListTable"
                           id="categoryItemRestoreButton"
                           status="WebTechnologcategoryItemCatalogItemProcessStatus"/>
    </h:panelGrid>
</h:panelGrid>