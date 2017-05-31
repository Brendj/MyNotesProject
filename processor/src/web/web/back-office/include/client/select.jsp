<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalClientSelectorPanel" autosized="true" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalClientSelectorPanel')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор клиента" />
    </f:facet>
    <a4j:form id="modalClientSelectorForm" styleClass="borderless-form" eventsQueue="modalClientSelectorFormEventsQueue"
              binding="#{mainPage.clientSelectPage.pageComponent}">
        <table class="borderless-grid" width="100%">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid">
                        <h:inputText value="#{mainPage.clientSelectPage.selectedItem.caption}" size="64" readonly="true"
                                     styleClass="input-text" />
                    </h:panelGrid>
                </td>
            </tr>
            <tr>
                <td>
                    <rich:simpleTogglePanel id="modalClientSelectorFilterPanel"
                                            label="Фильтр (#{mainPage.clientSelectPage.clientFilter.status})"
                                            switchType="client" eventsQueue="mainFormEventsQueue" opened="true"
                                            headerClass="filter-panel-header">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Организация" styleClass="output-text" />
                            <h:panelGroup styleClass="borderless-div">
                                <h:inputText id="modalClientSelectorOrgFilter"
                                             value="#{mainPage.clientSelectPage.clientFilter.org.shortName}"
                                             readonly="true" styleClass="input-text" style="margin-right: 2px;" />
                                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}"
                                                   reRender="modalOrgSelectorPanel"
                                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show()"
                                                   styleClass="command-link" style="width: 25px;" />
                            </h:panelGroup>
                            <h:outputText escape="true" value="Договор" styleClass="output-text" />
                            <h:panelGrid columns="2" styleClass="borderless-grid">
                                <h:outputText escape="true" value="Номер лицевого счета" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractId}" maxlength="16"
                                             styleClass="input-text" />
                                <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractPerson.surname}"
                                             maxlength="128" styleClass="input-text" />
                                <h:outputText escape="true" value="Имя" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractPerson.firstName}"
                                             maxlength="64" styleClass="input-text" />
                                <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractPerson.secondName}"
                                             maxlength="128" styleClass="input-text" />
                                <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.contractPerson.idDocument}"
                                             maxlength="128" styleClass="input-text" />
                            </h:panelGrid>
                            <h:outputText escape="true" value="Обслуживается" styleClass="output-text" />
                            <h:panelGrid columns="2" styleClass="borderless-grid">
                                <h:outputText escape="true" value="Фамилия" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.surname}"
                                             maxlength="128" styleClass="input-text" />
                                <h:outputText escape="true" value="Имя" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.firstName}"
                                             maxlength="64" styleClass="input-text" />
                                <h:outputText escape="true" value="Отчество" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.secondName}"
                                             maxlength="128" styleClass="input-text" />
                                <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
                                <h:inputText value="#{mainPage.clientSelectPage.clientFilter.person.idDocument}"
                                             maxlength="128" styleClass="input-text" />
                            </h:panelGrid>
                            <h:outputText escape="true" value="Наличие карт" styleClass="output-text" />
                            <h:selectOneMenu value="#{mainPage.clientSelectPage.clientFilter.clientCardOwnCondition}"
                                             styleClass="input-text">
                                <f:selectItems
                                        value="#{mainPage.clientSelectPage.clientFilter.clientCardOwnMenu.items}" />
                            </h:selectOneMenu>
                        </h:panelGrid>
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <a4j:commandButton value="Применить" action="#{mainPage.updateClientSelectPage}"
                                               reRender="modalClientSelectorForm" styleClass="command-button" />
                            <a4j:commandButton value="Очистить" action="#{mainPage.clearClientSelectPageFilter}"
                                               reRender="modalClientSelectorForm" ajaxSingle="true"
                                               styleClass="command-button" />
                        </h:panelGrid>
                    </rich:simpleTogglePanel>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalClientSelectorTable"
                                    value="#{mainPage.clientSelectPage.items}" var="item" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalClientSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.clientSelectPage.selectedItem}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.caption}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalClientSelectorTable" renderIfSinglePage="false" maxPages="5"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeClientSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="#{rich:component('modalClientSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.completeClientSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;">
                            <f:setPropertyActionListener value="#{null}"
                                                         target="#{mainPage.clientSelectPage.selectedItem}" />
                        </a4j:commandButton>
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>