<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 29.02.12
  Time: 13:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalClientGroupSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalClientGroupSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор группы" />
    </f:facet>
    <a4j:form id="modalClientGroupSelectorForm" binding="#{mainPage.clientGroupSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalClientGroupSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{mainPage.clientGroupSelectPage.selectedItem.groupName}" readonly="true"
                                     size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.clientGroupSelectPage.filter}" size="52" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.updateClientGroupSelectPage}" requestDelay="1000"
                                         reRender="modalClientGroupSelectorClientGroupTable" >
                                <f:param name="idOfOrg" value="#{mainPage.clientGroupSelectPage.idOfOrg}" />
                            </a4j:support>
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.updateClientGroupSelectPage}"
                                         reRender="modalClientGroupSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.clientGroupSelectPage.filter}" />
                            <f:param name="idOfOrg" value="#{mainPage.clientGroupSelectPage.idOfOrg}" />
                            <a4j:support event="onclick" action="#{mainPage.clientGroupSelectPage.cancelFilter}"
                                         reRender="modalClientGroupSelectorForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalClientGroupSelectorClientGroupTable"
                                    value="#{mainPage.clientGroupSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column" rowKeyVar="row"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalClientGroupSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.clientGroupSelectPage.selectedItem}" />
                        </a4j:support>

                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
                        </rich:column>

                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.groupName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalClientGroupSelectorClientGroupTable" renderIfSinglePage="false"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeClientGroupSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeClientGroupSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientGroupSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                            <f:setPropertyActionListener value="#{null}"
                                                         target="#{mainPage.clientGroupSelectPage.selectedItem}" />
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>