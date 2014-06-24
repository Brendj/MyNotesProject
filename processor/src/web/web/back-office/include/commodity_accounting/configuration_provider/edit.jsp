<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 15.05.12
  Time: 22:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="configurationProviderEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderEditPage"--%>
<h:panelGrid id="configurationProviderCreatePanelGrid" binding="#{configurationProviderEditPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
        <h:inputText value="#{configurationProviderEditPage.currentConfigurationProvider.name}" maxlength="128" styleClass="input-text long-field" />

        <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />

        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" >
                <f:setPropertyActionListener value="#{configurationProviderEditPage.getStringIdOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="configurationProviderFilter" escape="true" value=" {#{configurationProviderEditPage.filter}}" />
        </h:panelGroup>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{configurationProviderEditPage.save}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

        <a4j:commandButton value="Востановить" action="#{configurationProviderEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>


</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
