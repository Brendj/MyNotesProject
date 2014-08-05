<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
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

<h:panelGrid id="supportEmailGrid" binding="#{mainPage.supportEmailPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Кому" styleClass="output-text" />
        <h:inputText value="#{mainPage.supportEmailPage.address}" size="80" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Тема" styleClass="output-text" />
        <h:inputText value="#{mainPage.supportEmailPage.subject}" size="80" maxlength="128" styleClass="input-text" />
        <h:outputText escape="true" value="Текст" styleClass="output-text" />
        <h:inputTextarea rows="15" cols="80" value="#{mainPage.supportEmailPage.text}" styleClass="input-text" />
        <h:outputText escape="true" value="Прикрепить файл" styleClass="output-text" />
        <h:panelGrid styleClass="borderless-grid" columns="2" columnClasses="top,top">
            <rich:fileUpload id="mailFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                             fileUploadListener="#{mainPage.mailLoadFileListener}"
                             maxFilesQuantity="#{mainPage.supportEmailPage.uploadsAvailable}">
                         <a4j:support event="onuploadcomplete" reRender="supportEmailGrid" />
                         <a4j:support event="onclear" reRender="supportEmailGrid" />
            </rich:fileUpload>
            <h:panelGroup id="info">
                <rich:panel bodyClass="info">
                    <f:facet name="header">
                        <h:outputText value="Загруженные файлы" />
                    </f:facet>
                    <h:outputText value="Файлы не загружены"
                        rendered="#{mainPage.supportEmailPage.size == 0}" />
                    <rich:dataGrid columns="1" value="#{mainPage.supportEmailPage.files}"
                        var="file" rowKeyVar="row">
                        <h:panelGrid columns="1">
                                <h:outputText value="#{file.fileName}" />
                        </h:panelGrid>
                    </rich:dataGrid>
                </rich:panel>
                <rich:spacer height="3"/>
                <br />
                <a4j:commandButton action="#{mainPage.supportEmailPage.clearUploadData}"
                    reRender="supportEmailGrid" value="Очистить"
                    rendered="#{mainPage.supportEmailPage.size != 0}" />
            </h:panelGroup>
        </h:panelGrid>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton id="sendSupportEmailBtn" value="Отправить" action="#{mainPage.sendSupportEmail}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button"
                           status="mailSendGenerateStatus" />
        <a4j:status id="mailSendGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>