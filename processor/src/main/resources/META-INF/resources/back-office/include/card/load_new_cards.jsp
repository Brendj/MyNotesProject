<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCards())
{ out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель загрузки новых, непривязанных карт из файла --%>
<h:panelGrid id="newCardFileLoaderPanel" binding="#{mainPage.newCardFileLoadPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Проверять дубликаты (по номеру, нанесенному на карту)"
                      styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.newCardFileLoadPage.checkCardPrintedNoUnique}" styleClass="output-text">
            <a4j:support event="onclick" reRender="newCardFileLoaderPanel" ajaxSingle="true" />
        </h:selectBooleanCheckbox>

        <h:outputText escape="true" value="#{mainPage.newCardFileLoadPage.cardTypeNames}"
                      styleClass="output-text"/>
    </h:panelGrid>

    <rich:fileUpload id="newCardFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.newCardLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="newCardFileLoaderPanel" />
        <a4j:support event="onclear" reRender="cardFileLoaderPanel" />
    </rich:fileUpload>

    <h:outputText escape="true"
                  value="Обработано: #{mainPage.newCardFileLoadPage.lineResultSize}. Успешно: #{mainPage.newCardFileLoadPage.successLineNumber}"
                  styleClass="output-text" />

    <rich:dataTable id="newCardLoadResultTable" value="#{mainPage.newCardFileLoadPage.lineResults}" var="item" rows="20"
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
                <h:outputText escape="true" value="Идентификатор карты в БД (IdOfNewCard)" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfNewCard}" styleClass="output-text" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="newCardLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showNewCardLoadResultCSVList}"
                     styleClass="command-button" />

</h:panelGrid>