<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditContragents()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%--@elvariable id="paymentStatsPage" type="ru.axetta.ecafe.processor.web.ui.contragent.PaymentStatsPage"--%>
<h:panelGrid id="paymentStatsPanel" binding="#{paymentStatsPage.pageComponent}" styleClass="borderless-grid">
    <h:outputText escape="true" value="Статистика платежей" styleClass="output-text-strong" />
    <rich:dataTable id="paymentStatsTable" value="#{paymentStatsPage.items}" var="item" rows="25"
                    rowKeyVar="row" footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="№" />
            </f:facet>
            <h:outputText escape="true" value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Канал пополнения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.contragentName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Способ пополнения" />
            </f:facet>
            <h:inputText value="#{item.way}" styleClass="output-text">
                <a4j:support event="onchange" />
            </h:inputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Количество платежей" />
            </f:facet>
            <h:inputText value="#{item.amountTotal}" styleClass="output-text">
                <a4j:support event="onchange" reRender="paymentStatsTable" />
            </h:inputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Количество неуспешных платежей" />
            </f:facet>
            <h:inputText value="#{item.amountNotSuccessful}" styleClass="output-text">
                <a4j:support event="onchange" reRender="paymentStatsTable"/>
            </h:inputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Процент успешных платежей" />
            </f:facet>
            <h:outputText escape="true" value="#{item.percentStr}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Расхождение во времени платежа и зачисления более 10 минут" />
            </f:facet>
            <h:inputText value="#{item.difference}" styleClass="output-text">
                <a4j:support event="onchange" />
            </h:inputText>
        </rich:column>
        <rich:column headerClass="column-header">
            <a4j:commandLink styleClass="command-link" reRender="paymentStatsTable" action="#{paymentStatsPage.deteteItem}">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.idOfItem}" target="#{paymentStatsPage.selectedIdOfItem}" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>

    <h:panelGrid id="paymentStatsButtonsPanel" styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Добавить"
                           action="#{mainPage.contragentPaymentReportPage.showContragentSelectPageOwn(false)}"
                           reRender="modalContragentListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalContragentListSelectorPanel')}.show();"
                           styleClass="command-link" >
            <f:setPropertyActionListener value="1" target="#{mainPage.contragentListSelectPage.classTypesString}" />
            <f:setPropertyActionListener value="#{mainPage.contragentPaymentReportPage.contragentPaymentReceiverIds}" target="#{mainPage.contragentListSelectPage.selectedIds}" />
        </a4j:commandButton>
        <h:commandButton value="Выгрузить в Excel" actionListener="#{paymentStatsPage.generateXLS}"
                         styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>
<a4j:status id="paymentStatsStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
    </f:facet>
</a4j:status>

<h:panelGrid id="paymentStatsMessages" styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>