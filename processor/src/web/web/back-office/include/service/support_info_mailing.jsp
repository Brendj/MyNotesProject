<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<style>
    .top {
        vertical-align: top;
    }
    .info {
        height: 200px;
        overflow: auto;
    }
</style>

<h:panelGrid id="supportInfoMailingGrid" binding="#{mainPage.supportInfoMailingPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Кому (номер лицевого счета)" styleClass="output-text" />
        <h:inputText value="#{mainPage.supportInfoMailingPage.address}" size="80" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Прикрепить файл с л/с" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="2" columnClasses="top,top">
            <rich:fileUpload id="infoMailingFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
                             addButtonClassDisabled="upload-command-button-diasbled" cleanButtonClass="upload-command-button"
                             cleanButtonClassDisabled="upload-command-button-diasbled" stopButtonClass="upload-command-button"
                             stopButtonClassDisabled="upload-command-button-diasbled" uploadButtonClass="upload-command-button"
                             uploadButtonClassDisabled="upload-command-button-diasbled" fileEntryClass="output-text"
                             fileEntryClassDisabled="output-text" fileEntryControlClass="output-text"
                             fileEntryControlClassDisabled="output-text" sizeErrorLabel="Недопустимый размер"
                             stopControlLabel="Остановить" stopEntryControlLabel="Остановить" addControlLabel="Добавить файл"
                             clearControlLabel="Очистить" clearAllControlLabel="Очистить все" doneLabel="Готово"
                             cancelEntryControlLabel="Отменить" transferErrorLabel="Ошибка передачи"
                             uploadControlLabel="Загрузка файла" progressLabel="Загрузка" listHeight="204px"
                             fileUploadListener="#{mainPage.supportInfoMailingPage.loadFileListener}"
                             maxFilesQuantity="1">
                         <a4j:support event="onuploadcomplete" reRender="supportInfoMailingGrid" />
                         <a4j:support event="onclear" reRender="supportInfoMailingGrid" />
            </rich:fileUpload>
            <h:panelGroup id="info">
                <rich:panel bodyClass="info">
                    <f:facet name="header">
                        <h:outputText value="Загруженный файл" />
                    </f:facet>
                    <h:outputText value="Файл не загружен"
                        rendered="#{mainPage.supportInfoMailingPage.file == null}" />
                    <rich:dataGrid columns="1" value="#{mainPage.supportInfoMailingPage.file}"
                        var="file" rowKeyVar="row">
                        <h:panelGrid columns="1">
                                <h:outputText value="#{file.fileName}" />
                        </h:panelGrid>
                    </rich:dataGrid>
                </rich:panel>
                <rich:spacer height="3"/>
                <a4j:commandButton action="#{mainPage.supportInfoMailingPage.clearUploadData}"
                    reRender="supportInfoMailingGrid" value="Очистить"
                    rendered="#{mainPage.supportInfoMailingPage.file != null}" />
            </h:panelGroup>
        </h:panelGrid>
        <h:outputText escape="true" value="Профиль клиента" styleClass="output-text" />
        <h:selectOneMenu id="guardianFilter" value="#{mainPage.supportInfoMailingPage.guardianFilter}" style="width:350px;" >
            <f:selectItems value="#{mainPage.supportInfoMailingPage.guardianFilterItems}"/>
        </h:selectOneMenu>
        <h:outputText escape="true" value="Пол" styleClass="output-text" />
        <h:selectOneMenu id="genderFilter" value="#{mainPage.supportInfoMailingPage.genderFilter}" style="width:350px;" >
            <f:selectItems value="#{mainPage.supportInfoMailingPage.genderFilterItems}"/>
        </h:selectOneMenu>

        <h:outputText escape="true" value="Ограничить по возрастным категориям" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.ageCategory}" styleClass="output-text">
            <a4j:support event="onclick" reRender="supportInfoMailingGrid" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="До 20 лет" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.ageBefore20}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="21-25 лет" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.age2125}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="26-30 лет" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.age2630}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="31-35 лет" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.age3135}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="36-45 лет" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.age3645}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>
        <h:outputText escape="true" value="45 и старше" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}"/>
        <h:selectBooleanCheckbox value="#{mainPage.supportInfoMailingPage.ageOver46}" styleClass="output-text" rendered="#{mainPage.supportInfoMailingPage.ageCategory}">
            <a4j:support event="onclick" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="Текст" styleClass="output-text" />
        <h:inputTextarea rows="10" cols="80" value="#{mainPage.supportInfoMailingPage.text}" styleClass="input-text" />

    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton id="sendSupportInfoMailingBtn" value="Отправить" action="#{mainPage.supportInfoMailingPage.sendSupportInfoMailing}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button"
                           status="infoMailingSendGenerateStatus" />
        <a4j:status id="infoMailingSendGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>