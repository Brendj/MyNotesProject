<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditClients())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель загрузки клиентов из файла --%>
<h:panelGrid id="clientFileLoaderPanel" binding="#{mainPage.clientFileLoadPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="1">
        <h:outputText value="Файл для загрузки" styleClass="output-text"/>
        <h:commandLink action="#{mainPage.clientFileLoadPage.downloadSample}" id="downloadSample" value="Скачать образец" styleClass="command-link" />
    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="borderless-grid">

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.clientFileLoadPage.org.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Проверять дубликаты (по ФИО клиента)" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.clientFileLoadPage.checkFullNameUnique}" styleClass="output-text" >
            <a4j:support event="onclick" reRender="clientFileLoaderPanel" ajaxSingle="true" />
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:outputText escape="true"
                  value="Обработано: #{mainPage.clientFileLoadPage.lineResultSize}. Успешно: #{mainPage.clientFileLoadPage.successLineNumber}"
                  styleClass="output-text" />

    <rich:fileUpload id="clientFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.clientLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="clientFileLoaderPanel" />
        <a4j:support event="onclear" reRender="clientFileLoaderPanel" />
    </rich:fileUpload>

    <a4j:outputPanel>
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable id="clientLoadResultTable" value="#{mainPage.clientFileLoadPage.lineResults}" var="item" rows="20"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер строки файла" />
            </f:facet>
            <h:outputText escape="true" value="#{item.lineNo}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код результата" />
            </f:facet>
            <h:outputText escape="true" value="#{item.resultCode}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сообщение" />
            </f:facet>
            <h:outputText escape="true" value="#{item.message}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Идентификатор клиента в БД (IdOfClient)" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.idOfClient}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="clientLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>

    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showClientLoadResultCSVList}"
                     styleClass="command-button" />

</h:panelGrid>