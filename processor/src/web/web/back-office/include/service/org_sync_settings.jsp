<%--
  ~ Copyright (c) 2019. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="H" uri="http://richfaces.ajax4jsf.org/rich" %>

<%--@elvariable id="orgSyncSettingReportPage" type="ru.axetta.ecafe.processor.web.ui.service.orgparameters.OrgSyncSettingReportPage"--%>
<h:panelGrid id="orgSyncSettingsReportPanelGrid" binding="#{orgSyncSettingReportPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организации"/>
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                               reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{orgSyncSettingReportPage.getStringIdOfOrgList}"
                                             target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" escape="true" value=" {#{orgSyncSettingReportPage.filter}}"/>
        </h:panelGroup>
        <h:outputText styleClass="output-text" escape="true" value="Округ"/>
        <h:selectOneMenu id="orgDistricts"
                         value="#{orgSyncSettingReportPage.selectedDistricts}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingReportPage.listOfOrgDistricts}"/>
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Тип синхронизации"/>
        <h:selectOneMenu id="contentType"
                         value="#{orgSyncSettingReportPage.selectedContentType}"
                         styleClass="input-text" style="width: 250px;">
            <f:selectItems value="#{orgSyncSettingReportPage.listOfContentType}"/>
        </h:selectOneMenu>
        <h:outputText styleClass="output-text" escape="true" value="Построить по всем дружественным ОО"/>
        <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.allFriendlyOrgs}" styleClass="checkboxes"/>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Генерировать отчет" action="#{orgSyncSettingReportPage.buildHTML}"
                           reRender="orgSyncSettingsTable" styleClass="command-button"
                           status="orgSyncReportStatus" id="buildHTMLButton"/>

        <h:commandButton value="Выгрузить в Excel" action="#{orgSyncSettingReportPage.buildXLS}"
                         styleClass="command-button" id="buildXLSButton" disabled="false">
            <a4j:support status="orgSyncReportStatus" id="buildXLSButtonSupport"/>
        </h:commandButton>
    </h:panelGrid>
    <a4j:status id="orgSyncReportStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <rich:modalPanel id="OrgSyncSettingEditPanel" minWidth="735" minHeight="350" resizeable="false"
                     domElementAttachment="form">
        <f:facet name="header">
            <h:outputText value="Редактирование расписания"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <rich:componentControl for="OrgSyncSettingEditPanel" attachTo="hidelink" operation="hide"
                                       event="onclick"/>
            </h:panelGroup>
        </f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid" border="10" columnClasses="top">
            <h:panelGroup>
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <h:outputText styleClass="output-text" value="ID :"/>
                    <h:outputText styleClass="output-text" value="#{orgSyncSettingReportPage.selectedItem.idOfOrg}"/>
                    <h:outputText styleClass="output-text" value="Название :"/>
                    <h:outputText styleClass="output-text" value="#{orgSyncSettingReportPage.selectedItem.orgName}"/>
                    <h:outputText styleClass="output-text" value="Тип синхронизации"/>
                    <h:selectOneMenu id="modalContentType"
                                     value="#{orgSyncSettingReportPage.modalSelectedContentType}"
                                     styleClass="input-text" style="width: 175px;">
                        <f:selectItems value="#{orgSyncSettingReportPage.modalListOfContentType}"/>
                        <a4j:support event="onchange" action="#{orgSyncSettingReportPage.buildEditedItem()}"
                                     reRender="OrgSyncSettingEditPanelGrid"/>
                    </h:selectOneMenu>
                </h:panelGrid>
            </h:panelGroup>
            <h:panelGroup id="OrgSyncSettingEditPanelGrid">
                <h:panelGrid styleClass="borderless-grid" columns="2">
                    <H:columnGroup rendered="#{!orgSyncSettingReportPage.runEverySecond}">
                        <h:outputText styleClass="output-text" value="Время 1-го сеанса"/>
                        <h:inputText value="#{orgSyncSettingReportPage.editedSetting.concreteTime1}">
                            <f:convertDateTime pattern="HH:mm"/>
                        </h:inputText>
                        <H:columnGroup rendered="#{orgSyncSettingReportPage.showConcreteTime2}">
                            <h:outputText styleClass="output-text" value="Время 2-го сеанса"/>
                            <h:inputText value="#{orgSyncSettingReportPage.editedSetting.concreteTime2}">
                                <f:convertDateTime pattern="HH:mm"/>
                            </h:inputText>
                        </H:columnGroup>
                        <H:columnGroup rendered="#{orgSyncSettingReportPage.showConcreteTime3}">
                            <h:outputText styleClass="output-text" value="Время 3-го сеанса"/>
                            <h:inputText value="#{orgSyncSettingReportPage.editedSetting.concreteTime3}">
                                <f:convertDateTime pattern="HH:mm"/>
                            </h:inputText>
                        </H:columnGroup>
                    </H:columnGroup>
                    <H:columnGroup rendered="#{orgSyncSettingReportPage.runEverySecond}">
                        <h:outputText styleClass="output-text" value="Запускать через каждые (сек.) "/>
                        <h:inputText value="#{orgSyncSettingReportPage.editedSetting.everySecond}"/>
                    </H:columnGroup>
                    <h:outputText styleClass="output-text" value="Понедельник"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.monday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Вторник"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.tuesday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Среда"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.wednesday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Четверг"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.thursday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Пятница"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.friday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Суббота"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.saturday}"
                                             styleClass="checkboxes"/>
                    <h:outputText styleClass="output-text" value="Воскресенье"/>
                    <h:selectBooleanCheckbox value="#{orgSyncSettingReportPage.editedSetting.sunday}"
                                             styleClass="checkboxes"/>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton reRender="OrgSyncSettingEditPanelGrid"
                                           action="#{orgSyncSettingReportPage.saveLocalChanges()}"
                                           status="orgSyncReportStatus"
                                           value="Сохранить"/>
                    </h:panelGrid>
                </h:panelGrid>
            </h:panelGroup>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid" id="addNewElementToCatalogPanelGrid">
        </h:panelGrid>
        <h:panelGrid columns="1" styleClass="borderless-grid">
            <a4j:commandButton oncomplete="Richfaces.hideModalPanel('OrgSyncSettingEditPanel')"
                               reRender="workspaceTogglePanel, OrgSyncSettingEditPanel" value="Закрыть"
                               action="#{orgSyncSettingReportPage.resetChanges()}"
                               status="orgSyncReportStatus"/>
        </h:panelGrid>
    </rich:modalPanel>
    <rich:dataTable id="orgSyncSettingsTable" value="#{orgSyncSettingReportPage.items}" var="item" rows="25"
                    sortMode="single"
                    rowClasses="center-aligned-column" lang="rus"
                    footerClass="data-table-footer">
        <f:facet name="header">
            <h:outputText escape="true" value="Отчет по образовательным комплексам"/>
        </f:facet>
        <rich:column sortable="true" sortBy="#{item.orgName}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.orgName}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.idOfOrg}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ID ОО"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" sortBy="#{item.shortAddress}" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.shortAddress}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnFull}">
            <f:facet name="header">
                <h:outputText escape="true" value="Полная"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.fullSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header"
                     rendered="#{orgSyncSettingReportPage.showColumnBalance}">
            <f:facet name="header">
                <h:outputText escape="true" value="Балансы и проходы"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.accIncSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header"
                     rendered="#{orgSyncSettingReportPage.showColumnOrgSettings}">
            <f:facet name="header">
                <h:outputText escape="true" value="Настройки ОО"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.orgSettingSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header"
                     rendered="#{orgSyncSettingReportPage.showColumnClientData}">
            <f:facet name="header">
                <h:outputText escape="true" value="Данные по клиентам"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.clientDataSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnMenu}">
            <f:facet name="header">
                <h:outputText escape="true" value="Меню"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.menuSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnPhoto}">
            <f:facet name="header">
                <h:outputText escape="true" value="Фотографии"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.photoSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header"
                     rendered="#{orgSyncSettingReportPage.showColumnSupport}">
            <f:facet name="header">
                <h:outputText escape="true" value="Служба помощи"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.helpRequestsSync.fullInf}" styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header" rendered="#{orgSyncSettingReportPage.showColumnLib}">
            <f:facet name="header">
                <h:outputText escape="true" value="Библиотека"/>
            </f:facet>
            <h:outputText escape="true" value="#{item.libSync.fullInf}"
                          styleClass="output-text"/>
        </rich:column>
        <rich:column sortable="true" headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать"/>
            </f:facet>
            <a4j:commandLink styleClass="command-link"
                             action="#{orgSyncSettingReportPage.buildEditedItem()}"
                             oncomplete="Richfaces.showModalPanel('OrgSyncSettingEditPanel')"
                             reRender="OrgSyncSettingEditPanel">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;"/>
                <f:setPropertyActionListener value="#{item}" target="#{orgSyncSettingReportPage.selectedItem}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="orgSyncSettingsTable" renderIfSinglePage="false"
                               maxPages="15" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <a4j:support event="onpagechange"/>
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png"/>
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png"/>
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Применить изменения" action="#{orgSyncSettingReportPage.applyChanges}"
                           reRender="orgSyncSettingsTable" styleClass="command-button"
                           status="orgSyncReportStatus" id="applyChangesButton"/>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages"/>
</h:panelGrid>
