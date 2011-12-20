<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="optionPanelGrid" binding="#{mainPage.optionPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Использовать схему с оператором" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.optionPage.withOperator}" styleClass="output-text" />
        <h:outputText escape="true" value="Отпралять СМС-уведомление о событиях входа-выхода" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.optionPage.notifyBySMSAboutEnterEvent}" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="Сохранить" action="#{mainPage.saveOption}"
                               reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                               styleClass="command-button" />
            <a4j:commandButton value="Отмена" action="#{mainPage.optionPage.cancelOption}"
                               reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                               styleClass="command-button" />
        </h:panelGroup>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>