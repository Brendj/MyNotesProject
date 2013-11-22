<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра данных карты --%>
<h:panelGrid id="cardViewGrid" binding="#{mainPage.cardViewPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <a4j:commandLink value="#{mainPage.cardViewPage.client.shortName}" styleClass="command-link"
                   action="#{mainPage.showClientViewPage}" reRender="mainMenu, workspaceForm">
        <f:setPropertyActionListener value="#{mainPage.cardViewPage.client.idOfClient}"
                                     target="#{mainPage.selectedIdOfClient}" />
    </a4j:commandLink>
    <h:outputText escape="true" value="Номер карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.cardNo}" converter="cardNoConverter" readonly="true"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.cardPrintedNo}" converter="cardPrintedNoConverter" readonly="true"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Внешний идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.externalId}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Тип карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.cardType}" readonly="true" converter="cardTypeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Дата создания карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.createTime}" readonly="true" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Дата последнего обновления" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.updateTime}" readonly="true" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Дата выдачи" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.issueTime}" readonly="true" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Последний день действия" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.validTime}" readonly="true" converter="timeConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Статус карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.state}" readonly="true" converter="cardStateConverter"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Причина блокировки карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.lockReason}" readonly="true" styleClass="input-text" />
    <h:outputText escape="true" value="Статус расположения карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardViewPage.lifeState}" readonly="true" converter="cardLifeStateConverter"
                 styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showCardEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>