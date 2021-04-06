<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 04.04.2021
  Time: 11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalDishSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalDishSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор блюда>" />
    </f:facet>
    <a4j:form id="modalDishSelectorForm" binding="#{mainPage.complexDishSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalDishSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{mainPage.complexDishSelectPage.selectedItem.dishName}" readonly="true"
                                     size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.complexDishSelectPage.filter}" size="52" maxlength="128"
                                     styleClass="input-text">
                        </h:inputText>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalDishSelectorTable"
                                    value="#{mainPage.complexDishSelectPage.dish}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column" rowKeyVar="row"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalDishSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.complexDishSelectPage.selectedItem}" />
                        </a4j:support>

                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
                        </rich:column>

                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.dishName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalDishSelectorTable" renderIfSinglePage="false"
                                               maxPages="5" fastControls="hide" stepControls="auto"
                                               boundaryControls="hide">
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeDishSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalDishSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeDishSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalDishSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                            <f:setPropertyActionListener value="#{null}"
                                                         target="#{mainPage.complexDishSelectPage.selectedItem}" />
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>