<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="configurationPanelGrid" binding="#{mainPage.configurationPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="1">
        <h:inputTextarea rows="40" cols="140" value="#{mainPage.configurationPage.configurationText}" styleClass="input-text" />
        <h:panelGroup>
            <a4j:commandButton value="Сохранить" action="#{mainPage.saveConfiguration}"
                               reRender="workspaceTogglePanel, configurationPanelGrid"
                               styleClass="command-button" />
            <a4j:commandButton value="Отмена" action="#{mainPage.configurationPage.cancelConfiguration}"
                               reRender="workspaceTogglePanel, configurationPanelGrid"
                               styleClass="command-button" />
        </h:panelGroup>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>