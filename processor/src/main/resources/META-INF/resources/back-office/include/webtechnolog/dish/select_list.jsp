<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 07.04.2021
  Time: 8:47
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalDishListSelectorPanel" autosized="true" headerClass="modal-panel-header" minWidth="1100">
    <rich:hotKey key="esc" handler="#{rich:component('modalDishListSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор блюда" />
    </f:facet>
    <a4j:form id="modalDishListSelectorForm" binding="#{mainPage.dishWebListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalDishListSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText id="dishValue" value="#{mainPage.dishWebListSelectPage.selectedName}"
                                     readonly="true" size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Введите название блюда: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.dishWebListSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.showDishListSelectPage}"
                                         reRender="modalDishListSelectorTable" />
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.showDishListSelectPage}"
                                         reRender="modalDishListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value=""
                                                         target="#{mainPage.dishWebListSelectPage.filter}" />
                            <a4j:support event="onclick" action="#{mainPage.dishWebListSelectPage.cancelFilter}"
                                         reRender="modalDishListSelectorForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalDishListSelectorTable"
                                    value="#{mainPage.dishWebListSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column, center-aligned-column,
                                    center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,center-aligned-column, center-aligned-column "
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" action="#{mainPage.dishWebListSelectPage.updateSelectedIds(item.idOfDish, item.dishName)}"
                                     reRender="dishValue" />

                        <f:facet name="header">
                            <rich:columnGroup>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Название" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Цена" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="ВК" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Тип питания" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Дата начала" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Дата окончания" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="Код ПП" />
                                </rich:column>
                                <rich:column headerClass="column-header">
                                    <h:outputText escape="true" value="ИД блюда" />
                                </rich:column>
                            </rich:columnGroup>
                        </f:facet>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.dishName}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.price}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfAgeGroupItem}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfTypeOfProductionItem}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.dateOfBeginMenuIncluding}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.dateOfEndMenuIncluding}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.code}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfDish}" styleClass="output-text" />
                        </rich:column>

                        <f:facet name="footer">
                            <rich:datascroller for="modalDishListSelectorTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
                                <f:facet name="previous">
                                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                                </f:facet>
                                <f:facet name="next">
                                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                                </f:facet>
                                <a4j:support event="onpagechange" />
                            </rich:datascroller>
                        </f:facet>
                    </rich:dataTable>
                </td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    <h:panelGroup styleClass="borderless-div">
                        <a4j:commandButton value="Ok" action="#{mainPage.completeDishListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalDishListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton action="#{mainPage.clearDishListSelectedItemsList}"
                                           reRender="modalDishListSelectorForm" styleClass="command-button" style="width: 120px; margin-right: 4px;"
                                           value="Очистить выбор" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelDishListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalDishListSelectorPanel')}.hide();return false;"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>