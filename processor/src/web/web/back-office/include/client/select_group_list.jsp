<%--
  ~ Copyright (c) 2021. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Artem Saparov
  Date: 19.04.2021
  Time: 12:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalClientGroupSelectorListPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalClientGroupSelectorListPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор группы" />
    </f:facet>
    <a4j:form id="modalClientGroupSelectorListForm" binding="#{mainPage.clientGroupListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalClientGroupSelectorListFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid" >
                        <h:inputText id="groupSelected" value="#{mainPage.clientGroupListSelectPage.selectedItems}" readonly="true"
                                     size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientGroupListSelectPage.filter}" size="52" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.updateClientGroupListSelectPage}" requestDelay="1000"
                                         reRender="modalClientGroupSelectorListClientGroupTable" >
                                <f:param name="idOfOrg" value="#{mainPage.clientGroupListSelectPage.idOfOrg}" />
                            </a4j:support>
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.updateClientGroupListSelectPage}"
                                         reRender="modalClientGroupSelectorListForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.clientGroupListSelectPage.filter}" />
                            <f:param name="idOfOrg" value="#{mainPage.clientGroupListSelectPage.idOfOrg}" />
                            <a4j:support event="onclick" action="#{mainPage.clientGroupListSelectPage.cancelFilter}"
                                         reRender="modalClientGroupSelectorListForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllGroupListSelectedItemsList}"
                                           reRender="modalClientGroupSelectorListForm" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearGroupListSelectedItemsList}"
                                           reRender="modalClientGroupSelectorListForm" styleClass="command-link"
                                           value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalClientGroupSelectorListClientGroupTable"
                                    value="#{mainPage.clientGroupListSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="center-aligned-column, center-aligned-column, left-aligned-column" rowKeyVar="row"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'" >
                        <rich:column headerClass="column-header" style="width: auto" >
                            <f:facet name="header">
                                <h:outputText value="Выбор" />
                            </f:facet>
                            <h:selectBooleanCheckbox value="#{item.selected}" styleClass="output-text">
                                <a4j:support event="onchange" action="#{mainPage.clientGroupListSelectPage.updateSelectedIds(item.groupName, item.selected)}"
                                             reRender="groupSelected" />
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header"  style="width: auto" >
                            <f:facet name="header">
                                <h:outputText value="ИД ОО" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.idoforg}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header"  style="width: auto">
                            <f:facet name="header">
                                <h:outputText value="Группа" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.groupName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalClientGroupSelectorListClientGroupTable" renderIfSinglePage="false"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeClientGroupListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorListPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; " />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>