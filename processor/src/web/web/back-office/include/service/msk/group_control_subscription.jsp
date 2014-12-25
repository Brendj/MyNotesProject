<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="groupControlSubscriptionsPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.GroupControlSubscriptionsPage"--%>
<h:panelGrid id="groupControlSubscriptionsPage" styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>

    <h:outputText value="Загрузите файл формата .csv, вида: Л/c , сумма" />
    <rich:fileUpload id="subscriptionFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                     fileUploadListener="#{groupControlSubscriptionsPage.subscriptionLoadFileListener}">
        <f:facet name="label">
            <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
        </f:facet>
        <a4j:support event="onuploadcomplete" reRender="groupControlSubscriptionsPage" />
        <a4j:support event="onclear" reRender="groupControlSubscriptionsPage" />
    </rich:fileUpload>


    <h:outputText escape="true"
                  value="Обработано: #{groupControlSubscriptionsPage.lineResultSize}. Успешно: #{groupControlSubscriptionsPage.successLineNumber}"
                  styleClass="output-text" />

    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable value="#{groupControlSubscriptionsPage.groupControlSubscriptionsItems}" var="item" rowKeyVar="row">
        <rich:column>
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{row+1}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Л/с" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.contractId}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Результат" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.result}" styleClass="output-text" />
        </rich:column>
    </rich:dataTable>

</h:panelGrid>
