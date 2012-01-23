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

<%-- Панель редактирования платежа между контрагнетами --%>
<h:panelGrid id="settlementEditPanel" binding="#{mainPage.settlementEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Плательщик" styleClass="output-text" />
    <h:inputText value="#{mainPage.settlementEditPage.contragentPayer.contragentName}" readonly="true"
                     styleClass="input-text" />
    <h:outputText escape="true" value="Получатель" styleClass="output-text" />
    <h:inputText value="#{mainPage.settlementEditPage.contragentReceiver.contragentName}" readonly="true"
                     styleClass="input-text" />
    <h:outputText escape="true" value="Дата платежа" styleClass="output-text" />
    <rich:calendar value="#{mainPage.settlementEditPage.paymentDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Платежный документ" styleClass="output-text" />
    <h:inputText value="#{mainPage.settlementEditPage.paymentDoc}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Сумма" styleClass="output-text" />
    <h:inputText value="#{mainPage.settlementEditPage.summa}" maxlength="32" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateSettlement}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showSettlementEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>