<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>


<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра карточки цифровой подписи --%>
<%--@elvariable id="cardSignViewPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignViewPage"--%>
<%--@elvariable id="cardSignEditPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignEditPage"--%>
<h:panelGrid binding="#{cardSignViewPage.pageComponent}">
    <h:panelGrid id="cardSignViewGrid" styleClass="borderless-grid" columns="2" rendered="#{cardSignViewPage.newProvider}">
        <h:outputText escape="true" value="Номер ключа" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.idOfCardSign}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Тип ключа подписи производителя" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.signTypeProvider}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Данные ключа производителя" styleClass="output-text" />
        <h:outputText value="#{cardSignViewPage.signDataSize}" styleClass="output-text" />
        <h:outputText escape="true" value="Тип ключа подписи карт" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.signTypeCard}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Код производителя" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.manufacturerCode}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Наименование производителя" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.manufacturerName}" styleClass="input-text" readonly="true" />
    </h:panelGrid>

    <h:panelGrid id="cardSignViewGrid_OLD" styleClass="borderless-grid" columns="2" rendered="#{!cardSignViewPage.newProvider}">
        <h:outputText escape="true" value="Номер ключа" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.idOfCardSign}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Тип ключа" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.signTypeCard}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Данные ключа" styleClass="output-text" />
        <h:outputText value="#{cardSignViewPage.signDataSize}" styleClass="output-text" />
        <h:outputText escape="true" value="Код производителя" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.manufacturerCode}" styleClass="input-text" readonly="true" />
        <h:outputText escape="true" value="Наименование производителя" styleClass="output-text" />
        <h:inputText value="#{cardSignViewPage.manufacturerName}" styleClass="input-text" readonly="true" />
    </h:panelGrid>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{cardSignEditPage.show}" styleClass="command-button"
                       reRender="mainMenu, workspaceForm" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>