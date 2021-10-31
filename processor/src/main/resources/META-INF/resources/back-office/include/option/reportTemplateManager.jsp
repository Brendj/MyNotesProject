<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 01.05.12
  Time: 23:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="reportTemplateManagerGrid" binding="#{mainPage.reportTemplateManagerPage.pageComponent}" styleClass="borderless-grid" columns="1" width="1150">

    <rich:dataTable id="reportTemplateListTable" value="#{mainPage.reportTemplateManagerPage.items}" var="item"
                    columnClasses="left-aligned-column, left-aligned-column, center-aligned-column, right-aligned-column, center-aligned-column"
                    width="1150">
        <rich:column headerClass="column-header" width="350">
            <f:facet name="header">
                <h:outputText escape="true" value="Название отчета" />
            </f:facet>
            <h:outputText escape="true" value="#{item.reportName}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header" width="350">
            <f:facet name="header">
                <h:outputText escape="true" value="Имя файла" />
            </f:facet>
            <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header" width="200">
            <f:facet name="header">
                <h:outputText escape="true" value="Дата изменения" />
            </f:facet>
            <h:outputText escape="true" value="#{item.dateEdit}" converter="timeConverter" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header" width="150">
            <f:facet name="header">
                <h:outputText escape="true" value="Размер (Кб)" />
            </f:facet>
            <h:outputText escape="true" value="#{item.sizeInStr}" styleClass="output-text" />
        </rich:column>

        <rich:column headerClass="column-header" width="100">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" />
            </f:facet>
            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('reportTemplateDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{item.name}" target="#{mainPage.removedReportTemplate}" />
            </a4j:commandLink>
        </rich:column>
    </rich:dataTable>

    <rich:simpleTogglePanel switchType="client" label="Добавление шаблона" opened="false">

        <h:panelGrid styleClass="borderless-grid" columns="2">

            <h:outputText id="relativePathLabel" escape="true" value="Относительный путь: " styleClass="output-text" />
            <h:inputText value="#{mainPage.reportTemplateManagerPage.relativePath}" styleClass="input-text"
                         style="width: 100%;">
                <a4j:support event="onkeyup" reRender="relativePathLabel" />
            </h:inputText>

            <h:outputText escape="true" value="Файл отчета" styleClass="output-text" />
            <rich:fileUpload id="reportTemplateFileUploadElement" styleClass="upload"
                             addButtonClass="upload-command-button"
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
                             progressLabel="Загрузка" listHeight="204px" acceptedTypes="jasper"
                             fileUploadListener="#{mainPage.reportTemplateLoadFileListener}" maxFilesQuantity="10">
                <a4j:support event="onuploadcomplete" reRender="reportTemplateListTable, reportTemplateMessages" />
            </rich:fileUpload>

        </h:panelGrid>

    </rich:simpleTogglePanel>

    <h:panelGrid styleClass="borderless-grid" id="reportTemplateMessages">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
</h:panelGrid>
