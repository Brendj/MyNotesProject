<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2020. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalComplexListSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalComplexListSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор комплекса" />
    </f:facet>
    <a4j:form id="modalComplexListSelectorForm" binding="#{mainPage.complexWebListSelectPage.pageComponent}"
              styleClass="borderless-form" eventsQueue="modalComplexListSelectorFormEventsQueue">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText id="contragentListValue" value="#{mainPage.complexWebListSelectPage.selectedItems}"
                                     readonly="true" size="64" styleClass="input-text" />
                    </h:panelGrid>
                    <h:panelGrid columns="4" styleClass="borderless-grid">
                        <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                        <h:inputText value="#{mainPage.complexWebListSelectPage.filter}" size="48" maxlength="128"
                                     styleClass="input-text">
                            <a4j:support event="onkeyup" action="#{mainPage.showComplexListSelectPage}"
                                         reRender="modalComplexListSelectorTable" />
                        </h:inputText>
                        <a4j:commandLink action="#{mainPage.showComplexListSelectPage}"
                                         reRender="modalComplexListSelectorForm" styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <f:setPropertyActionListener value=""
                                                         target="#{mainPage.complexWebListSelectPage.filter}" />
                            <a4j:support event="onclick" action="#{mainPage.complexWebListSelectPage.cancelFilter}"
                                         reRender="modalComplexListSelectorForm" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="2" styleClass="borderless-grid">
                        <a4j:commandButton action="#{mainPage.selectAllComplexListSelectedItemsList}"
                                           reRender="modalComplexListSelectorForm" styleClass="command-link"
                                           value="Выбрать все" />
                        <a4j:commandButton action="#{mainPage.clearComplexListSelectedItemsList}"
                                           reRender="modalComplexListSelectorForm" styleClass="command-link"
                                           value="Очистить выбор" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalComplexListSelectorTable"
                                    value="#{mainPage.complexWebListSelectPage.items}" var="item" rows="15"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column>
                            <h:selectBooleanCheckbox value="#{item.selected}" styleClass="output-text">
                                <a4j:support event="onchange" action="#{mainPage.complexWebListSelectPage.updateSelectedIds(item.idOfComplex, item.selected)}"
                                             reRender="contragentListValue" />
                            </h:selectBooleanCheckbox>
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfComplex}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.complexName}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalComplexListSelectorTable" renderIfSinglePage="false"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeComplexListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalComplexListSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelComplexListSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalComplexListSelectorPanel')}.hide();return false;"
                                           styleClass="command-button" style="width: 80px;">
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>