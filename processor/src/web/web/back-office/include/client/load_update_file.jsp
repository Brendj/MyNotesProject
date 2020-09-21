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
<h:panelGrid id="clientUpdateFileLoaderPanel" binding="#{mainPage.clientUpdateFileLoadPage.pageComponent}"
             styleClass="borderless-grid">
    <h:panelGrid columns="2" styleClass="borderless-grid">

        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{mainPage.clientUpdateFileLoadPage.org.shortName}" readonly="true" styleClass="input-text"
                         style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
<rich:tabPanel>
    <rich:tab label="Обновление из файла">
    <h:panelGrid columns="1">
        <h:commandLink action="#{mainPage.clientUpdateFileLoadPage.downloadClients}" id="downloadOrgClients"
                       value="Выгрузить клиентов организации" styleClass="command-link" disabled="#{!mainPage.clientUpdateFileLoadPage.orgSelected()}"/>
    </h:panelGrid>

    <h:panelGrid columns="1">
        <h:outputText value="Загрузка файла:" styleClass="output-text"/>
    </h:panelGrid>

    <rich:fileUpload id="clientUpdateFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.clientUpdateLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="clientUpdateFileLoaderPanel" />
        <a4j:support event="onclear" reRender="clientUpdateFileLoaderPanel" />
    </rich:fileUpload>

    <h:outputText escape="true"
                  value="Обработано: #{mainPage.clientUpdateFileLoadPage.lineResultSize}. Успешно: #{mainPage.clientUpdateFileLoadPage.successLineNumber}"
                  styleClass="output-text" />
    <h:outputText escape="true"
                  value="Во время обработки файла произошла ошибка: #{mainPage.clientUpdateFileLoadPage.errorText}"
                  styleClass="error-output-text" rendered="#{mainPage.clientUpdateFileLoadPage.errorPresent}" />

    <rich:dataTable id="clientUpdateLoadResultTable" value="#{mainPage.clientUpdateFileLoadPage.lineResults}" var="item" rows="20"
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
            <h:outputText escape="true" value="#{item.resultDescription}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Л/с клиента" />
            </f:facet>
            <a4j:commandLink action="#{mainPage.showClientViewPage}" styleClass="command-link" reRender="mainMenu, workspaceForm">
                <h:outputText escape="true" value="#{item.contractId}" styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.idOfClient}" target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="clientUpdateLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showClientUpdateLoadResultCSVList}"
                     styleClass="command-button" />
    </rich:tab>
    <rich:tab label="Изменение группы">
        <h:panelGrid columns="1">
            <h:outputText value="Загрузка файла:" styleClass="output-text"/>
        </h:panelGrid>

        <rich:fileUpload id="clientUpdateGroupFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                         fileUploadListener="#{mainPage.clientUpdateFileLoadPage.uploadGroupChange}" disabled="#{!mainPage.clientUpdateFileLoadPage.orgSelected()}">
            <f:facet name="label">
                <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
            </f:facet>
            <a4j:support event="onuploadcomplete" reRender="clientUpdateFileLoaderPanel" />
            <a4j:support event="onclear" reRender="clientUpdateFileLoaderPanel" />
        </rich:fileUpload>

        <h:outputText escape="true"
                      value="Обработано: #{mainPage.clientUpdateFileLoadPage.lineGroupsResultSize}. Успешно: #{mainPage.clientUpdateFileLoadPage.successLineNumber}"
                      styleClass="output-text" /><br/>
        <h:outputText escape="true"
                      value="Во время обработки файла произошла ошибка: #{mainPage.clientUpdateFileLoadPage.errorTextGroups}"
                      styleClass="error-output-text" rendered="#{mainPage.clientUpdateFileLoadPage.errorGroupsPresent}" />

        <rich:dataTable id="clientGroupsUpdateLoadResultTable" value="#{mainPage.clientUpdateFileLoadPage.lineGroupsResults}" var="item" rows="20"
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
                <h:outputText escape="true" value="#{item.resultDescription}" styleClass="output-text" />
            </rich:column>
            <rich:column headerClass="column-header">
                <f:facet name="header">
                    <h:outputText escape="true" value="ФИО клиента" />
                </f:facet>
                <h:outputText escape="true" value="#{item.fio}" styleClass="output-text" />
            </rich:column>

            <f:facet name="footer">
                <rich:datascroller for="clientGroupsUpdateLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

        <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showClientUpdateGroupsLoadResultCSVList}"
                         styleClass="command-button" />

    </rich:tab>
</rich:tabPanel>
</h:panelGrid>
