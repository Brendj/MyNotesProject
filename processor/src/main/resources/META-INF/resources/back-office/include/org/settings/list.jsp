<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 05.04.13
  Time: 11:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%--@elvariable id="settingsListPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingsListPage"--%>
<%--@elvariable id="selectedSettingsGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SelectedSettingsGroupPage"--%>
<%--@elvariable id="settingEditPage" type="ru.axetta.ecafe.processor.web.ui.org.settings.SettingEditPage"--%>
<h:panelGrid id="settingsListPage" binding="#{settingsListPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Фильтр (#{settingsListPage.status})" switchType="client"
                            eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{settingsListPage.orgItem.shortName}" readonly="true"
                             styleClass="input-text long-field" style="margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>
            <h:outputText escape="true" value="Тип устройства" styleClass="output-text"/>
            <h:selectOneMenu value="#{settingsListPage.settingsIds}" styleClass="input-text">
                <f:selectItem itemValue="-1" itemLabel="--Не выбрано--"/>
                <f:selectItems value="#{settingsListPage.settingsIdEnumTypeMenu.items}" />
            </h:selectOneMenu>
        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{settingsListPage.updateSettingListPage}"
                               reRender="workspaceTogglePanel" styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{settingsListPage.clearSettingListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>
    </rich:simpleTogglePanel>

    <rich:dataTable id="ecafeSettingsEditListTable" value="#{settingsListPage.settingsList}" var="setting" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№"/>
            </f:facet>
            <h:outputText value="#{row+1}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="GUID"/>
            </f:facet>
            <a4j:commandLink value="#{setting.guid}" action="#{settingsListPage.view}"
                             styleClass="command-link" reRender="mainMenu, workspaceForm"/>
        </rich:column>
        <rich:column sortBy="#{setting.globalVersion}">
            <f:facet name="header">
                <h:outputText value="Версия"/>
            </f:facet>
            <h:outputText value="#{setting.globalVersion}"/>
        </rich:column>
        <rich:column sortBy="#{setting.settingsId}">
            <f:facet name="header">
                <h:outputText value="Тип устройства"/>
            </f:facet>
            <h:outputText value="#{setting.settingsId}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="#{settingsListPage.settingsHeadText}"/>
            </f:facet>
            <h:outputText value="#{setting.settingValue}"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink action="#{settingsListPage.edit}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>
</h:panelGrid>

<a4j:status id="sSettingsListStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>