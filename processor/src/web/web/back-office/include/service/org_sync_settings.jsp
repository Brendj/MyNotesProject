<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="orgSyncSettingReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncSettingReportPage"--%>
<h:panelGrid id="orgSyncSettingsReportPanelGrid" binding="#{orgSyncSettingReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{orgSyncSettingReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{orgSyncSettingReportPage.filter}}" />
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Округ" />
        <h:selectOneMenu id="orgDistricts"
                         value="#{orgSyncSettingReportPage.selectedDistricts}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingReportPage.listOfOrgDistricts}" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Тип синхронизации" />
        <h:selectOneMenu id="contentType"
                         value="#{orgSyncSettingReportPage.selectedContentType}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingReportPage.listOfContentType}" />
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО" />
        <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.allFriendlyOrgs}" styleClass="checkboxes"/>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{orgSyncSettingReportPage.buildHTML}"
                           reRender="orgSyncSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="buildHTMLButton" />

        <h:commandButton value="Выгрузить в Excel" action="#{orgSyncSettingReportPage.buildXLS}"
                         styleClass="command-button"  id="buildXLSButton" disabled="false">
            <a4j:support status="reportGenerateStatus" id="buildXLSButtonSupport"/>
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="reportGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>
    <rich:modalPanel id="OrgSyncSettingEditPanel" minWidth="830" minHeight="600" resizeable="false" domElementAttachment="form">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Редактирование расписания" />
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="OrgSyncSettingEditPanel" attachTo="hidelink" operation="hide" event="onclick" />
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText value="Название справочника" styleClass="output-text" />
            <h:inputText value="#{catalogListPage.catalogNameOfSelectedItem}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid" id="addNewElementToCatalogPanelGrid">
            <h:inputText value="#{catalogListPage.descriptionForNewElement}" styleClass="input-text" />
            <a4j:commandButton action="#{catalogListPage.addElementToSelectedCatalog()}"
                               reRender="OrgSyncSettingEditListElementsTable, addNewElementToCatalogPanelGrid" value="Добавить элемент"
                               status="WebTechnologCatalogProcessStatus" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('OrgSyncSettingEditPanel')"
                               reRender="workspaceTogglePanel, OrgSyncSettingEditPanel" value="Закрыть" action="#{catalogListPage.clearDescriptionForNewElementAndDropChanges()}"
                               status="WebTechnologCatalogProcessStatus"/>
            <a4j:commandButton reRender="OrgSyncSettingEditListElementsTable"
                               action="#{catalogListPage.applyChange()}" status="WebTechnologCatalogProcessStatus"
                               value="Сохранить изменения" />
        </h:panelGrid>
    </rich:modalPanel>
    <rich:dataTable id="orgSyncSettingsTable" value="#{orgSyncSettingReportPage.items}" var="item" rows="25"
                    sortMode="single"
                    rowClasses="center-aligned-column" lang="rus"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <h:outputText escape="true" value="Отчет по образовательным комплексам" />
        </f:facet>
        <rich:column sortable="true" sortBy="#{item.orgName}" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="ID ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header" >
        <f:facet name="header">
                <h:outputText escape="true" value="Адрес" />
            </f:facet>
            <h:outputText escape="true" value="#{item.shortAddress}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnFull}">
            <f:facet name="header">
                <h:outputText escape="true" value="Полная" />
            </f:facet>
            <h:outputText escape="true" value="#{item.fullSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnBalance}">
            <f:facet name="header">
                <h:outputText escape="true" value="Балансы и проходы"  />
            </f:facet>
            <h:outputText escape="true" value="#{item.accIncSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnOrgSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Настройки ОО" />
            </f:facet>
            <h:outputText escape="true" value="#{item.orgSettingSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnClientData}">
            <f:facet name="header">
                <h:outputText escape="true" value="Данные по клиентам" />
            </f:facet>
            <h:outputText escape="true" value="#{item.clientDataSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnMenu}">
            <f:facet name="header">
                <h:outputText escape="true" value="Меню" />
            </f:facet>
            <h:outputText escape="true" value="#{item.menuSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnPhoto}">
            <f:facet name="header">
                <h:outputText escape="true" value="Фотографии" />
            </f:facet>
            <h:outputText escape="true" value="#{item.photoSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnSupport}">
            <f:facet name="header">
                <h:outputText escape="true" value="Служба помощи" />
            </f:facet>
            <h:outputText escape="true" value="#{item.helpRequestsSync.fullInf}" styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnLib}">
            <f:facet name="header">
                <h:outputText escape="true" value="Библиотека" />
            </f:facet>
            <h:outputText escape="true" value="#{item.libSync.fullInf}"
                          styleClass="output-text" />
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" >
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink  styleClass="command-link"
                              oncomplete="Richfaces.showModalPanel('OrgSyncSettingEditPanel')"
                              reRender="OrgSyncSettingEditPanel">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}" target="#{catalogListPage.selectedItem}" />
                <f:setPropertyActionListener value="#{item.catalogName}" target="#{catalogListPage.catalogNameOfSelectedItem}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="orgSyncSettingsTable" renderIfSinglePage="false"
                               maxPages="15" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange" />
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
        <a4j:commandButton value="Применить изменения" action="#{orgSyncSettingReportPage.applyChanges}"
                           reRender="orgSyncSettingsTable" styleClass="command-button"
                           status="reportGenerateStatus" id="applyChangesButton" />
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
