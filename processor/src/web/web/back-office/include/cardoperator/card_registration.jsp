<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="cardRegistrationAndIssueGrid" binding="#{mainPage.cardRegistrationAndIssuePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.client.shortName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Номер карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.cardNo}" converter="cardNoConverter" maxlength="10"
                 styleClass="input-text" />
    <h:outputText escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.cardPrintedNo}" converter="cardPrintedNoConverter"
                 maxlength="16" styleClass="input-text" />
    <h:outputText escape="true" value="Тип карты" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.cardRegistrationAndIssuePage.cardType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cardRegistrationAndIssuePage.cardTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата выдачи" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardRegistrationAndIssuePage.issueTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Последний день действия" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardRegistrationAndIssuePage.validTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать новую карту" action="#{mainPage.createCard}"
                       styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>