<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalUserSelectorPanel" autosized="true" width="500" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalUserSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор пользователя" />
    </f:facet>
    <a4j:form id="modalUserSelectorForm" binding="#{mainPage.userSelectPage.pageComponent}" styleClass="borderless-form"
              eventsQueue="modalUserSelectorFormEventsQueue">
        <table class="borderless-grid" width="480">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid" columns="2">
                        <h:inputText value="#{mainPage.userSelectPage.selectedItem.userName}" readonly="true" size="64"
                                     styleClass="input-text" />
                        <a4j:commandLink styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <a4j:support event="onclick" action="#{mainPage.userSelectPage.cancelFilter}"
                                         reRender="modalUserSelectorForm" />
                            <f:setPropertyActionListener value="" target="#{mainPage.userSelectPage.filter}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.userSelectPage.filter}" size="48" maxlength="128"
                                         styleClass="input-text">
                                <a4j:support event="onkeyup" action="#{mainPage.updateUserSelectPage}"
                                             reRender="modalUserSelectorUserTable" />
                            </h:inputText>
                        </h:panelGrid>
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalUserSelectorUserTable"
                                    value="#{mainPage.userSelectPage.items}" var="item" rows="12"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalUserSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.userSelectPage.selectedItem}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Ид." />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.idOfUser}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="Имя пользователя" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.userName}"
                                          styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <f:facet name="header">
                                <h:outputText escape="true" value="ФИО" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.getSurnameAndFirstLetters()}"
                                          styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalUserSelectorUserTable" renderIfSinglePage="false" maxPages="5"
                                               fastControls="hide" stepControls="auto" boundaryControls="hide">
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeUserSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id},userFilter"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalUserSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelUserSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalUserSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>
