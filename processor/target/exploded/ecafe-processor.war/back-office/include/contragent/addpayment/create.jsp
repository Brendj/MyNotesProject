<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditPayment())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания начисленного платежа --%>
<h:panelGrid id="addPaymentCreatePanel" binding="#{mainPage.addPaymentCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Плательщик" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.addPaymentCreatePage.contragentPayer.contragentName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showContragentSelectPage}"
                           reRender="modalContragentSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="0"
                                             target="#{mainPage.multiContrFlag}" />
                <f:setPropertyActionListener value="2"
                                             target="#{mainPage.classTypes}" />
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Получатель" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentCreatePage.operatorContragent.contragentName}" readonly="true"
                     styleClass="input-text" />
    <h:outputText escape="true" value="Сумма" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentCreatePage.summa}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Комментарий" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentCreatePage.comment}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Период с" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <rich:calendar value="#{mainPage.addPaymentCreatePage.fromDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value=" по " styleClass="output-text" />
        <rich:calendar value="#{mainPage.addPaymentCreatePage.toDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать платеж" action="#{mainPage.createAddPayment}"
                       reRender="addPaymentCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>