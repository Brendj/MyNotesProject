<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 24.12.12
  Time: 17:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<%-- Панель просмотра списка опросников --%>
<%--@elvariable id="questionaryListPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryListPage"--%>
<%--@elvariable id="questionaryViewPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryViewPage"--%>
<%--@elvariable id="questionaryEditPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryEditPage"--%>
<%--@elvariable id="questionaryGroupPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryGroupPage"--%>
<h:panelGrid id="questionaryListPanelGrid" binding="#{questionaryListPage.pageComponent}" styleClass="borderless-grid">

    <rich:dataTable value="#{questionaryListPage.questionary}" var="questionary"
                    captionClass="center-aligned-column"
                    columnClasses="right-aligned-column, center-aligned-column, center-aligned-column,
                                   center-aligned-column, center-aligned-column, center-aligned-column,
                                   center-aligned-column, center-aligned-column, center-aligned-column">
        <f:facet name="header">
             <rich:columnGroup>
                 <rich:column  sortBy="#{questionary.idOfQuestionary}">
                     <h:outputText value="Идентификатор"/>
                 </rich:column>
                 <rich:column  sortBy="#{questionary.question}">
                     <h:outputText value="Наименование"/>
                 </rich:column>
                 <rich:column  sortBy="#{questionary.question}">
                     <h:outputText value="Вопрос"/>
                 </rich:column>
                 <rich:column >
                     <h:outputText value="Тип"/>
                 </rich:column>
                 <rich:column  sortBy="#{questionary.createdDate}">
                     <h:outputText value="Дата регистрации"/>
                 </rich:column>
                 <rich:column  sortBy="#{questionary.status}">
                     <h:outputText value="Запуск"/>
                 </rich:column>
                 <rich:column>
                     <h:outputText value="Дни работы"/>
                 </rich:column>
                 <rich:column >
                     <h:outputText value="Редактировать"/>
                 </rich:column>
                 <rich:column >
                     <h:outputText value="Удалить"/>
                 </rich:column>
             </rich:columnGroup>
        </f:facet>
        <rich:column>
            <h:outputText value="#{questionary.idOfQuestionary}"/>
        </rich:column>
        <rich:column>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{questionary.questionName}" action="#{questionaryViewPage.show}" styleClass="command-link">
                <f:setPropertyActionListener value="#{questionary}" target="#{questionaryGroupPage.questionary}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column>
            <h:outputText value="#{questionary.question}"/>
        </rich:column>
        <rich:column>
            <h:outputText value="#{questionary.questionaryType}"/>
        </rich:column>
        <rich:column>
            <h:outputText value="#{questionary.createdDate}" converter="timeConverter"/>
        </rich:column>
        <rich:column>
            <a4j:commandLink action="#{questionaryListPage.start}" rendered="#{questionary.inactiveStatus}" reRender="mainMenu, workspaceForm">
                <f:param name="id" value="#{questionary.idOfQuestionary}"/>
                <h:graphicImage value="/images/16x16/play.png" style="border: 0;" />
            </a4j:commandLink>
            <a4j:commandLink action="#{questionaryListPage.stop}" rendered="#{questionary.startStatus}" reRender="mainMenu, workspaceForm">
                <f:param name="id" value="#{questionary.idOfQuestionary}"/>
                <h:graphicImage value="/images/16x16/stop.png" style="border: 0;" />
            </a4j:commandLink>
        </rich:column>

        <rich:column>
            <h:outputText value="#{questionary.viewDate}" converter="dateConverter"/>
        </rich:column>

        <rich:column>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{questionaryEditPage.show}">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{questionary}" target="#{questionaryGroupPage.questionary}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{questionaryListPage.remove}"
                             rendered="#{!questionary.startStatus}">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:param name="id" value="#{questionary.idOfQuestionary}"/>
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>
</h:panelGrid>

<a4j:status id="sQuestionaryListStatus">
    <f:facet name="start">
        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
    </f:facet>
</a4j:status>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
