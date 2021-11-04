<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="orgListLoaderPage" type="ru.axetta.ecafe.processor.web.ui.service.OrgListLoaderPage"--%>
<h:panelGrid id="orgListLoaderGrid" binding="#{orgListLoaderPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid columns="1">
        <h:outputText value="Файл для загрузки"/>
        <h:commandLink action="#{orgListLoaderPage.downloadSample}" id="downloadSample" value="Скачать образец" styleClass="command-link" />
    </h:panelGrid>
    <h:panelGrid columns="2">
        <rich:fileUpload id="orgFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                         fileUploadListener="#{orgListLoaderPage.uploadFile}">

            <f:facet name="label">
                <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
            </f:facet>
            <a4j:support event="onuploadcomplete" reRender="orgListLoaderGrid" />
            <a4j:support event="onclear" reRender="orgListLoaderGrid" />
        </rich:fileUpload>
    </h:panelGrid>

    <h:outputText escape="true"
                  value="Обработано: #{orgListLoaderPage.lineResultSize}. Успешно: #{orgListLoaderPage.successLineNumber}"
                  styleClass="output-text" />

    <rich:dataTable id="orgListLoaderResultTable" value="#{orgListLoaderPage.lineResults}" var="item" rows="20"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column"
                    footerClass="data-table-footer">

        <f:facet name="header">
            <rich:columnGroup>
                <rich:column headerClass="column-header">
                    <h:outputText value="Номер строки файла" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText value="Код результата" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText value="Сообщение" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <h:outputText value="Идентификатор ОУ в БД (IdOfOrg)" />
                </rich:column>
            </rich:columnGroup>
        </f:facet>

        <rich:column styleClass="center-aligned-column">
            <h:outputText escape="true" value="#{item.lineNo}" styleClass="output-text" />
        </rich:column>

        <rich:column styleClass="center-aligned-column">
            <h:outputText escape="true" value="#{item.resultCode}" styleClass="output-text" />
        </rich:column>

        <rich:column styleClass="center-aligned-column">
            <h:outputText escape="true" value="#{item.message}" styleClass="output-text" />
        </rich:column>

        <rich:column styleClass="center-aligned-column">
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{item.idOfOrg}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
                <f:setPropertyActionListener value="#{item.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="orgListLoaderResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>