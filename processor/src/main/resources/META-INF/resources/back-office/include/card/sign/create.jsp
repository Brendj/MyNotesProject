<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCards())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель регистрации цифровой подписи --%>
<%--@elvariable id="cardSignCreatePage" type="ru.axetta.ecafe.processor.web.ui.card.sign.CardSignCreatePage"--%>
<h:panelGrid id="cartSign"  binding="#{cardSignCreatePage.pageComponent}">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Тип регистрируемого поставщика" styleClass="output-text" />
        <h:selectOneMenu value="#{cardSignCreatePage.newProvider}" styleClass="input-text">
            <f:selectItems value="#{cardSignCreatePage.getTypesRegister()}" />
            <a4j:support event="onchange" reRender="cartSign" ajaxSingle="true"/>
        </h:selectOneMenu>
    </h:panelGrid>
    <h:panelGroup rendered="#{cardSignCreatePage.newProvider}">
        <h:panelGrid id="cardSignCreateGrid" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Тип ключа подписи производителя" styleClass="output-text" />
            <h:selectOneMenu value="#{cardSignCreatePage.signTypeProvider}" styleClass="input-text">
                <f:selectItems value="#{cardSignCreatePage.getTypes(2)}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Данные ключа производителя" styleClass="output-text" />
            <h:outputText value="#{cardSignCreatePage.signDataSize}" styleClass="output-text" />
            <h:outputText value="Загрузка файла с данными ключа производителя" styleClass="output-text" />
            <rich:fileUpload id="cardSignFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
                             addButtonClassDisabled="upload-command-button-diasbled"
                             cleanButtonClass="upload-command-button"
                             cleanButtonClassDisabled="upload-command-button-diasbled"
                             stopButtonClass="upload-command-button"
                             stopButtonClassDisabled="upload-command-button-diasbled"
                             uploadButtonClass="upload-command-button"
                             uploadButtonClassDisabled="upload-command-button-diasbled" fileEntryClass="output-text"
                             fileEntryClassDisabled="output-text" fileEntryControlClass="output-text"
                             fileEntryControlClassDisabled="output-text" sizeErrorLabel="Недопустимый размер"
                             stopControlLabel="Остановить" stopEntryControlLabel="Остановить"
                             addControlLabel="Добавить файл" clearControlLabel="Очистить"
                             clearAllControlLabel="Очистить все" doneLabel="Готово" cancelEntryControlLabel="Отменить"
                             transferErrorLabel="Ошибка передачи" uploadControlLabel="Загрузка файла"
                             progressLabel="Загрузка" listHeight="70px"
                             fileUploadListener="#{cardSignCreatePage.fileUploadListener}">
                <f:facet name="label">
                    <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
                </f:facet>
                <a4j:support event="onuploadcomplete" reRender="cardSignCreateGrid" />
                <a4j:support event="onclear" reRender="cardSignCreateGrid" />
            </rich:fileUpload>
            <h:outputText escape="true" value="Тип ключа подписи карт" styleClass="output-text" />
            <h:selectOneMenu value="#{cardSignCreatePage.signTypeCard}" styleClass="input-text">
                <f:selectItems value="#{cardSignCreatePage.getTypes(1)}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Код производителя" styleClass="output-text" />
            <h:inputText value="#{cardSignCreatePage.manufacturerCode}" styleClass="input-text" />
            <h:outputText escape="true" value="Наименование производителя" styleClass="output-text" />
            <h:inputText value="#{cardSignCreatePage.manufacturerName}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid">
            <a4j:commandButton value="Создать" action="#{cardSignCreatePage.createCardSign(1)}"
                               styleClass="command-button" />
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </h:panelGroup>


    <h:panelGroup rendered="#{!cardSignCreatePage.newProvider}">
        <h:panelGrid id="cardSignCreateGrid_OLD" styleClass="borderless-grid" columns="2">
            <h:outputText escape="true" value="Тип ключа" styleClass="output-text" />
            <h:selectOneMenu value="#{cardSignCreatePage.signTypeCard}" styleClass="input-text">
                <f:selectItems value="#{cardSignCreatePage.getTypes(2)}" />
            </h:selectOneMenu>
            <h:outputText escape="true" value="Данные ключа" styleClass="output-text" />
            <h:outputText value="#{cardSignCreatePage.signDataSize}" styleClass="output-text" />
            <h:outputText value="Загрузка файла с данными ключа" styleClass="output-text" />
            <rich:fileUpload id="cardSignFileUploadElement_OLD" styleClass="upload" addButtonClass="upload-command-button"
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
                             fileUploadListener="#{cardSignCreatePage.fileUploadListener}">
                <f:facet name="label">
                    <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
                </f:facet>
                <a4j:support event="onuploadcomplete" reRender="cardSignCreateGrid_OLD" />
                <a4j:support event="onclear" reRender="cardSignCreateGrid_OLD" />
            </rich:fileUpload>
            <h:outputText escape="true" value="Код производителя" styleClass="output-text" />
            <h:inputText value="#{cardSignCreatePage.manufacturerCode}" styleClass="input-text" />
            <h:outputText escape="true" value="Наименование производителя" styleClass="output-text" />
            <h:inputText value="#{cardSignCreatePage.manufacturerName}" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid styleClass="borderless-grid">
            <a4j:commandButton value="Создать" action="#{cardSignCreatePage.createCardSign(2)}"
                               styleClass="command-button" />
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </h:panelGroup>
</h:panelGrid>