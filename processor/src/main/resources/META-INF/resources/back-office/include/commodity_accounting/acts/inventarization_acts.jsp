<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 18.12.12
  Time: 16:33
  Список Актов инвентаризации
--%>
<%--@elvariable id="actOfInventorizationListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.act.ActOfInventorizationListPage"--%>
<h:panelGrid id="actOfInventarizationListPage" binding="#{actOfInventorizationListPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid id="actOfInventarizationListPageFilter" styleClass="borderless-grid">
        <rich:simpleTogglePanel label="Фильтр (#{actOfInventorizationListPage.filter.status})" switchType="client" opened="true"
                                headerClass="filter-panel-header">

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <h:outputText escape="true" value="Организация" styleClass="output-text" />
                <h:panelGroup styleClass="borderless-div">
                    <h:inputText value="#{actOfInventorizationListPage.shortName}" readonly="true" styleClass="input-text long-field"
                                 style="margin-right: 2px;" />
                    <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                       oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                       styleClass="command-link" style="width: 25px;" />
                </h:panelGroup>
                <h:outputText escape="true" value="Комиссия" styleClass="output-text" />
                <h:inputText value="#{actOfInventorizationListPage.filter.commission}" styleClass="input-text" />
                <h:outputText escape="true" value="Номер документа" styleClass="output-text" />
                <h:inputText value="#{actOfInventorizationListPage.filter.number}" styleClass="input-text" />
                <h:outputText escape="true" value="Удаленные акты" styleClass="output-text" />
                <h:selectOneMenu id="selectDeletedStatus" value="#{actOfInventorizationListPage.filter.deletedState}" styleClass="input-text">
                    <f:selectItem itemLabel="Скрыть" itemValue="true"/>
                    <f:selectItem itemLabel="Показать" itemValue="false"/>
                </h:selectOneMenu>
                <h:outputText escape="true" value="Дата выборки от" styleClass="output-text" />
                <rich:calendar value="#{actOfInventorizationListPage.filter.startDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
                <h:outputText escape="true" value="Дата выборки до" styleClass="output-text" />
                <rich:calendar value="#{actOfInventorizationListPage.filter.endDate}" datePattern="dd.MM.yyyy"
                               converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
            </h:panelGrid>

            <h:panelGrid columns="2" styleClass="borderless-grid">
                <a4j:commandButton value="Применить" action="#{actOfInventorizationListPage.reload}"
                                   reRender="workspaceTogglePanel" styleClass="command-button" />
                <a4j:commandButton value="Очистить" action="#{actOfInventorizationListPage.resetFilter}"
                                   reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
            </h:panelGrid>
        </rich:simpleTogglePanel>
    </h:panelGrid>

    <a4j:status id="actOfInventarizationListTableStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="actOfInventarizationListPageTable" value="#{actOfInventorizationListPage.itemList}" var="act"
                    rowKeyVar="row" rows="10" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Комиссия" />
            </f:facet>
            <h:outputText escape="true" value="#{act.commission}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер" />
            </f:facet>
            <h:outputText escape="true" value="#{act.number}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата регистрации акта" />
            </f:facet>
            <h:outputText escape="true" value="#{act.dateOfAct}" styleClass="output-text" converter="timeConverter"/>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Организация владелец" />
            </f:facet>
            <h:outputText escape="true" value="#{act.orgOwner.shortName}" styleClass="output-text" />
        </rich:column>
        <%--<rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Версия" />
            </f:facet>
            <h:outputText escape="true" value="#{act.version}" styleClass="output-text" />
        </rich:column>--%>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{act.deletedState}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="actOfInventarizationListPageTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
