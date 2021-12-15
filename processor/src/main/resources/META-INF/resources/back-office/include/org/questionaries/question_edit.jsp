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
<%--@elvariable id="questionaryEditPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryEditPage"--%>

<h:panelGrid id="questionaryEditPanelGrid" binding="#{questionaryEditPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid id = "questionaryEditPanel" styleClass="borderless-grid" columns="2">
        <h:outputText value="Идентификатор" styleClass="output-text"/>
        <h:inputText value="#{questionaryEditPage.questionary.idOfQuestionary}" styleClass="output-text" readonly="true"/>

        <h:outputText value="Наименование" styleClass="output-text long-field required-field"/>
        <%--<h:inputText value="#{questionaryEditPage.questionName}" styleClass="output-text"/>--%>
        <h:inputText value="#{questionaryEditPage.questionary.questionName}" styleClass="output-text"/>

        <h:outputText value="Вопрос" styleClass="output-text long-field required-field"/>
        <%--<h:inputText value="#{questionaryEditPage.question}" styleClass="output-text"/>--%>
        <h:inputText value="#{questionaryEditPage.questionary.question}" styleClass="output-text"/>

        <h:outputText value="Описание" styleClass="output-text long-field"/>
        <%--<h:inputTextarea value="#{questionaryEditPage.description}" styleClass="output-text"/>--%>
        <h:inputText value="#{questionaryEditPage.questionary.description}" styleClass="output-text"/>

        <h:outputText escape="true" value="Организации" styleClass="output-text" />
        <h:panelGrid columns="2">
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{questionaryEditPage.idOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText value="{#{questionaryEditPage.filter}}" styleClass="output-text"/>
        </h:panelGrid>

        <%--<h:outputText value="Статус" styleClass="output-text"/>
        <h:inputText value="#{questionaryEditPage.questionary.status}" styleClass="output-text"/>--%>
        <h:outputText escape="true" value="Тип" styleClass="output-text" />
        <h:selectOneMenu value="#{questionaryEditPage.questionary.type}" styleClass="input-text">
            <f:selectItems value="#{questionaryEditPage.questionaryEnumTypeMenu.items}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Дата показа" styleClass="output-text" />
        <rich:calendar value="#{questionaryEditPage.questionary.viewDate}" datePattern="dd.MM.yyyy" converter="dateConverter" inputClass="input-text" showWeeksBar="false" />

        <h:outputText value="Дата регистрации" styleClass="output-text"/>
        <h:inputText value="#{questionaryEditPage.questionary.createdDate}" converter="dateConverter" styleClass="output-text" readonly="true"/>
        <h:outputText value="Дата последних изменений" styleClass="output-text"/>
        <h:inputText value="#{questionaryEditPage.questionary.updatedDate}" converter="dateConverter" styleClass="output-text" readonly="true"/>
    </h:panelGrid>
    <rich:dataTable value="#{questionaryEditPage.questionary.answerList}" var="ans" id="answerTable" rowKeyVar="row"
            columnClasses="right-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№"/>
            </f:facet>
            <h:outputText value="#{row+1}"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Вариант ответа"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{ans.answer}"
                               id="answerText" required="true"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">
                <a4j:support event="onviewactivated" reRender="answerTable"/>
            </rich:inplaceInput>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Пояснения"/>
            </f:facet>
            <rich:inplaceInput layout="block" value="#{ans.description}"
                               id="answerDescription"
                               changedHoverClass="hover" viewHoverClass="hover"
                               viewClass="inplace" changedClass="inplace"
                               selectOnEdit="true" editEvent="ondblclick">
                <a4j:support event="onviewactivated" reRender="answerTable"/>
            </rich:inplaceInput>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Балл"/>
            </f:facet>
            <rich:inputNumberSpinner value="#{ans.weight}" id="answerWeightSpinner"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Удалить"/>
            </f:facet>
            <a4j:commandLink action="#{questionaryEditPage.removeAnswer}" reRender="answerTable">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{ans}" target="#{questionaryEditPage.removeAnswer}"/>
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>
    <h:panelGrid columns="2">
        <a4j:commandButton action="#{questionaryEditPage.save}" value="Сохранить" reRender="mainMenu, workspaceTogglePanel"/>
        <a4j:commandButton action="#{questionaryEditPage.reload}" value="Востановить" reRender="mainMenu, workspaceTogglePanel"/>
    </h:panelGrid>
</h:panelGrid>

<a4j:status id="sQuestionaryEditStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>