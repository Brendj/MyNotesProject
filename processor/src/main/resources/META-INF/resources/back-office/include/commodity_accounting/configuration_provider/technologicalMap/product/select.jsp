<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="productListItemsPanel" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItemsPanel"--%>

<rich:modalPanel id="modalTechnologicalMapListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalTechnologicalMapListSelectorPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор продуктов" />
    </f:facet>
    <a4j:form id="modalTechnologicalMapListSelectorForm" binding="#{productListItemsPanel.pageComponent}" styleClass="borderless-form"
              eventsQueue="modalTechnologicalMapListSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{productListItemsPanel.filter}" size="48" maxlength="128"
                                     styleClass="input-text" />
                        <a4j:commandLink action="#{productListItemsPanel.updateTechnologicalMapProductListSelectPage}" reRender="modalTechnologicalMapListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{productListItemsPanel.updateTechnologicalMapProductListSelectPage}" reRender="modalTechnologicalMapListSelectorForm"
                                         styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgListSelectPage.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable value="#{productListItemsPanel.productItems}" var="item" rows="15"
                                    width="100%" align="center" id="modalTechnologicalMapListSelectorTechnologicalMapTable"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column headerClass="column-header">
                            <h:selectBooleanCheckbox value="#{item.checked}"/>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText value="#{item.product.productName}"/>
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalTechnologicalMapListSelectorTechnologicalMapTable" renderIfSinglePage="false" maxPages="5"
                                               fastControls="hide" stepControls="auto" boundaryControls="hide">
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
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{productListItemsPanel.addProducts}"
                                           reRender="workspaceTogglePanel"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalTechnologicalMapListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{productListItemsPanel.cancel}"
                                           reRender="#{productListItemsPanel.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalTechnologicalMapListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>