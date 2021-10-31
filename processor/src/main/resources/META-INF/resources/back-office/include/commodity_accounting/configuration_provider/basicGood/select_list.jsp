<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="basicGoodListItemsPanel" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good.BasicGoodListItemsPanel"--%>
<rich:modalPanel id="basicGoodListItemsPanel" autosized="true" width="200" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('basicGoodListItemsPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор элементов базовой корзины" />
    </f:facet>
    <a4j:form id="basicGoodListModalForm" styleClass="borderless-form"
              eventsQueue="basicGoodSelectorListFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{basicGoodListItemsPanel.filter}" size="48" maxlength="128"
                                     styleClass="input-text" />
                        <a4j:commandLink action="#{basicGoodListItemsPanel.updateBasicGoodSelectPage}" reRender="modalBasicGoodListSelectorTable"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{basicGoodListItemsPanel.updateBasicGoodSelectPage}" reRender="modalBasicGoodListSelectorTable"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{basicGoodListItemsPanel.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{basicGoodListItemsPanel.selectAll}" reRender="modalBasicGoodListSelectorTable"
                                           styleClass="command-button" value="Выбрать все" />
                        <a4j:commandButton action="#{basicGoodListItemsPanel.clearAll}" reRender="modalBasicGoodListSelectorTable"
                                           styleClass="command-button" value="Очистить все" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable value="#{basicGoodListItemsPanel.basicGoodList}" var="basicGood" rowKeyVar="row"
                                    width="100%" align="center" id="modalBasicGoodListSelectorTable" rows="20"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column>
                            <h:selectBooleanCheckbox value="#{basicGood.selected}" styleClass="output-text">
                                <a4j:support event="onchange" />
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Ид." />
                            </f:facet>
                            <h:outputText value="#{basicGood.idOfBasicGood}"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Название" />
                            </f:facet>
                            <h:outputText value="#{basicGood.name}"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Масса нетто" />
                            </f:facet>
                            <h:outputText value="#{basicGood.netWeight}"/>
                        </rich:column>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:datascroller for="modalBasicGoodListSelectorTable" renderIfSinglePage="false" maxPages="5" styleClass="data-table-footer"
                                       fastControls="hide" stepControls="auto" boundaryControls="hide" reRender="basicGoodListItemsPanel">
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
                        <a4j:commandButton value="Ok" action="#{basicGoodListItemsPanel.completeBasicGoodSelection}"
                                           reRender="workspaceTogglePanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('basicGoodListItemsPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />

                        <a4j:commandButton value="Отмена" action="#{basicGoodListItemsPanel.cancel}"
                                           reRender="workspaceTogglePanel" styleClass="command-button" style="width: 80px;"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('basicGoodListItemsPanel')}.hide();" />
                    </h:panelGrid>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>