<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 24.12.12
  Time: 18:57
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель загрузки опросника из файла --%>
<%--@elvariable id="questionaryLoadPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryLoadPage"--%>
<%--@elvariable id="questionaryViewPage" type="ru.axetta.ecafe.processor.web.ui.org.questionaries.questionary.QuestionaryViewPage"--%>
<h:panelGrid id="questionaryFileLoaderPanel" binding="#{questionaryLoadPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid columns="2" styleClass="borderless-grid">

        <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />

        <h:panelGrid columns="2">
            <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;">
                <f:setPropertyActionListener value="#{questionaryLoadPage.idOfOrgList}" target="#{mainPage.orgFilterOfSelectOrgListSelectPage}"/>
            </a4j:commandButton>
            <h:outputText styleClass="output-text" id="categoryOrgEditFilter" escape="true" value=" {#{questionaryLoadPage.filter}}" />
        </h:panelGrid>

    </h:panelGrid>

    <rich:fileUpload id="questionaryFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
                     addButtonClassDisabled="upload-command-button-diasbled" cleanButtonClass="upload-command-button"
                     cleanButtonClassDisabled="upload-command-button-diasbled" stopButtonClass="upload-command-button"
                     stopButtonClassDisabled="upload-command-button-diasbled" uploadButtonClass="upload-command-button"
                     uploadButtonClassDisabled="upload-command-button-diasbled" fileEntryClass="output-text"
                     fileEntryClassDisabled="output-text" fileEntryControlClass="output-text"
                     fileEntryControlClassDisabled="output-text" sizeErrorLabel="Недопустимый размер"
                     stopControlLabel="Остановить" stopEntryControlLabel="Остановить" addControlLabel="Добавить файл"
                     clearControlLabel="Очистить" clearAllControlLabel="Очистить все" doneLabel="Готово"
                     cancelEntryControlLabel="Отменить" transferErrorLabel="Ошибка передачи"
                     uploadControlLabel="Загрузка файла" progressLabel="Загрузка" listHeight="70px"
                     fileUploadListener="#{questionaryLoadPage.questionaryLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="questionaryFileLoaderPanel" />
        <a4j:support event="onclear" reRender="questionaryFileLoaderPanel" />
    </rich:fileUpload>

    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable value="#{questionaryLoadPage.registrationItems}" var="item" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text"/>
            </f:facet>
            <h:outputText value="#{row+1}" styleClass="output-text"/>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Наименование" styleClass="output-text"/>
            </f:facet>
            <h:outputText value="#{item.questionaryItem}" styleClass="output-text" rendered="#{item.questionaryItem!=null}"/>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.questionary.question}"
                             action="#{questionaryViewPage.show}" styleClass="command-link" rendered="#{item.questionary!=null}">
                <f:setPropertyActionListener value="#{item.questionary}" target="#{questionaryGroupPage.questionary}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Результат" styleClass="output-text"/>
            </f:facet>
            <h:outputText value="#{item.result}" styleClass="output-text"/>
        </rich:column>
    </rich:dataTable>

</h:panelGrid>
