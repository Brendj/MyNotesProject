<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>


<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра карточки цифровой подписи --%>
<%--@elvariable id="cardSignEditPage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignEditPage"--%>
<%--@elvariable id="cardSignCreatePage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignCreatePage"--%>
<h:panelGrid id="cardSignEditGrid" binding="#{cardSignEditPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <h:outputText escape="true" value="Номер ключа" styleClass="output-text" />
    <h:inputText value="#{cardSignEditPage.idOfCardSign}" styleClass="input-text" readonly="true"/>
    <h:outputText escape="true" value="Тип ключа" styleClass="output-text" />
    <h:selectOneMenu value="#{cardSignEditPage.signType}" styleClass="input-text">
        <f:selectItems value="#{cardSignCreatePage.types}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Данные ключа" styleClass="output-text" />
    <h:outputText value="#{cardSignEditPage.signDataSize}" styleClass="output-text" />
    <h:outputText value="Загрузка нового файла с данными ключа" styleClass="output-text" />
    <rich:fileUpload id="cardSignFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{cardSignEditPage.fileUploadListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="cardSignCreateGrid" />
        <a4j:support event="onclear" reRender="cardSignCreateGrid" />
    </rich:fileUpload>
    <h:outputText escape="true" value="Код производителя" styleClass="output-text" />
    <h:inputText value="#{cardSignEditPage.manufacturerCode}" styleClass="input-text" />
    <h:outputText escape="true" value="Наименование производителя" styleClass="output-text" />
    <h:inputText value="#{cardSignEditPage.manufacturerName}" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{cardSignEditPage.save}"
                       styleClass="command-button" reRender="mainMenu, workspaceForm" />
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>