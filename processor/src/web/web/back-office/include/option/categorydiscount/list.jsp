<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Категории --%>
<%--@elvariable id="categoryDiscountListPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountListPage"--%>
<h:panelGrid id="categoryListPanel" binding="#{categoryDiscountListPage.pageComponent}" styleClass="borderless-grid">
    <rich:dataTable id="categoryTable" value="#{categoryDiscountListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header" style="text-align:right">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfCategoryDiscount}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Наименование" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.categoryName}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Описание" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.description}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left" width="300">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Льготы ДСЗН" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.categoriesDSZN}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Тип организации" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.organizationTypeString}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Запрет изменения в АРМ" />
                </h:panelGroup>
            </f:facet>
            <h:selectBooleanCheckbox value="#{item.blockedToChange}" styleClass="output-text" disabled="true"/>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditCategory}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{categoryDiscountEditPage.show}" styleClass="command-link" rendered="#{item.idOfCategoryDiscount>=0}">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfCategoryDiscount}" target="#{categoryDiscountEditPage.idOfCategoryDiscount}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditCategory}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('confirmDeletePanel')}.show();">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{categoryDiscountListPage}"
                                             target="#{confirmDeletePage.listener}" />
                <f:setPropertyActionListener value="#{item.idOfCategoryDiscount }"
                                             target="#{confirmDeletePage.entityId}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="categoryTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

    <!--h:commandButton value="Выгрузить в CSV" action="{mainPage.showCategoryCSVList}" styleClass="command-button" /-->
</h:panelGrid>
