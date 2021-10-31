<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Категории --%>
<%--@elvariable id="categoryDiscountDSZNListPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn.CategoryDiscountDSZNListPage"--%>
<h:panelGrid id="categoryDSZNListPanel" binding="#{categoryDiscountDSZNListPage.pageComponent}" styleClass="borderless-grid">
    <rich:dataTable id="categoryDSZNTable" value="#{categoryDiscountDSZNListPage.items}" var="item" rows="20"
                    columnClasses="left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header" style="text-align:right">
            <f:facet name="header">
                <h:outputText escape="true" value="Код льготы ДТиСЗН" />
            </f:facet>
            <h:outputText escape="true" value="#{item.code}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Описание льготы ДТиСЗН" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.description}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="ИД льготы ИСПП" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.idOfCategoryDiscount}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Наименование льготы ИСПП" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.categoryName}" />
        </rich:column>
        <rich:column headerClass="center-aligned-column" style="text-align:left">
            <f:facet name="header">
                <h:panelGroup>
                    <h:outputText styleClass="output-text" escape="true" value="Код льготы ЕТП" />
                </h:panelGroup>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{item.ETPCode}" />
        </rich:column>
        <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToEditCategory}"
                     style="text-align:center">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{categoryDiscountDSZNEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfCategoryDiscountDSZN}" target="#{categoryDiscountDSZNEditPage.idOfCategoryDiscountDSZN}" />
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
                <f:setPropertyActionListener value="#{categoryDiscountDSZNListPage}"
                                             target="#{confirmDeletePage.listener}" />
                <f:setPropertyActionListener value="#{item.idOfCategoryDiscountDSZN }"
                                             target="#{confirmDeletePage.entityId}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="categoryDSZNTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

</h:panelGrid>
