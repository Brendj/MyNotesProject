<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToProcessPayment()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%--@elvariable id="clientRefundPage" type="ru.axetta.ecafe.processor.web.ui.client.ClientRefundPage"--%>
<h:panelGrid id="clientBalanceRefundPanel" binding="#{clientRefundPage.pageComponent}" styleClass="borderless-grid">

<a4j:status>
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
    </f:facet>
</a4j:status>

<rich:simpleTogglePanel label="Возврат средств клиенту" switchType="client" opened="true"
                        headerClass="filter-panel-header">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Клиент" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{clientRefundPage.clientName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <h:outputText escape="true" value="Л/с" styleClass="output-text" />
            <h:inputText value="#{clientRefundPage.clientContractId}" readonly="true" styleClass="input-text" converter="contractIdConverter"
                         style="margin-right: 2px;" />
            <h:outputText escape="true" value="Баланс" styleClass="output-text" />
            <h:inputText value="#{clientRefundPage.clientBalance}" readonly="true" styleClass="input-text" converter="copeckSumConverter"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showClientSelectPage}" reRender="modalClientSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalClientSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
            </a4j:commandButton>
        </h:panelGroup>
        <h:outputText escape="true" value="Сумма (руб.)" styleClass="output-text" />
        <h:inputText value="#{clientRefundPage.sum}" maxlength="20" size="20" styleClass="input-text" converter="copeckSumConverter"/>
        <h:outputText escape="true" value="Причина" styleClass="output-text" />
        <h:inputText value="#{clientRefundPage.reason}" maxlength="256" size="40" styleClass="input-text" />
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Провести" action="#{clientRefundPage.registerRefund}" reRender="workspaceTogglePanel"
                           styleClass="command-button" />

    </h:panelGrid>
</rich:simpleTogglePanel>


<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>

</h:panelGrid>
