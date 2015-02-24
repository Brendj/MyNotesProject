<%--
  ~ Copyright (c) 2014. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="groupControlBenefitsPage" binding="#{mainPage.groupControlBenefitsPage.pageComponent}"
             styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText value="Загрузите файл формата .csv, разделитель в файле ';'" />
        <h:outputText
                value="вида: 'Наименование ОУ'; 'Группа (класс)'; 'Фамилия'; 'Имя'; 'Отчество'; 'Номер л/c'; 'Пуст. строка'; 'Названия льготных категорий - через запятую'" />
        <rich:fileUpload id="benefitFileUploadElement" styleClass="upload" addButtonClass="upload-command-button"
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
                         fileUploadListener="#{mainPage.benefitsLoadFileListener}">
            <f:facet name="label">
                <h:outputText escape="true" value="{_KB}KB/{KB}KB [{mm}:{ss}]" />
            </f:facet>
            <a4j:support event="onuploadcomplete" reRender="groupControlBenefitsPage" />
            <a4j:support event="onclear" reRender="groupControlBenefitsPage" />
        </rich:fileUpload>

        <h:panelGroup id="info">
            <rich:panel bodyClass="info">
                <f:facet name="header">
                    <h:outputText value="Загруженные файлы" />
                </f:facet>
                <rich:dataGrid columns="1" value="#{mainPage.groupControlBenefitsPage.files}" var="file"
                               rowKeyVar="row">
                    <h:panelGrid columns="1">
                        <h:outputText value="#{file.fileName}" />
                    </h:panelGrid>
                </rich:dataGrid>
            </rich:panel>
        </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Предварительная отмена льготных категорий по всем учащимся"
                      styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.groupControlBenefitsPage.clientCancelBenefits}"
                                 styleClass="output-text">
            <a4j:support event="onclick" ajaxSingle="true"
                         actionListener="#{mainPage.groupControlBenefitsPage.onHideClientCancelBenefitsChange}" />
        </h:selectBooleanCheckbox>
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Провести" action="#{mainPage.groupControlBenefitsGenerate}"
                           reRender="workspaceTogglePanel" styleClass="command-button" />
    </h:panelGrid>

    <a4j:outputPanel ajaxRendered="true">
        <h:panelGrid styleClass="borderless-grid">
            <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                           warnClass="warn-messages" />
        </h:panelGrid>
    </a4j:outputPanel>

    <rich:dataTable id="groupControlBenefitsTable"
                    value="#{mainPage.groupControlBenefitsPage.groupControlBenefitsItems}" var="item" rows="30"
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
                <h:outputText value="Наименование ОУ" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.orgName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Группа (класс)" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.groupName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Фамилия" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.surname}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Имя" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.firstName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Отчество" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.secondName}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Номер л/с" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.contractId}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Льготы" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.benefits}" styleClass="output-text" />
        </rich:column>
        <rich:column>
            <f:facet name="header">
                <h:outputText value="Результат" styleClass="output-text" />
            </f:facet>
            <h:outputText value="#{item.result}" styleClass="output-text" />
        </rich:column>
        <f:facet name="footer">
            <rich:datascroller for="groupControlBenefitsTable" renderIfSinglePage="false" maxPages="5"
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
    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showGroupControlBenefitsCSVList}"
                     styleClass="command-button" />
</h:panelGrid>
