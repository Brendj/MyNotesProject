<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditClients())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<h:panelGrid id="contractBuildPageGrid" binding="#{mainPage.contractBuildPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.contractBuildPage.org.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
        <h:outputText escape="true" value="Сгенерированный номер договора" styleClass="output-text" />
        <h:inputText id="generatedClientContractId" value="#{mainPage.contractBuildPage.contractId}"
                     converter="contractIdConverter" readonly="true" styleClass="input-text" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Генерировать" action="#{mainPage.generateClientContractNumber}"
                           styleClass="command-button" reRender="generatedClientContractId" />
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>