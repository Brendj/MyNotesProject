<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 15.05.12
  Time: 21:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="configurationProviderCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderCreatePage"--%>
<h:panelGrid id="configurationProviderCreatePanelGrid" binding="#{configurationProviderCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
        <h:inputText value="#{configurationProviderCreatePage.currentConfigurationProvider.name}" maxlength="128" styleClass="input-text long-field" />

        <h:outputText escape="true" value="Количество выгружаемых дней с меню (больше текущей даты)" styleClass="output-text" />
        <h:inputText value="#{configurationProviderCreatePage.currentConfigurationProvider.menuSyncCountDays}" styleClass="input-text" />

        <h:outputText escape="true" value="Количество выгружаемых дней с меню (меньше текущей даты)" styleClass="output-text" />
        <h:inputText value="#{configurationProviderCreatePage.currentConfigurationProvider.menuSyncCountDaysInPast}" styleClass="input-text" />

        <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{configurationProviderCreatePage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryListFilter" escape="true" value=" {#{configurationProviderCreatePage.filter}}" />
        </h:panelGroup>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Создать" action="#{configurationProviderCreatePage.save}"
                           reRender="configurationProviderCreatePanelGrid" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>

