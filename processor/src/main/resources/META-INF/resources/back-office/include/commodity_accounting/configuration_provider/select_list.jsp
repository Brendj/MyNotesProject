<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="configurationProviderListItemsPanel" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderListItemsPanel"--%>
<rich:modalPanel id="configurationProviderSelectListModalPanel" autosized="true" width="200" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('configurationProviderSelectListModalPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор производственной конфигурации" />
    </f:facet>
    <a4j:form id="configurationProviderListModalForm" styleClass="borderless-form"
              eventsQueue="configurationProviderSelectorListFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{configurationProviderListItemsPanel.selectedIds}" readonly="true" size="64"
                                     styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{configurationProviderListItemsPanel.filter}" size="48" maxlength="128"
                                     styleClass="input-text" />
                        <a4j:commandLink action="#{configurationProviderListItemsPanel.updateConfigurationProviderSelectPage}" reRender="configurationProviderListModalForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{configurationProviderListItemsPanel.updateConfigurationProviderSelectPage}" reRender="configurationProviderListModalForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{configurationProviderListItemsPanel.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable value="#{configurationProviderListItemsPanel.configurationProviderList}" var="configurationProvider" rowKeyVar="row"
                                    width="100%" align="center" id="modalСonfigurationProviderListSelectorTable" rows="20"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column>
                            <h:selectBooleanCheckbox value="#{configurationProvider.selected}" styleClass="output-text">
                                <a4j:support event="onchange" />
                                             <%--action="#{configurationProviderListItemsPanel.updateSelectedIds(configurationProvider.idOfConfigurationProvider, configurationProvider.selected)}"
                                             reRender="contragentListValue" />--%>
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Ид." />
                            </f:facet>
                            <h:outputText value="#{configurationProvider.idOfConfigurationProvider}"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Наименование конфигурации" />
                            </f:facet>
                            <h:outputText value="#{configurationProvider.name}"/>
                        </rich:column>

                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:datascroller for="modalСonfigurationProviderListSelectorTable" renderIfSinglePage="false" maxPages="5" styleClass="data-table-footer"
                                       fastControls="hide" stepControls="auto" boundaryControls="hide" reRender="configurationProviderListModalForm">
                        <f:facet name="previous">
                            <h:graphicImage value="/images/16x16/left-arrow.png" />
                        </f:facet>
                        <f:facet name="next">
                            <h:graphicImage value="/images/16x16/right-arrow.png" />
                        </f:facet>
                    </rich:datascroller>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGrid styleClass="borderless-div" columns="2">
                        <a4j:commandButton value="Ok" action="#{configurationProviderListItemsPanel.completeConfigurationProviderSelection}"
                                           reRender="workspaceTogglePanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectListModalPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />

                        <a4j:commandButton value="Отмена" action="#{configurationProviderListItemsPanel.cancel}"
                                           reRender="workspaceTogglePanel" styleClass="command-button" style="width: 80px;"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectListModalPanel')}.hide();" />
                    </h:panelGrid>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>