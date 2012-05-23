<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 15.05.12
  Time: 22:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификаторв" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.configurationProviderViewPage.item.idOfConfigurationProvider}" styleClass="input-text"
                     style="width: 200px;" />

    <h:outputText escape="true" value="Имя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{mainPage.configurationProviderViewPage.item.name}" styleClass="input-text"/>


    <a4j:commandButton value="Редактировать" action="#{mainPage.showConfigurationProviderEditPage}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>