<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="optionPanelGrid" binding="#{optionPage.pageComponent}" styleClass="borderless-grid">
   <rich:tabPanel>
        <rich:tab label="Общие">
            <h:panelGrid styleClass="borderless-grid" columns="2">
                <h:outputText escape="true" value="Использовать схему с оператором" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.withOperator}" styleClass="output-text" />
                <h:outputText escape="true" value="Отпралять СМС-уведомление о событиях входа-выхода" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.notifyBySMSAboutEnterEvent}" styleClass="output-text" />
            </h:panelGrid>
            <h:panelGroup styleClass="borderless-grid">
                <h:outputText escape="true" value="Удалять записи меню в базе" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.cleanMenu}" styleClass="output-text" />
                <h:outputText escape="true" value="Хранить дней от текущей даты " styleClass="output-text" />
                <h:inputText value="#{optionPage.menuDaysForDeletion}" styleClass="input-text" size="3"/>
            </h:panelGroup>
        </rich:tab>
        <rich:tab label="Взаимодействие">
            <h:panelGroup styleClass="borderless-grid">
                <h:outputText escape="true" value="Журналировать и отправлять транзакции в ИСНП" styleClass="output-text" />
                <h:selectBooleanCheckbox value="#{optionPage.journalTransactions}" styleClass="output-text" />
            </h:panelGroup>
        </rich:tab>
    </rich:tabPanel>

    <h:panelGroup style="margin-top: 10px">
        <a4j:commandButton value="Сохранить" action="#{optionPage.save}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                           styleClass="command-button" />
        <a4j:commandButton value="Отмена" action="#{optionPage.cancel}"
                           reRender="mainMenu, workspaceTogglePanel, optionPanelGrid"
                           styleClass="command-button" />
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>