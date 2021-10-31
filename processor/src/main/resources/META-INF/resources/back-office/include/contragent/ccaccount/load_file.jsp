<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель загрузки счетов клиентов из файла --%>
<h:panelGrid id="ccAccountFileLoadGrid" binding="#{mainPage.CCAccountFileLoadPage.pageComponent}"
             styleClass="borderless-grid">
    <rich:fileUpload id="ccAccountFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.ccAccountLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="ccAccountFileLoadGrid" />
        <a4j:support event="onclear" reRender="ccAccountFileLoadGrid" />
    </rich:fileUpload>
    <rich:dataTable id="ccAccountLoadResultTable" value="#{mainPage.CCAccountFileLoadPage.lineResults}" var="item"
                    rows="20"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер строки файла" />
            </f:facet>
            <h:outputText escape="true" value="#{item.line}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Код результата" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createResult.resultCode}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сообщение" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createResult.message}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Контрагент" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showContragentViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.createResult.ccAccount.contragent.contragentName}"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.createResult.ccAccount.contragent.idOfContragent}"
                                             target="#{mainPage.selectedIdOfContragent}" />
            </a4j:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер счета" />
            </f:facet>
            <h:outputText escape="true" value="#{item.createResult.ccAccount.idOfAccount}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Клиент" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showClientViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.createResult.ccAccount.client.shortName}"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.createResult.ccAccount.client.idOfClient}"
                                             target="#{mainPage.selectedIdOfClient}" />
            </a4j:commandLink>
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="ccAccountLoadResultTable" renderIfSinglePage="false" maxPages="5"
                               fastControls="hide" stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
