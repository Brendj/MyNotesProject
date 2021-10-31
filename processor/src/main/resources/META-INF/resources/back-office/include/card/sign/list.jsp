<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка сотрудников --%>
<%--@elvariable id="cardSignGroupPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignGroupPage"--%>
<%--@elvariable id="cardSignListPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignListPage"--%>
<%--@elvariable id="cardSignViewPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignViewPage"--%>
<%--@elvariable id="cardSignEditPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignEditPage"--%>
<h:panelGrid id="cardSignListGrid" binding="#{cardSignListPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable id="cardSignTable" value="#{cardSignListPage.cards}" var="card" rows="15"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер ключа" />
            </f:facet>
            <h:outputText escape="true" value="#{card.idOfCardSign}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код производителя" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{cardSignViewPage.show}" styleClass="command-link">
                <h:outputText escape="true" value="#{card.manufacturerCode}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{card}" target="#{cardSignGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование производителя" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{cardSignViewPage.show}" styleClass="command-link">
                <h:outputText escape="true" value="#{card.manufacturerName}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{card}" target="#{cardSignGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Тип регистрации карт" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceTogglePanel" action="#{cardSignViewPage.show}" styleClass="command-link">
                <h:outputText escape="true" value="#{card.getProviderType()}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{card}" target="#{cardSignGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column styleClass="center-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактирование" />
            </f:facet>
            <a4j:commandLink reRender="workspaceForm" action="#{cardSignEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{card}" target="#{cardSignGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <rich:column styleClass="center-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Удаление" />
            </f:facet>
            <a4j:commandLink reRender="workspaceTogglePanel" action="#{cardSignListPage.delete}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{card}" target="#{cardSignGroupPage.currentCard}"/>
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="cardSignTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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