<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка карт --%>
<h:panelGrid id="cardListGrid" binding="#{mainPage.cardListPage.pageComponent}" styleClass="borderless-grid">
    <rich:simpleTogglePanel id="cardListFilterPanel" label="Фильтр (#{mainPage.cardListPage.cardFilter.status})"
                            switchType="client" eventsQueue="mainFormEventsQueue" opened="false"
                            headerClass="filter-panel-header">

        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Организация" styleClass="output-text" />
            <h:panelGroup styleClass="borderless-div">
                <h:inputText value="#{mainPage.cardListPage.cardFilter.org.shortName}" readonly="true"
                             styleClass="input-text" style="width: 240px; margin-right: 2px;" />
                <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            </h:panelGroup>

            <h:outputText escape="true" value="Номер карты" styleClass="output-text" />
            <h:inputText value="#{mainPage.cardListPage.cardFilter.cardNo}" converter="cardNoConverter" maxlength="18"
                         style="width: 240px;" styleClass="input-text" />

            <h:outputText escape="true" value="Номер, нанесённый на карту" styleClass="output-text" />
            <h:inputText value="#{mainPage.cardListPage.cardFilter.cardPrintedNo}" converter="cardPrintedNoConverter" maxlength="18"
                         style="width: 240px;" styleClass="input-text"/>

            <h:outputText escape="true" value="Cтатус" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.cardListPage.cardFilter.cardState}" styleClass="input-text"
                             style="width: 240px;">
                <f:selectItems value="#{mainPage.cardListPage.cardFilter.cardStateFilterMenu.items}" />
            </h:selectOneMenu>

            <h:outputText escape="true" value="Cтатус расположения" styleClass="output-text" />
            <h:selectOneMenu value="#{mainPage.cardListPage.cardFilter.cardLifeState}" styleClass="input-text"
                             style="width: 240px;">
                <f:selectItems value="#{mainPage.cardListPage.cardFilter.cardLifeStateFilterMenu.items}" />
            </h:selectOneMenu>

        </h:panelGrid>

        <h:panelGrid columns="2" styleClass="borderless-grid">
            <a4j:commandButton value="Применить" action="#{mainPage.updateCardListPage}" reRender="workspaceTogglePanel"
                               styleClass="command-button" />
            <a4j:commandButton value="Очистить" action="#{mainPage.clearCardListPageFilter}"
                               reRender="workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
        </h:panelGrid>

    </rich:simpleTogglePanel>

    <a4j:status id="cardTableGenerateStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>

    <rich:dataTable id="cardTable" value="#{mainPage.cardListPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер, нанесённый на карту" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.cardPrintedNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.client.shortName}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.client.idOfClient}"
                                             target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус" />
            </f:facet>
            <h:outputText escape="true" value="#{item.state}" converter="cardStateConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Статус расположения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lifeState}" converter="cardLifeStateConverter"
                          styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Последние изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <%--@elvariable id="runtimeContext" type="ru.axetta.ecafe.processor.core.RuntimeContext"--%>
            <a4j:commandLink action="#{mainPage.showCardEditPage}" styleClass="command-link" reRender="mainMenu, workspaceForm"
                    disabled="#{runtimeContext.settingsConfig.cardsEditDisabled}">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showCardCSVList}" styleClass="command-button" />
</h:panelGrid>