<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="smartWatchVendorsListPage" type="ru.axetta.ecafe.processor.web.ui.card.smartwatchvendors.SmartWatchVendorsListPage"--%>
<h:panelGrid id="SmartWatchVendorsItemListPagePanelGrid"
             binding="#{smartWatchVendorsListPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:simpleTogglePanel label="Фильтр " switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Название поставщика" styleClass="output-text"/>
            <h:inputText value="#{smartWatchVendorsListPage.nameFilter}" styleClass="input-text"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{smartWatchVendorsListPage.updateVendorList()}"
                               reRender="workspaceTogglePanel" styleClass="command-button"
                               status="SmartWatchVendorsItemProcessStatus"/>
            <a4j:commandButton value="Очистить" action="#{smartWatchVendorsListPage.dropAndReloadCatalogList()}"
                               status="SmartWatchVendorsItemProcessStatus" reRender="workspaceTogglePanel"
                               styleClass="command-button"/>
        </h:panelGrid>
    </rich:simpleTogglePanel>
    <a4j:status id="SmartWatchVendorsItemProcessStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages"/>
    <rich:dataTable id="SmartWatchVendorsDataTable"
                    value="#{smartWatchVendorsListPage.smartWatchVendorList}" var="item" rows="30"
                    footerClass="data-table-footer"
                    columnClasses="center-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Название"/>
            </f:facet>
            <h:inputText value="#{item.name}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Ключ"/>
            </f:facet>
            <h:inputText value="#{item.apiKey}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер сертификата"/>
            </f:facet>
            <h:inputText value="#{item.cardSignCertNum}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Разрешение на обслуживание"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.enableService}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Разрешение на отправку событий"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.enablePushes}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес для отправки проходов"/>
            </f:facet>
            <h:inputText value="#{item.enterEventsEndPoint}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес для отправки покупок"/>
            </f:facet>
            <h:inputText value="#{item.purchasesEndPoint}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес для отправки пополнений счёта"/>
            </f:facet>
            <h:inputText value="#{item.paymentEndPoint}" styleClass="output-text"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true"/>
            </f:facet>
            <a4j:commandLink styleClass="command-link"
                             action="#{smartWatchVendorsListPage.deleteVendor()}"
                             reRender="SmartWatchVendorsDataTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}"
                                             target="#{smartWatchVendorsListPage.selectedItem}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="SmartWatchVendorsDataTable" renderIfSinglePage="false"
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
    <h:panelGrid columns="2" styleClass="borderless-grid" id="smartWatchCreateVendorPanelGrid">
        <h:outputText escape="true" value="Имя нового элемента" styleClass="output-text"/>
        <h:inputText value="#{smartWatchVendorsListPage.nameForNewVendor}" styleClass="input-text"/>
        <a4j:commandButton reRender="SmartWatchVendorsDataTable, SmartWatchVendorDetailPanelGroup"
                           action="#{smartWatchVendorsListPage.createNewVendor()}"
                           status="SmartWatchVendorsItemProcessStatus" value="Создать"/>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить изменения"
                           action="#{smartWatchVendorsListPage.applyChanges()}"
                           reRender="SmartWatchVendorsDataTable"
                           id="vendorItemApplyChangeButton"
                           status="SmartWatchVendorsItemProcessStatus"/>

        <a4j:commandButton value="Восстановить значения"
                           action="#{smartWatchVendorsListPage.refreshItems()}"
                           reRender="SmartWatchVendorsDataTable"
                           id="vendorItemRestoreButton"
                           status="SmartWatchVendorsItemProcessStatus"/>
    </h:panelGrid>
</h:panelGrid>