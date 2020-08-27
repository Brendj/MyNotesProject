<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
    .isDelete {
        background-color: #8d8d8d;
    }
</style>

<%--@elvariable id="categoryCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog.CategoryCatalogListPage"--%>
<h:panelGrid id="webTechnologistcategoryItemCatalogItemListPagePanelGrid"
             binding="#{categoryCatalogListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название элемента" styleClass="output-text"/>
            <h:inputText value="#{categoryCatalogListPage.descriptionFilter}" styleClass="input-text"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{categoryCatalogListPage.updateCatalogList()}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus"/>
            <a4j:commandButton value="Очистить" action="#{categoryCatalogListPage.dropAndReloadCatalogList()}"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus" reRender="workspaceTogglePanel"
                               styleClass="command-button"/>
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="WebTechnologcategoryItemCatalogItemProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages"/>
    <!-- ********* VIEW ITEM MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistcategoryItemViewCategoryPanel" resizeable="false" minWidth="780"
                     minHeight="700"
                     domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Просмотр Элементов : #{categoryCatalogListPage.selectedItem.description}"/>
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="webtechnologistcategoryItemViewCategoryPanel" attachTo="hidelink"
                                       operation="hide" event="onclick"/>
            </h:panelGroup>
        </f:facet>
        <h:panelGroup id="WtViewDetailPanelGroup">
            <h:panelGrid columns="2" styleClass="borderless-grid">
                <rich:dataTable id="WtCatalogItemListTable"
                                value="#{categoryCatalogListPage.getItemsForSelectedItem()}" var="categoryItem"
                                rows="10"
                                footerClass="data-table-footer">
                    <rich:column headerClass="column-header"
                                 styleClass=" #{categoryItem.deleteState == 1 ? 'isDelete' : ''}">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Описание"/>
                        </f:facet>
                        <h:inputText value="#{categoryItem.description}" styleClass="output-text"/>
                    </rich:column>
                    <rich:column headerClass="column-header"
                                 styleClass=" #{categoryItem.deleteState == 1 ? 'isDelete' : ''}">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Создал пользователь"/>
                        </f:facet>
                        <h:outputText escape="true" value="#{categoryItem.user.userName}" styleClass="output-text"/>
                    </rich:column>
                    <rich:column headerClass="column-header"
                                 styleClass=" #{categoryItem.deleteState == 1 ? 'isDelete' : ''}">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Дата создания"/>
                        </f:facet>
                        <h:outputText escape="true" value="#{categoryItem.createDate}" styleClass="output-text">
                            <f:convertDateTime pattern="dd.MM.yyyy HH:mm"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column headerClass="column-header"
                                 styleClass="#{categoryItem.deleteState == 1 ? 'isDelete' : ''}">
                        <f:facet name="header">
                            <h:outputText escape="true" value="Последнее обновление"/>
                        </f:facet>
                        <h:outputText escape="true" value="#{categoryItem.lastUpdate}" styleClass="output-text">
                            <f:convertDateTime pattern="dd.MM.yyyy HH:mm"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column headerClass="column-header"
                                 styleClass=" #{categoryItem.deleteState == 1 ? 'isDelete' : ''}">
                        <f:facet name="header">
                            <h:outputText value="Удалить/Восстановить" escape="true"/>
                        </f:facet>
                        <a4j:commandLink styleClass="command-link" rendered="#{categoryItem.deleteState == 0}"
                                         action="#{categoryCatalogListPage.deleteItem()}"
                                         reRender="WtCatalogItemListTable">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;"/>
                            <f:setPropertyActionListener value="#{categoryItem}"
                                                         target="#{categoryCatalogListPage.selectedCategoryItem}"/>
                        </a4j:commandLink>
                        <a4j:commandLink styleClass="command-link" rendered="#{categoryItem.deleteState == 1}"
                                         action="#{categoryCatalogListPage.reestablishItem()}"
                                         reRender="WtCatalogItemListTable">
                            <h:graphicImage value="/images/16x16/true.png" style="border: 0;"/>
                            <f:setPropertyActionListener value="#{categoryItem}"
                                                         target="#{categoryCatalogListPage.selectedCategoryItem}"/>
                        </a4j:commandLink>
                    </rich:column>
                    <f:facet name="footer">
                        <rich:datascroller for="WtCatalogItemListTable" renderIfSinglePage="false"
                                           maxPages="5"
                                           fastControls="hide" stepControls="auto" boundaryControls="hide">
                            <f:facet name="previous">
                                <h:graphicImage value="/images/16x16/left-arrow.png"/>
                            </f:facet>
                            <f:facet name="next">
                                <h:graphicImage value="/images/16x16/right-arrow.png"/>
                            </f:facet>
                        </rich:datascroller>
                    </f:facet>
                </rich:dataTable>
            </h:panelGrid>
            <h:panelGrid columns="1">
                <h:outputText escape="true" value="Описание для нового элемента" styleClass="output-text"/>
            </h:panelGrid>
            <h:panelGrid columns="2">
                <h:inputText value="#{categoryCatalogListPage.descriptionForNewItem}" styleClass="input-text"/>
                <a4j:commandButton value="Создать"
                                   reRender="WtViewDetailPanelGroup"
                                   action="#{categoryCatalogListPage.createNewItem()}"
                                   status="WebTechnologcategoryItemCatalogItemProcessStatus"/>
            </h:panelGrid>
        </h:panelGroup>
        <h:panelGrid columns="2">
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistcategoryItemViewCategoryPanel')"
                               value="Закрыть"
                               status="WebTechnologcategoryItemCatalogItemProcessStatus"
                               reRender="webtechnologistcategoryItemViewCategoryPanel, webtechnologistcategoryItemCatalogItemListTable"/>
        </h:panelGrid>
    </rich:modalPanel>
    <!-- Main page -->
    <rich:dataTable id="webtechnologistcategoryItemCatalogItemListTable"
                    value="#{categoryCatalogListPage.catalogListItem}" var="item" rows="30"
                    footerClass="data-table-footer"
                    columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText escape="true" value="Описание"/>
            </f:facet>
            <h:inputText value="#{item.description}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText escape="true" value="Создал пользователь"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.user.userName}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата создания"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.createDate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm"/>
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header" styleClass="#{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText escape="true" value="Последнее обновление"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm"/>
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText escape="true" value="Кол.Элементов"/>
            </f:facet>
            <h:outputText value="#{item.categoryItems.size()}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText value="Удалить/Восстановить" escape="true"/>
            </f:facet>
            <a4j:commandLink styleClass="command-link" rendered="#{item.deleteState == 0}"
                             action="#{categoryCatalogListPage.deleteCategory()}"
                             reRender="webtechnologistcategoryItemCatalogItemListTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}"
                                             target="#{categoryCatalogListPage.selectedItem}"/>
            </a4j:commandLink>
            <a4j:commandLink styleClass="command-link" rendered="#{item.deleteState == 1}"
                             action="#{categoryCatalogListPage.reestablishCategory()}"
                             reRender="webtechnologistcategoryItemCatalogItemListTable">
                <h:graphicImage value="/images/16x16/true.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}"
                                             target="#{categoryCatalogListPage.selectedItem}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" styleClass=" #{item.deleteState == 1 ? 'isDelete' : ''}">
            <f:facet name="header">
                <h:outputText value="Элементы категории" escape="true"/>
            </f:facet>
            <a4j:commandLink styleClass="command-link"
                             onclick="Richfaces.showModalPanel('webtechnologistcategoryItemViewCategoryPanel');"
                             reRender="webtechnologistcategoryItemCatalogItemListTable, WtViewDetailPanelGroup">
                <h:graphicImage value="/images/16x16/search.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}"
                                             target="#{categoryCatalogListPage.selectedItem}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="webtechnologistcategoryItemCatalogItemListTable" renderIfSinglePage="false"
                               maxPages="5"
                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png"/>
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png"/>
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <h:panelGrid columns="2" styleClass="borderless-grid" id="wtCreateCategoryPanelGrid">
        <h:outputText escape="true" value="Описание категории" styleClass="output-text"/>
        <h:inputText value="#{categoryCatalogListPage.descriptionForNewCategory}" styleClass="input-text"/>
        <a4j:commandButton reRender="webtechnologistcategoryItemCatalogItemListTable, wtCreateCategoryPanelGrid"
                           action="#{categoryCatalogListPage.createNewCatalog()}"
                           status="WebTechnologcategoryItemCatalogItemProcessStatus" value="Создать"/>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить изменения"
                           action="#{categoryCatalogListPage.applyChanges()}"
                           reRender="webtechnologistcategoryItemCatalogItemListTable"
                           id="categoryItemApplyChangeButton"
                           status="WebTechnologcategoryItemCatalogItemProcessStatus"/>

        <a4j:commandButton value="Восстановить значения"
                           action="#{categoryCatalogListPage.refreshItems()}"
                           reRender="webtechnologistcategoryItemCatalogItemListTable"
                           id="categoryItemRestoreButton"
                           status="WebTechnologcategoryItemCatalogItemProcessStatus"/>
    </h:panelGrid>
</h:panelGrid>