<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditPayment())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования начисленного платежа --%>
<h:panelGrid id="addPaymentEditPanel" binding="#{mainPage.addPaymentEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Плательщик" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentEditPage.contragentPayerName}" readonly="true"
                     styleClass="input-text" />
    <h:outputText escape="true" value="Получатель" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentEditPage.contragentReceiverName}" readonly="true"
                     styleClass="input-text" />
    <h:outputText escape="true" value="Сумма" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentEditPage.summa}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Комментарий" styleClass="output-text" />
    <h:inputText value="#{mainPage.addPaymentCreatePage.comment}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Период с" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <rich:calendar value="#{mainPage.addPaymentEditPage.fromDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
        <h:outputText escape="true" value=" по " styleClass="output-text" />
        <rich:calendar value="#{mainPage.addPaymentEditPage.toDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateAddPayment}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showAddPaymentEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>