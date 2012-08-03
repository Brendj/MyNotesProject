<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="technologicalMapGroupItemsPanel" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel"--%>
<rich:modalPanel id="technologicalMapGroupSelectModalPanel" autosized="true" width="200" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText escape="true" value="Выберите группу" />
    </f:facet>
    <a4j:form id="technologicalMapGroupModalForm" styleClass="borderless-form"
              eventsQueue="technologicalMapGroupSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:outputText value="#{technologicalMapGroupItemsPanel.selectTechnologicalMapGroup.nameOfGroup}"  styleClass="output-text"
                                      style="margin-right: 2px; margin-top: 2px; width: 447px; float:left; min-height: 14px; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{technologicalMapGroupItemsPanel.filter}" size="48" maxlength="128"
                                     styleClass="input-text" />
                        <a4j:commandLink action="#{technologicalMapGroupItemsPanel.updateConfigurationProviderSelectPage}" reRender="technologicalMapGroupModalForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{technologicalMapGroupItemsPanel.updateConfigurationProviderSelectPage}" reRender="technologicalMapGroupModalForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{technologicalMapGroupItemsPanel.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable value="#{technologicalMapGroupItemsPanel.technologicalMapGroupList}" var="technologicalMapGroup" rowKeyVar="row"
                                    width="100%" align="center" id="modalProductGroupSelectorTable" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="technologicalMapGroupModalForm">
                            <f:setPropertyActionListener value="#{technologicalMapGroup}"
                                                         target="#{technologicalMapGroupItemsPanel.selectTechnologicalMapGroup}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="№" />
                            </f:facet>
                            <h:outputText value="#{row+1}"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Наименование группы" />
                            </f:facet>
                            <h:outputText value="#{technologicalMapGroup.nameOfGroup}"/>
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalProductGroupSelectorTable" renderIfSinglePage="false" maxPages="5"
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
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGrid styleClass="borderless-div" columns="2">
                        <a4j:commandButton value="Ok" action="#{technologicalMapGroupItemsPanel.addTechnologicalMapGroup}"
                                           reRender="workspaceTogglePanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapGroupSelectModalPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />

                        <a4j:commandButton value="Отмена" styleClass="command-button" style="width: 80px; float: right;"
                                           action="#{technologicalMapGroupItemsPanel.cancel}"
                                           reRender="workspaceTogglePanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapGroupSelectModalPanel')}.hide();" />
                    </h:panelGrid>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>