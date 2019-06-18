<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="catalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogListPage"--%>
<h:panelGrid id="webTechnologistCatalogListPagePanelGrid" binding="#{catalogListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{catalogListPage.catalogNameFilter}" styleClass="input-text" />
            <h:outputText escape="true" value="GUID справочника" styleClass="output-text" />
            <h:inputText value="#{catalogListPage.GUIDfilter}" maxlength="36" styleClass="input-text" size="50" />
            <h:outputText escape="true" value="Показать только активные справочники" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{catalogListPage.showOnlyActive}" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{catalogListPage.updateCatalogList()}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="WebTechnologCatalogProcessStatus"  />
            <a4j:commandButton value="Очистить" action="#{catalogListPage.dropAndReloadCatalogList()}"
                               status="WebTechnologCatalogProcessStatus" reRender="workspaceTogglePanel"
                                styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="WebTechnologCatalogProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <!-- ********* VIEW MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistCatalogElementListPanel" width="750" height="600">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Просмотр элементов справочника" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <a4j:commandLink onclick="Richfaces.hideModalPanel('webtechnologistCatalogElementListPanel')"
                             style="color: white;" status="WebTechnologCatalogProcessStatus">
                <h:outputText value="Закрыть" />
            </a4j:commandLink>
        </f:facet>
        <rich:dataTable id="webtechnologistCatalogListElementsTable"
                        value="#{catalogListPage.selectedItem.items.toArray()}" var="catalogViewElement" rows="20"
                        footerClass="data-table-footer"
                        columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Описание" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogViewElement.description}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="GUID" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogViewElement.GUID}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Состояние" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogViewElement.deleteStateAsString}"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата создания" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogViewElement.createDate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
                </h:outputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Последнее обновление" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogViewElement.lastUpdate}" styleClass="output-text">
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
    </rich:modalPanel>
    <!-- ********* EDIT MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistCatalogEditPanel" width="800" height="600" resizeable="false" domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Редактирование справочника" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="webtechnologistCatalogEditPanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{catalogListPage.catalogNameOfSelectedItem}" styleClass="input-text" />
        </h:panelGrid>
        <rich:dataTable id="webtechnologistCatalogEditListElementsTable"
                        value="#{catalogListPage.selectedItem.items.toArray()}" var="catalogEditElement" rows="20"
                        footerClass="data-table-footer"
                        columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Описание" />
                </f:facet>
                <h:inputText onchange="#{catalogEditElement.isChanged()}" value="#{catalogEditElement.description}"
                             styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="GUID" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogEditElement.GUID}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Состояние" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogEditElement.deleteStateAsString}"
                              styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Дата создания" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogEditElement.createDate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
                </h:outputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Последнее обновление" />
                </f:facet>
                <h:outputText escape="true" value="#{catalogEditElement.lastUpdate}" styleClass="output-text">
                    <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
                </h:outputText>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Удалить" escape="true" />
                </f:facet>
                <a4j:commandLink  styleClass="command-link"
                                 action="#{catalogListPage.deleteCatalogElement()}"
                                 reRender="webtechnologistCatalogEditListElementsTable">
                    <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{catalogEditElement}"
                                                 target="#{catalogListPage.selectedCatalogElement}" />
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="webtechnologistCatalogEditListElementsTable" renderIfSinglePage="false"
                                   maxPages="5" fastControls="hide" stepControls="auto" boundaryControls="hide">
                    <f:facet name="previous">
                        <h:graphicImage value="/images/16x16/left-arrow.png" />
                    </f:facet>
                    <f:facet name="next">
                        <h:graphicImage value="/images/16x16/right-arrow.png" />
                    </f:facet>
                </rich:datascroller>
            </f:facet>
        </rich:dataTable>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:inputText value="#{catalogListPage.descriptionForNewElement}" styleClass="input-text" />
            <a4j:commandButton action="#{catalogListPage.addElementToSelectedCatalog()}"
                               reRender="webtechnologistCatalogEditListElementsTable" value="Добавить элемент"
                               status="WebTechnologCatalogProcessStatus" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistCatalogEditPanel')"
                               reRender="workspaceTogglePanel, webtechnologistCatalogEditPanel" value="Закрыть" action="#{catalogListPage.clearDescriptionForNewElementAndDropChanges()}"
                               status="WebTechnologCatalogProcessStatus"/>
            <a4j:commandButton reRender="webtechnologistCatalogEditListElementsTable"
                               action="#{catalogListPage.applyChange()}" status="WebTechnologCatalogProcessStatus"
                               value="Сохранить изменения" />
        </h:panelGrid>
    </rich:modalPanel>
    <!-- ********* CREATE MODAL PANEL ********* -->
    <rich:modalPanel id="webtechnologistCatalogCreatePanel" width="300" height="150" resizeable="false" domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Создание справочника" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="webtechnologistCatalogCreatePanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{catalogListPage.nameForNewCatalog}" styleClass="input-text" />
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistCatalogCreatePanel')" value="Закрыть"
                               status="WebTechnologCatalogProcessStatus"  action="#{catalogListPage.clearNameForNewCatalog()}" reRender="webtechnologistCatalogCreatePanel"/>
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistCatalogCreatePanel')"
                               reRender="webtechnologistCatalogListTable, webtechnologistCatalogCreatePanel" action="#{catalogListPage.createNewCatalog()}"
                               status="WebTechnologCatalogProcessStatus" value="Создать" />
        </h:panelGrid>
    </rich:modalPanel>
    <!-- Main page -->
    <rich:dataTable id="webtechnologistCatalogListTable" value="#{catalogListPage.itemList}" var="item" rows="30"
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
            <h:outputText escape="true" value="#{item.lastUpdate}" styleClass="output-text">
                <f:convertDateTime pattern="dd.MM.yyyy HH:mm" />
            </h:outputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Состав каталога" />
            </f:facet>
            <a4j:commandLink value="Просмотр" rendered="#{not empty item.items}"
                             oncomplete="Richfaces.showModalPanel('webtechnologistCatalogElementListPanel');"
                             styleClass="command-link" reRender="webtechnologistCatalogListElementsTable">
                <f:setPropertyActionListener value="#{item}" target="#{catalogListPage.selectedItem}" />
            </a4j:commandLink>
            <h:outputText escape="true" value="Элементы отсуствуют" rendered="#{empty item.items}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Изменить" escape="true" />
            </f:facet>
            <a4j:commandLink  styleClass="command-link"
                             oncomplete="Richfaces.showModalPanel('webtechnologistCatalogEditPanel')"
                             reRender="webtechnologistCatalogEditPanel">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}" target="#{catalogListPage.selectedItem}" />
                <f:setPropertyActionListener value="#{item.catalogName}" target="#{catalogListPage.catalogNameOfSelectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" />
            </f:facet>
            <a4j:commandLink  styleClass="command-link" action="#{catalogListPage.deleteItem()}"
                             reRender="workspaceTogglePanel">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{catalogListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="webtechnologistCatalogListTable" renderIfSinglePage="false" maxPages="5"
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
        <a4j:commandButton value="Создать новый справочник"
                           onclick="Richfaces.showModalPanel('webtechnologistCatalogCreatePanel');"
                           id="showCreateModalPanelButton" />
    </h:panelGrid>
</h:panelGrid>


