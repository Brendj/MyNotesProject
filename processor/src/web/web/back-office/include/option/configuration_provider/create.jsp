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

<h:panelGrid styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Имя" styleClass="output-text" />
    <h:inputText value="#{mainPage.configurationProviderCreatePage.item.name}" styleClass="input-text"/>
</h:panelGrid>

<h:panelGrid columns="2" styleClass="borderless-grid">
    <a4j:commandButton value="Создать" action="#{mainPage.createConfigurationProvider}" reRender="mainMenu, workspaceTogglePanel"/>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>