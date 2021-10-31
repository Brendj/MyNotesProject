<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="modalOrgSelectorPanel" autosized="true" width="700" headerClass="modal-panel-header">
    <rich:hotKey key="esc" handler="#{rich:component('modalOrgSelectorPanel')}.hide();return false;" />
    <f:facet name="header">
        <h:outputText escape="true" value="Выбор организации" />
    </f:facet>
    <a4j:form id="modalOrgSelectorForm" binding="#{mainPage.orgSelectPage.pageComponent}" styleClass="borderless-form"
              eventsQueue="modalOrgSelectorFormEventsQueue">
        <table class="borderless-grid" width="750">
            <tr>
                <td style="text-align: left;">
                    <h:panelGrid styleClass="borderless-grid" columns="2">
                        <h:inputText value="#{mainPage.orgSelectPage.selectedItem.shortName}" readonly="true" size="64"
                                     styleClass="input-text" />
                        <a4j:commandLink styleClass="command-link">
                            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                            <a4j:support event="onclick" action="#{mainPage.orgSelectPage.cancelFilter}"
                                         reRender="modalOrgSelectorForm" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgSelectPage.filter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgSelectPage.idFilter}" />
                            <f:setPropertyActionListener value="" target="#{mainPage.orgSelectPage.region}" />
                        </a4j:commandLink>
                    </h:panelGrid>
                    <h:panelGrid columns="3" styleClass="borderless-grid">
                        <h:panelGrid columns="2" styleClass="borderless-grid">
                            <h:outputText escape="true" value="Фильтр: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgSelectPage.filter}" size="48" maxlength="128"
                                         styleClass="input-text">
                                <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable" />
                            </h:inputText>
                            <h:outputText escape="true" value="Фильтр по ID: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgSelectPage.idFilter}" size="48" maxlength="128"
                                         styleClass="input-text">
                                <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable" />
                            </h:inputText>
                            <h:outputText escape="true" value="ID в НСИ-3: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgSelectPage.orgIdFromNsi}" size="48" maxlength="10"
                                         styleClass="input-text">
                                <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable" />
                            </h:inputText>
                            <h:outputText escape="true" value="ЕКИС Id: " styleClass="output-text" />
                            <h:inputText value="#{mainPage.orgSelectPage.ekisId}" size="48" maxlength="10"
                                         styleClass="input-text">
                                <a4j:support requestDelay="1000" event="onkeyup" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable" />
                            </h:inputText>
                            <h:outputText escape="true" value="Фильтр по округу: " styleClass="output-text" />
                            <h:selectOneMenu id="regionsList" value="#{mainPage.orgSelectPage.region}"
                                             style="width:325px;"
                                             disabled="#{mainPage.orgSelectPage.districtFilterDisabled}">
                                <f:selectItems value="#{mainPage.orgSelectPage.regions}" />
                                <a4j:support event="onchange" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable" />
                            </h:selectOneMenu>
                            <h:outputText escape="true" value="Поставщик питания: " styleClass="output-text" />
                            <h:selectOneMenu id="contragentList" disabled="#{mainPage.orgSelectPage.disableContragentFilter()}"
                                             value="#{mainPage.orgSelectPage.idOfSelectedContragent}"
                                             styleClass="output-text" style="width:325px;">
                                <f:selectItems value="#{mainPage.orgSelectPage.contragentsList}" />
                                <a4j:support event="onchange" action="#{mainPage.updateOrgSelectPage}"
                                             reRender="modalOrgSelectorOrgTable"/>
                            </h:selectOneMenu>
                        </h:panelGrid>
                    </h:panelGrid>
                    <rich:simpleTogglePanel label="Показать доп. фильтры" switchType="client" opened="false"
                                                                                         styleClass="borderNone" timeout="10"
                                                                                         headerClass="imageNone borderNone linkClass"
                                                                                         bodyClass="imageNone borderNone">
                    <h:panelGrid columns="2">
                        <a4j:repeat id="OrganizationTypesForSelectOne"
                                    value="#{mainPage.orgSelectPage.availableOrganizationTypes}" var="item">
                            <h:panelGrid columns="2">
                                <h:selectBooleanCheckbox value="#{item.selected}" disabled="#{item.disabled}">
                                    <a4j:support event="onclick"
                                                 action="#{mainPage.updateOrgSelectPageWithItemDeselection}"
                                                 reRender="modalOrgSelectorForm" requestDelay="1000"/>
                                </h:selectBooleanCheckbox>
                                <h:outputText styleClass="output-text" value="#{item.typeName}"/>
                            </h:panelGrid>
                        </a4j:repeat>
                    </h:panelGrid>
                </rich:simpleTogglePanel>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">
                    <rich:dataTable width="100%" align="center" id="modalOrgSelectorOrgTable"
                                    value="#{mainPage.orgSelectPage.items}" var="item" rows="8"
                                    footerClass="data-table-footer" columnClasses="left-aligned-column"
                                    rowClasses="select-panel-row" onRowMouseOver="this.style.backgroundColor='#e6e6e6'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <a4j:support event="onRowClick" reRender="modalOrgSelectorForm">
                            <f:setPropertyActionListener value="#{item}"
                                                         target="#{mainPage.orgSelectPage.selectedItem}" />
                        </a4j:support>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.idOfOrg}" styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.shortName} (#{item.officialName})"
                                          styleClass="output-text" />
                        </rich:column>
                        <rich:column headerClass="column-header">
                            <h:outputText escape="true" value="#{item.address}" styleClass="output-text" />
                        </rich:column>
                        <f:facet name="footer">
                            <rich:datascroller for="modalOrgSelectorOrgTable" renderIfSinglePage="false" maxPages="5"
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
                        <a4j:commandButton value="Ok" action="#{mainPage.completeOrgSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px; margin-right: 4px;" />
                        <a4j:commandButton value="Отмена" action="#{mainPage.cancelOrgSelection}"
                                           reRender="#{mainPage.topMostPage.pageComponent.id}"
                                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.hide();"
                                           styleClass="command-button" style="width: 80px;" />
                    </h:panelGroup>
                </td>
            </tr>
        </table>
    </a4j:form>
</rich:modalPanel>
