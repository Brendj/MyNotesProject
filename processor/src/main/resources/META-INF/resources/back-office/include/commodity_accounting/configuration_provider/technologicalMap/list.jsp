<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapListPage"--%>
<%--@elvariable id="technologicalMapEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapEditPage"--%>
<%--@elvariable id="technologicalMapViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.TechnologicalMapViewPage"--%>
<%--@elvariable id="selectedTechnologicalMapGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.SelectedTechnologicalMapGroupPage"--%>
<h:panelGrid id="technologicalMapListPage" binding="#{technologicalMapListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <h:panelGrid id="technologicalMapListFilter" styleClass="borderless-grid" columns="1">

        <rich:simpleTogglePanel label="Фильтр" switchType="client" eventsQueue="mainFormEventsQueue"
                                opened="true" headerClass="filter-panel-header">
            <h:panelGrid columns="2" styleClass="borderless-grid">

                <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{technologicalMapListPage.selectedConfigurationProvider.name}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{technologicalMapListPage.selectConfigurationProvider}" reRender="configurationProviderSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText escape="true" value="Группа" styleClass="output-text required-field" />
                <h:panelGroup styleClass="borderless-div">
                    <h:outputText value="#{technologicalMapListPage.selectedTechnologicalMapGroup.nameOfGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    <a4j:commandButton value="..." action="#{technologicalMapListPage.selectTechnologicalMapGroup}" reRender="technologicalMapGroupSelectModalPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapGroupSelectModalPanel')}.show();"
                                       styleClass="command-link" style="width: 25px; float: right;" />
                </h:panelGroup>
                <h:outputText value="Удаленные элементы" styleClass="output-text" escape="true"/>
                <h:selectOneMenu id="selectDeletedStatus" value="#{technologicalMapListPage.deletedStatusSelected}" styleClass="input-text long-field">
                    <f:selectItem itemLabel="Скрыть" itemValue="false"/>
                    <f:selectItem itemLabel="Показать" itemValue="true"/>
                </h:selectOneMenu>

            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">

                <a4j:commandButton value="Применить" action="#{technologicalMapListPage.onSearch}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />

                <a4j:commandButton value="Очистить" action="#{technologicalMapListPage.onClear}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>

    </h:panelGrid>

    <a4j:status id="technologicalMapListTableStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="technologicalMapListTable" var="technologicalMap" value="#{technologicalMapListPage.technologicalMapList}"
                    rows="10" rowKeyVar="row" columnClasses="center-aligned-column" footerClass="data-table-footer">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Идентификатор" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{technologicalMap.globalId}" />
        </rich:column>
        <rich:column  headerClass="column-header" width="200">
            <f:facet name="header">
                <h:outputText value="GUID" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{technologicalMap.guid}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Наименование технологическая карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{technologicalMap.nameOfTechnologicalMap}" action="#{technologicalMapViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{technologicalMap}" target="#{selectedTechnologicalMapGroupPage.currentTechnologicalMap}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Номер технологической карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{technologicalMap.numberOfTechnologicalMap}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="Статус технологической карты" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:selectBooleanCheckbox value="#{technologicalMap.deletedState}" readonly="true" disabled="true"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Редактировать" styleClass="output-text" escape="true"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{technologicalMapEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{technologicalMap}" target="#{selectedTechnologicalMapGroupPage.currentTechnologicalMap}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column style="text-align:center">
            <f:facet name="header">
                <h:outputText value="Удалить" escape="true" styleClass="output-text"/>
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link" rendered="#{technologicalMap.deletedState}"
                             oncomplete="#{rich:component('removedTechnologicalMapItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{technologicalMap}" target="#{technologicalMapEditPage.currentTechnologicalMap}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="technologicalMapListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
