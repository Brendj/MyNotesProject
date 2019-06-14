<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="webTechnologistCatalogListPage" type="ru.axetta.ecafe.processor.web.ui.service.webtechnologist.CatalogListPage"--%>
<h:panelGrid id="webTechnologistCatalogListPagePanelGrid" binding="#{webTechnologistCatalogListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{webTechnologistCatalogListPage.catalogNameFilter}" styleClass="input-text" />
            <h:outputText escape="true" value="GUID справочника" styleClass="output-text" />
            <h:inputText value="#{webTechnologistCatalogListPage.GUIDfilter}" maxlength="36" styleClass="input-text"
                         size="50" />
            <h:outputText escape="true" value="Показать только активные справочники" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{webTechnologistCatalogListPage.showOnlyActive}" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{webTechnologistCatalogListPage.updateCatalogList}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="WebTechnologCatalogProcessStatus" />
            <a4j:commandButton value="Очистить" action="#{webTechnologistCatalogListPage.dropAndReloadCatalogList}"
                               status="WebTechnologCatalogProcessStatus" reRender="workspaceTogglePanel"
                               ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="WebTechnologCatalogProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <rich:modalPanel id="webtechnologistCatalogElementListPanel" width="600" height="600" style="overflow: scroll;">
        <rich:dataTable id="webtechnologistCatalogListElementsTable"
                        value="#{webTechnologistCatalogListPage.selectedItem.items}" var="catalogElement" rows="20"
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
        <f:facet name="controls">
            <a4j:commandLink onclick="Richfaces.hideModalPanel('webtechnologistCatalogItemListPanel')" reRender="this"
                             style="color: white;" status="WebTechnologCatalogProcessStatus">
                <h:outputText value="Закрыть" />
            </a4j:commandLink>
        </f:facet>
    </rich:modalPanel>
    <rich:modalPanel id="webtechnologistCatalogEditePanel" width="350" height="150" resizeable="false">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Редактирование справочника" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="groupCreatePanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:inputText value="Название справочника" styleClass="output-text" />
            <h:inputText onchange="#{webTechnologistCatalogListPage.selectedItem.hasBeenChanged}" value="#{webTechnologistCatalogListPage.selectedItem.catalogName}" styleClass="input-text" />
        </h:panelGrid>
        <rich:dataTable id="webtechnologistCatalogEditListElementsTable"
                        value="#{webTechnologistCatalogListPage.selectedItem.items}" var="catalogElement" rows="20"
                        footerClass="data-table-footer"
                        columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Описание" />
                </f:facet>
                <h:inputText  onchange="#{catalogElement.hasBeenChanged}" value="#{catalogElement.description}" styleClass="output-text" />
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
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText value="Удалить" escape="true" />
                </f:facet>
                <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                                 action="#{webTechnologistCatalogListPage.deleteCatalogElement}" reRender="workspaceTogglePanel">
                    <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                    <f:setPropertyActionListener value="#{catalogElement}" target="#{webTechnologistCatalogListPage.selectedCatalogElement}" />
                </a4j:commandLink>
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="webtechnologistCatalogEditListElementsTable" renderIfSinglePage="false" maxPages="5"
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
        <h:inputText value="#{webTechnologistCatalogListPage.createdCatalogElementDescription}" styleClass="input-text" />
        <a4j:commandButton action="#{webTechnologistCatalogListPage.addElementToSelectedCatalog}"  reRender="this"
                           value="Добавить элемент" status="WebTechnologCatalogProcessStatus" />
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton onclick="Richfaces.hideModalPanel('webtechnologistCatalogCreatePanel')" reRender="this"
                               value="Закрыть" status="WebTechnologCatalogProcessStatus" />
            <a4j:commandButton reRender="this, webtechnologistCatalogListTable"
                               action="#{webTechnologistCatalogListPage.applyChange}"
                               status="WebTechnologCatalogProcessStatus" value="Сохранить изменения" />
        </h:panelGrid>
    </rich:modalPanel>
    <rich:modalPanel id="webtechnologistCatalogEditPanel" width="600" height="600" resizeable="false">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Создание справочника" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="groupCreatePanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{webTechnologistCatalogListPage.createdCatalogName}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton onclick="Richfaces.hideModalPanel('webtechnologistCatalogEditPanel')" reRender="this"
                               value="Закрыть" status="WebTechnologCatalogProcessStatus" />
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('webtechnologistCatalogEditPanel')"
                               reRender="this, webtechnologistCatalogListTable"
                               action="#{webTechnologistCatalogListPage.createNewCatalog}"
                               status="WebTechnologCatalogProcessStatus" value="Создать" />
        </h:panelGrid>
    </rich:modalPanel>
    <rich:dataTable id="webtechnologistCatalogListTable" value="#{webTechnologistCatalogListPage.itemList}" var="item"
                    rows="30" footerClass="data-table-footer"
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
            <a4j:commandButton value="Просмотр" rendered="#{not empty item.items}" ajaxSingle="true"
                               oncomplete="Richfaces.showModalPanel('webtechnologistCatalogItemListPanel');"
                               reRender="webtechnologistCatalogItemListPanel" styleClass="command-link">
                <f:setPropertyActionListener value="#{item}" target="#{webTechnologistCatalogListPage.selectedItem}" />
            </a4j:commandButton>
            <h:outputText escape="true" value="Элементы отсуствуют" rendered="#{empty item.items}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Изменить" escape="true" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             action="#{webTechnologistCatalogListPage.editSelectedItem}"
                             reRender="workspaceTogglePanel">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{webTechnologistCatalogListPage.selectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             action="#{webTechnologistCatalogListPage.deleteItem}" reRender="workspaceTogglePanel">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item}" target="#{webTechnologistCatalogListPage.selectedItem}" />
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
    <rich:separator align="center" height="5" width="270px" />

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Создать новый справочник"
                           oncomplete="Richfaces.showModalPanel('webtechnologistCatalogCreatePanel');"
                           reRender="webtechnologistCatalogItemListPanel" id="showCreateModalPanelButton" />
    </h:panelGrid>
</h:panelGrid>


