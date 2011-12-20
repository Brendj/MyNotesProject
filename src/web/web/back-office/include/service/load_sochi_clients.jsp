<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="sochiClientsLoadPanel" binding="#{mainPage.sochiClientsLoadPage.pageComponent}"
             styleClass="borderless-grid">

    <rich:fileUpload id="sochiClientsFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.sochiClientsLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="sochiClientsLoadPanel" />
        <a4j:support event="onclear" reRender="sochiClientsLoadPanel" />
    </rich:fileUpload>

    <h:outputText escape="true"
                  value="Обработано: #{mainPage.sochiClientsLoadPage.lineResultSize}. Успешно: #{mainPage.sochiClientsLoadPage.successLineNumber}"
                  styleClass="output-text" />

    <rich:dataTable id="sochiClientsLoadResultTable" value="#{mainPage.sochiClientsLoadPage.lineResults}" var="item"
                    rows="20"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column"
                    footerClass="data-table-footer">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер строки" />
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
                <h:outputText escape="true" value="Номер договора" />
            </f:facet>
            <h:commandLink action="#{mainPage.showSochiClientsViewPage}" styleClass="command-link">
                <h:outputText escape="true" value="#{item.client.contractId}" converter="contractIdConverter"
                              styleClass="output-text" />
                <f:setPropertyActionListener value="#{item.client.contractId}"
                                             target="#{mainPage.selectedSochiClientContractId}" />
            </h:commandLink>
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="ФИО клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{item.client.fullName}" styleClass="output-text" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес клиента" />
            </f:facet>
            <h:outputText escape="true" value="#{item.client.address}" styleClass="output-text" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="sochiClientsLoadResultTable" renderIfSinglePage="false" maxPages="5"
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