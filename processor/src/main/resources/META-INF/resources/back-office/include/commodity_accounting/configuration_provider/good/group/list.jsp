<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="goodGroupListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupListPage"--%>
<%--@elvariable id="goodGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupEditPage"--%>
<%--@elvariable id="goodGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupViewPage"--%>
<%--@elvariable id="selectedGoodGroupGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.SelectedGoodGroupGroupPage"--%>
<h:panelGrid id="goodGroupListPanelGrid" binding="#{goodGroupListPage.pageComponent}" styleClass="borderless-grid" columns="1">

    <h:panelGrid id="goodGroupListPanelFilter" styleClass="borderless-grid" columns="1">
        <rich:simpleTogglePanel label="Фильтр" switchType="client" eventsQueue="mainFormEventsQueue" opened="true" headerClass="filter-panel-header">
            <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{goodGroupListPage.selectedConfigurationProvider.name}" styleClass="output-text"
                                  style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{goodGroupListPage.selectConfigurationProvider}"
                                       reRender="configurationProviderSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>

                <h:outputText value="Удаленные элементы" styleClass="output-text" escape="true" />
                <h:selectOneMenu id="selectDeletedStatus" value="#{goodGroupListPage.deletedStatusSelected}" styleClass="input-text long-field">
                    <f:selectItem itemLabel="Скрыть" itemValue="false" />
                    <f:selectItem itemLabel="Показать" itemValue="true" />
                </h:selectOneMenu>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">

                <a4j:commandButton value="Применить" action="#{goodGroupListPage.onSearch}" reRender="workspaceTogglePanel"
                                   styleClass="command-button" />

                <a4j:commandButton value="Очистить" action="#{goodGroupListPage.onClear}" reRender="workspaceTogglePanel" ajaxSingle="true"
                                   styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>


    <a4j:status id="oodGroupListTableStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
        </f:facet>
    </a4j:status>

    <rich:dataTable id="goodGroupListTable" value="#{goodGroupListPage.goodGroupList}" var="goodGroup" rows="10"
                    rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Идентификатор" styleClass="output-text" escape="true" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodGroup.globalId}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="GUID" styleClass="output-text" escape="true" />
            </f:facet>
            <h:outputText styleClass="output-text" value="#{goodGroup.guid}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наименование группы" styleClass="output-text" escape="true" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{goodGroup.nameOfGoodsGroup}" action="#{goodGroupViewPage.show}"
                             styleClass="command-link">
                <f:setPropertyActionListener value="#{goodGroup}" target="#{selectedGoodGroupGroupPage.currentGoodGroup}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Статус группы" styleClass="output-text" escape="true" />
            </f:facet>
            <h:selectBooleanCheckbox value="#{goodGroup.deletedState}" readonly="true" disabled="true" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" escape="true" styleClass="output-text" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{goodGroupEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{goodGroup}" target="#{selectedGoodGroupGroupPage.currentGoodGroup}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" styleClass="output-text" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link" reRender="mainMenu, workspaceForm" rendered="#{goodGroup.deletedState}"
                             oncomplete="#{rich:component('removedGoodGroupItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{goodGroup}" target="#{goodGroupEditPage.currentGoodGroup}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="goodGroupListTable" renderIfSinglePage="false" maxPages="10" fastControls="hide" stepControls="auto"
                               boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>

