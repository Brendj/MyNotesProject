<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%-- Панель загрузки записей в справочник продуктов питания из файла --%>
<h:panelGrid id="productGuideFileLoaderPanel" binding="#{mainPage.productGuideLoadPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid columns="2" styleClass="borderless-grid">

        <h:outputText escape="true" value="Конфигурация поставщика" styleClass="output-text" />
        <h:selectOneMenu id="selectCurrentConfigurationProvider" value="#{mainPage.currentConfigurationProvider}" styleClass="input-text long-field" >
            <f:selectItems value="#{mainPage.productGuideLoadPage.configurationProviderMenu.items}" />
            <a4j:support event="onchange" action="#{mainPage.updateProductGuideListPage}" />
        </h:selectOneMenu>

        <%--<h:outputText escape="true" value="Проверять дубликаты (по ФИО клиента)" styleClass="output-text" />--%>
        <%--<h:selectBooleanCheckbox value="#{mainPage.clientFileLoadPage.checkFullNameUnique}" styleClass="output-text" >--%>
            <%--<a4j:support event="onclick" reRender="clientFileLoaderPanel" ajaxSingle="true" />--%>
        <%--</h:selectBooleanCheckbox>--%>

    </h:panelGrid>

    <h:outputText escape="true"
                  value="Обработано: #{mainPage.productGuideLoadPage.lineResultSize}. Успешно: #{mainPage.productGuideLoadPage.successLineNumber}"
                  styleClass="output-text" />

    <rich:fileUpload id="productGuideFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{mainPage.productGuideLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="productGuideFileLoaderPanel" />
        <a4j:support event="onclear" reRender="productGuideFileLoaderPanel" />
    </rich:fileUpload>

    <a4j:outputPanel>
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable id="productGuideLoadResultTable" value="#{mainPage.productGuideLoadPage.lineResults}" var="item" rows="20"
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
                <h:outputText escape="true" value="Идентификатор клиента в БД (IdOfProductGuide)" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfProductGuide}" styleClass="output-text" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="productGuideLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <%--<h:commandButton value="Выгрузить в CSV" action="#{mainPage.showProductGuideLoadResultCSVList}"--%>
                     <%--styleClass="command-button" />--%>

</h:panelGrid>