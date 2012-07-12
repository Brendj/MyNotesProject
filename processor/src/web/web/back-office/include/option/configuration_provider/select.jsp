<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="productItemsPanel" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItemsPanel"--%>
<rich:modalPanel id="technologicalMapProductSelectModalPanel" autosized="true" width="200" headerClass="modal-panel-header">
    <f:facet name="header">
        <h:outputText escape="true" value="Выберите продукты" />
    </f:facet>
    <a4j:form id="technologicalMapProductModalForm" styleClass="borderless-form"
              eventsQueue="technologicalMapProductSelectorFormEventsQueue">
        <rich:dataTable value="#{productItemsPanel.productItems}" var="item" rowKeyVar="row">
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="№" />
                </f:facet>
                <h:outputText value="#{row+1}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="" />
                </f:facet>
                <h:selectBooleanCheckbox value="#{item.checked}"/>
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="Наименование продукта" />
                </f:facet>
                <h:outputText value="#{item.product.productName}"/>
            </rich:column>
        </rich:dataTable>
        <h:panelGrid columns="2">
            <a4j:commandButton value="Ok" action="#{productItemsPanel.addProducts}"
                               reRender="workspaceTogglePanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('technologicalMapProductSelectModalPanel')}.hide();"
                               styleClass="command-button" style="width: 80px; margin-right: 4px;" />

            <a4j:commandButton value="Отмена" styleClass="command-button"
                               onclick="#{rich:component('technologicalMapProductSelectModalPanel')}.hide();return false;" />
        </h:panelGrid>
    </a4j:form>
</rich:modalPanel>