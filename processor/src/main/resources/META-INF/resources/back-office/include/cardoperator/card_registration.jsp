<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<script>
    var socket = new WebSocket("ws://localhost:8001");
    socket.onmessage = function(event) {
        var incomingMessage = event.data;
        document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:cardNoHidden").value = incomingMessage;
        var button = document.getElementById("workspaceSubView:workspaceForm:workspacePageSubView:setCardNoByHidden");
        if (button != null) {
            button.click();
        }
    };
</script>


<h:panelGrid id="cardRegistrationAndIssueGrid" binding="#{mainPage.cardRegistrationAndIssuePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.cardNoHidden}" converter="cardNoConverter" maxlength="10" id="cardNoHidden"
                 style="display: none;" />
    <a4j:commandButton id="setCardNoByHidden" action="#{mainPage.cardRegistrationAndIssuePage.onCardRead}"  reRender="cardNo"
                       style="display: none;"/>
    <h:outputText escape="true" value="Клиент" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <a4j:commandButton value="..." action="#{mainPage.showEmptyClientSelectPage}" reRender="modalClientSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();disableButtons(false);"
                           styleClass="command-link" style="width: 25px;"
                           id="clientButtonCardShow" onclick="disableButtons(true);"/>
        <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.client.shortNameContractId}" readonly="true"
                     styleClass="input-text long-field" style="margin-right: 2px;" />
    </h:panelGroup>
    <h:outputText escape="true" value="Номер карты" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.cardNo}" converter="cardNoConverter" maxlength="10" id="cardNo"
                 styleClass="input-text" readonly="true" />
    <h:outputText escape="true" value="Номер, нанесенный на карту" styleClass="output-text" />
    <h:inputText value="#{mainPage.cardRegistrationAndIssuePage.cardPrintedNo}" converter="cardPrintedNoConverter"
                 maxlength="16" styleClass="input-text" />
    <h:outputText escape="true" value="Тип карты" styleClass="output-text" />
    <h:selectOneMenu value="#{mainPage.cardRegistrationAndIssuePage.cardType}" styleClass="input-text">
        <f:selectItems value="#{mainPage.cardRegistrationAndIssuePage.cardTypeMenu.itemsCardOperator}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Дата выдачи" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardRegistrationAndIssuePage.issueTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Последний день действия" styleClass="output-text" />
    <rich:calendar value="#{mainPage.cardRegistrationAndIssuePage.validTime}" datePattern="dd.MM.yyyy"
                   converter="dateConverter" inputClass="input-text" showWeeksBar="false" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать новую карту" action="#{mainPage.verificationCardData}"
                       oncomplete="if (#{facesContext.maximumSeverity == null})#{rich:component('cardRegistrationConfirm')}.show();"
                       reRender="cardRegistrationConfirm"
                       styleClass="command-button">
    </a4j:commandButton>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>