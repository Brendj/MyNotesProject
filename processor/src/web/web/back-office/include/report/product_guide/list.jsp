<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 10.05.12
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Конфигурация поставщика" styleClass="output-text" />
    <h:selectOneMenu id="selectCurrentConfigurationProvider" value="#{mainPage.currentConfigurationProvider}" styleClass="input-text long-field" >
        <f:selectItems value="#{mainPage.productGuideListPage.configurationProviderMenu.items}" />
        <a4j:support event="onchange" action="#{mainPage.updateProductGuideListPage}" reRender="productGuideListTable"  />
    </h:selectOneMenu>

    <h:outputText escape="true" value="Удаленные элементы" styleClass="output-text" />
    <h:selectOneMenu id="selectShowDeleted" value="#{mainPage.productGuideListPage.showDeletedSelectedText}" styleClass="input-text long-field" >
        <f:selectItems value="#{mainPage.productGuideListPage.showDeletedComboMenu.items}" />
        <a4j:support event="onchange" action="#{mainPage.updateProductGuideListPage}" reRender="productGuideListTable"  />
    </h:selectOneMenu>

</h:panelGrid>



<rich:extendedDataTable id="productGuideListTable" value="#{mainPage.productGuideListPage.items}" var="item"
        columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column" width="100%" rowKeyVar="row" rows="15"
        sortMode="multi" selectionMode="single">

    <rich:column headerClass="column-header"  sortable="true" sortBy="#{item.code}" filterBy="#{item.code}" filterEvent="onkeyup" width="100px">
        <f:facet name="header">
            <h:outputText escape="true" value="Код" />
        </f:facet>
        <rich:inplaceInput layout="block" value="#{item.code}"
                id="inplaceCode"
                changedHoverClass="hover" viewHoverClass="hover"
                viewClass="inplace" changedClass="inplace"
                selectOnEdit="true" editEvent="ondblclick" >
            <a4j:support event="onchange" action="#{mainPage.addEditedProductGuideItemId(item.idOfProductGuide)}" reRender="selectCurrentConfigurationProvider"  />
        </rich:inplaceInput>
    </rich:column>

    <rich:column headerClass="column-header" sortable="true" sortBy="#{item.fullName}" filterBy="#{item.fullName}" filterEvent="onkeyup" width="340px">
        <f:facet name="header">
            <h:outputText escape="true" value="Наименование пищевого продукта полное" />
        </f:facet>
        <rich:inplaceInput layout="block" value="#{item.fullName}"
                id="inplaceFullName"
                requiredMessage="Наименование пищевого продукта полное с кодом #{item.code} пусто."
                changedHoverClass="hover" viewHoverClass="hover"
                viewClass="inplace" changedClass="inplace"
                selectOnEdit="true" editEvent="ondblclick" >
            <a4j:support event="onchange" action="#{mainPage.addEditedProductGuideItemId(item.idOfProductGuide)}" reRender="selectCurrentConfigurationProvider"  />
        </rich:inplaceInput>
    </rich:column>

    <rich:column headerClass="column-header" sortable="true" sortBy="#{item.productName}" filterBy="#{item.productName}" filterEvent="onkeyup" width="170px">
        <f:facet name="header">
            <h:outputText escape="true" value="Товарное название" />
        </f:facet>
        <rich:inplaceInput layout="block" value="#{item.productName}"
                id="inplaceProductName"
                changedHoverClass="hover" viewHoverClass="hover"
                viewClass="inplace" changedClass="inplace"
                selectOnEdit="true" editEvent="ondblclick" >
            <a4j:support event="onchange" action="#{mainPage.addEditedProductGuideItemId(item.idOfProductGuide)}" reRender="selectCurrentConfigurationProvider"  />
        </rich:inplaceInput>
    </rich:column>

    <rich:column headerClass="column-header" sortable="true" sortBy="#{item.okpCode}" filterBy="#{item.okpCode}" filterEvent="onkeyup" width="140px">
        <f:facet name="header">
            <h:outputText escape="true" value="Код (коды) ОКП" />
        </f:facet>
        <rich:inplaceInput layout="block" value="#{item.okpCode}"
                id="inplaceOkpCode"
                changedHoverClass="hover" viewHoverClass="hover"
                viewClass="inplace" changedClass="inplace"
                selectOnEdit="true" editEvent="ondblclick" >
            <a4j:support event="onchange" action="#{mainPage.addEditedProductGuideItemId(item.idOfProductGuide)}" reRender="selectCurrentConfigurationProvider"  />
        </rich:inplaceInput>
    </rich:column>

    <rich:column headerClass="column-header" width="100px">
        <f:facet name="header">
            <h:outputText escape="true" value="Удален" />
        </f:facet>
        <h:selectBooleanCheckbox value="#{item.deleted}"
                                 styleClass="output-text" >
            <a4j:support event="onchange" action="#{mainPage.addEditedProductGuideItemId(item.idOfProductGuide)}" reRender="selectCurrentConfigurationProvider"  />
        </h:selectBooleanCheckbox>
    </rich:column>

    <rich:column headerClass="column-header" width="100px">
        <f:facet name="header">
            <h:outputText escape="true" value="Доп. инф." />
        </f:facet>
        <h:graphicImage value="/images/16x16/person.png" style="border: 0;" />
        <rich:toolTip>
            <h:outputText escape="false" value="#{item.getAdditionInfo}" />
        </rich:toolTip>
    </rich:column>

    <rich:column headerClass="column-header" width="110px" >
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>

        <a4j:commandLink ajaxSingle="true" styleClass="command-link"  disabled="#{item.deleted}"
                         oncomplete="#{rich:component('removedProductGuideItemDeletePanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfProductGuide}"
                                         target="#{mainPage.removedProductGuideItemId}" />
        </a4j:commandLink>

    </rich:column>

</rich:extendedDataTable>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

<h:panelGrid columns="3" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateProducts}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showProductGuideListPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    <a4j:commandButton value="Добавить" action="#{mainPage.addProductGuideInListPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>