<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="supportEmailGrid" binding="#{mainPage.supportEmailPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Кому" styleClass="output-text" />
        <h:inputText value="#{mainPage.supportEmailPage.address}" size="80" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Тема" styleClass="output-text" />
        <h:inputText value="#{mainPage.supportEmailPage.subject}" size="80" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Текст" styleClass="output-text" />
        <h:inputTextarea rows="15" cols="80" value="#{mainPage.supportEmailPage.text}" styleClass="input-text" />
    </h:panelGrid>
    <a4j:commandButton id="sendSupportEmailBtn" value="Отправить" action="#{mainPage.sendSupportEmail}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>