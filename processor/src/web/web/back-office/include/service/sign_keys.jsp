<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="signKeysBuiderGrid" binding="#{mainPage.buildSignKeysPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Публичный ключ" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.buildSignKeysPage.publicSignKey}" size="64"
                     styleClass="input-text" />
        <h:outputText escape="true" value="Закрытый ключ" styleClass="output-text" />
        <h:inputText readonly="true" value="#{mainPage.buildSignKeysPage.privateSignKey}" size="64"
                     styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Генерировать пару ключей для ЭЦП" action="#{mainPage.buildSignKeys}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>