<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--Тест лога --%>
<h:panelGrid id="testLogGrid" binding="#{mainPage.testLogPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Текст для логирования" styleClass="output-text" />
        <h:inputText value="#{mainPage.testLogPage.text}" size="128" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton value="Записать текст в лог с уровнем ERROR" action="#{mainPage.doLogTest}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>