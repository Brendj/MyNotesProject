<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToCommodityAccounting()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%-- Панель загрузки элементов базовых товаров из csv --%>
<h:panelGrid id="loadingElementsOfBasicGoodsGrid" binding="#{mainPage.loadingElementsOfBasicGoodsPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText value="Загрузите файл формата .csv, кодировка UTF-8, " />
        <h:outputText
                value="в формате: 'Ид. производственных конфигураций через запятую'; 'Наименование базового товара'; 'Единица измерения'; 'Масса нетто (грамм)'" />
        <rich:fileUpload id="loadingElementsFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                         fileUploadListener="#{mainPage.basicGoodsLoadFileListener}">
            <f:facet name="label">
                <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
            </f:facet>
            <a4j:support event="onuploadcomplete" reRender="loadingElementsOfBasicGoodsGrid" />
            <a4j:support event="onclear" reRender="loadingElementsOfBasicGoodsGrid" />
        </rich:fileUpload>

        <h:panelGroup id="info">
            <rich:panel bodyClass="info">
                <f:facet name="header">
                    <h:outputText value="Загруженные файлы" />
                </f:facet>
                <rich:dataGrid columns="1" value="#{mainPage.loadingElementsOfBasicGoodsPage.files}" var="file"
                               rowKeyVar="row">
                    <h:panelGrid columns="1">
                        <h:outputText value="#{file.fileName}" />
                    </h:panelGrid>
                </rich:dataGrid>
            </rich:panel>
        </h:panelGroup>
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Провести" action="#{mainPage.loadingElementsOfBasicGoodsGenerate}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable id="loadingElementsOfBasicGoodsTable"
                    value="#{mainPage.loadingElementsOfBasicGoodsPage.loadingElementsOfBasicGoodsItems}" var="item"
                    rows="30"
                    columnClasses="right-aligned-column, right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, right-aligned-column, right-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column, center-aligned-column,  center-aligned-column"
                    footerClass="data-table-footer" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Производственная конфигурация" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.nameOfGood}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Наименование базового товара" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.configurationProviderName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Единица измерения" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.unitsScale}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Масса нетто (грамм)" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.netWeight}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Результат" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.result}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="loadingElementsOfBasicGoodsTable" renderIfSinglePage="false" maxPages="5"
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
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showLoadingElementsOfBasicGoodsCSVList}"
                     styleClass="command-button" />
</h:panelGrid>