<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="cardOperatorPage" type="ru.axetta.ecafe.processor.web.ui.cardoperator.CardOperatorPage"--%>
<h:panelGrid id="cardOperatorGrid" binding="#{cardOperatorPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable id="cardTable" value="#{cardOperatorPage.items}" var="item" rows="20"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер карты" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link"
                             reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.cardNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер, нанесённый на карту" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showCardViewPage}" styleClass="command-link"
                             reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.cardPrintedNo}" converter="cardNoConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfCard}" target="#{mainPage.selectedIdOfCard}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link"
                             reRender="mainMenu, workspaceForm">
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
            <a4j:commandLink action="#{mainPage.showCardEditPage}" styleClass="command-link"
                             reRender="mainMenu, workspaceForm"
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
</h:panelGrid>