<%@ page import="ru.axetta.ecafe.processor.web.ui.MainPage" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!MainPage.getSessionInstance().isEligibleToEditCards()) { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования данных карты --%>
<h:panelGrid id="cardEditGrid" binding="#{mainPage.cardEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.cardEditPage.client.shortName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Номер карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardEditPage.cardNo}" converter="cardNoConverter" readonly="true" maxlength="18"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Длинный номер карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardEditPage.longCardNo}" converter="cardNoConverter" readonly="true" maxlength="18"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardEditPage.cardPrintedNo}" converter="cardPrintedNoConverter" readonly="true" maxlength="18"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Внешний идентификатор" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardEditPage.externalId}" styleClass="input-text" />
    <h:outputText escape="true" value="Тип карты" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.cardEditPage.cardType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cardEditPage.cardTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата выдачи" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardEditPage.issueTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Последний день действия" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardEditPage.validTime}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Статус карты" styleClass="output-text" />
    <h:panelGroup>
        <h:selectOneMenu value="#{mainPage.cardEditPage.state}"
                         rendered="#{!mainPage.cardEditPage.tempCard}"
                         styleClass="input-text">
            <f:selectItems value="#{mainPage.cardEditPage.cardStateMenu.items}" />
        </h:selectOneMenu>
        <a4j:commandButton value="Возврат карты"
                           reRender="mainMenu, workspaceForm"
                           action="#{mainPage.cardEditPage.returnCard(request.remoteUser)}"
                           rendered="#{mainPage.cardEditPage.tempCard}"/>
    </h:panelGroup>
    <h:outputText escape="true" value="Причина блокировки карты" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.cardEditPage.lockReasonState}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cardEditPage.cardLockReasonMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Статус расположения карты" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.cardEditPage.lifeState}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cardEditPage.cardLifeStateMenu.items}" />
    </h:selectOneMenu>
</h:panelGrid>
<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateCard}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showCardEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>