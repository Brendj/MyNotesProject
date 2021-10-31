<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalContractSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalContractSelectorPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор контракта" />
    </f:facet>
    <a4j:form id="modalContractSelectorForm" binding="#{mainPage.contractSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalContractSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{mainPage.contractSelectPage.selectedItem.contractName}"
                                     readonly="true" size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.contractSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text" />
                        <a4j:commandLink action="#{mainPage.showContractSelectPage}"
                                         reRender="modalContractSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/search.png" style="border: 0;" />
                        </a4j:commandLink>
                        <a4j:commandLink action="#{mainPage.contractSelectPage.cancelSelection}"
                                         reRender="modalContractSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value="" target="#{mainPage.contractSelectPage.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalContractSelectorTable"
                                    value="#{mainPage.contractSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalContractSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.contractSelectPage.selectedItem}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.contractName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalContractSelectorTable" renderIfSinglePage="false"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeContractSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeContractSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContractSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                            <f:setPropertyActionListener value="#{null}"
                                                         target="#{mainPage.contractSelectPage.selectedItem}" />
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>
