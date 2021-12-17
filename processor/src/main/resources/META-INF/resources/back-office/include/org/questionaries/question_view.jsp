<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 24.12.12
  Time: 18:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра выбранного опросника --%>
<%--@elvariable id="questionaryViewPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryViewPage"--%>
<%--@elvariable id="questionaryEditPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryEditPage"--%>
<h:panelGrid id="questionaryViewPanelGrid" binding="#{questionaryViewPage.pageComponent}" styleClass="borderless-grid" columns="2">
    <h:outputText value="Список организаций" styleClass="output-text"/>
    <h:panelGroup>
        <a4j:repeat var="org" value="#{questionaryViewPage.orgItemList}">
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{org.shortName}" styleClass="command-link"
                             action="#{mainPage.showOrgViewPage}">
                <f:setPropertyActionListener value="#{org.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
            <h:outputText value="; " styleClass="output-text"/>
        </a4j:repeat>
    </h:panelGroup>
    <h:outputText value="Идентификатор" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.idOfQuestionary}" styleClass="input-text"/>

    <h:outputText value="Наименование" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.questionName}" styleClass="input-text"/>

    <h:outputText value="Вопрос" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.question}" styleClass="input-text"/>

    <h:outputText value="Описание" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.description}" styleClass="input-text"/>

    <h:outputText value="Статус" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.status}" styleClass="input-text"/>

    <h:outputText value="Тип" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.questionaryType}" styleClass="input-text"/>

    <h:outputText value="Дата показа" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.viewDate}" converter="dateConverter" styleClass="input-text"/>

    <h:outputText value="Дата остановки" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.createdDate}" converter="timeConverter" styleClass="input-text"/>

    <h:outputText value="Дата последних изменений" styleClass="output-text"/>
    <h:inputText readonly="true" value="#{questionaryViewPage.questionary.updatedDate}" converter="timeConverter" styleClass="input-text"/>

    <a4j:commandButton value="Редактировать" reRender="mainMenu, workspaceForm"  action="#{questionaryEditPage.show}" styleClass="command-button"/>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
