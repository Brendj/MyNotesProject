<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>


<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель загрузки параметров клиента из файла --%>
<h:panelGrid id="registryLoaderPanel" binding="#{mainPage.registryLoadPage.pageComponent}" styleClass="borderless-grid">

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

    <h:panelGrid columns="2" styleClass="borderless-grid">

        <h:outputText escape="true" value="Абсолютный путь к директории с файлами"
                      styleClass="output-text" />
        <h:outputText escape="true" value=""
                      styleClass="output-text" />
        <h:outputText escape="true" value="на сервере (C:\foo\ или /root/foo/)"
                      styleClass="output-text" />
        <h:inputText value="#{mainPage.registryLoadPage.path}" maxlength="512"
                     styleClass="input-text" />

        <h:outputText escape="true" value="Первый файл в обработке" styleClass="output-text" />
        <h:inputText value="#{mainPage.registryLoadPage.firstFile}" maxlength="128"
                     styleClass="input-text" />

        <h:outputText escape="true" value="Последний файл в обработке" styleClass="output-text" />
        <h:inputText value="#{mainPage.registryLoadPage.lastFile}" maxlength="128"
                     styleClass="input-text" />

        <h:outputText escape="true" value="Обработка параметров" styleClass="output-text" />
        <h:selectOneRadio value="#{mainPage.registryLoadPage.parameters}" styleClass="input-text">
            <f:selectItem itemValue="1" itemLabel="Пол и дата рождения" />
            <f:selectItem itemValue="2" itemLabel="Представители" />
        </h:selectOneRadio>
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <a4j:commandButton value="Обработать параметры клиентов" action="#{mainPage.registryLoadPage.process()}"
                           reRender="registryLoadResultTable,registryLoadResultInfo"
                           status="updateStatus" onclick="this.disabled = true;" oncomplete="this.disabled = false;"/>
        <a4j:status id="updateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" id="registryLoadResultInfo">
        <h:outputText escape="true"
                      value="Обработано: #{mainPage.registryLoadPage.lineResultsSize}"
                      styleClass="output-text" />
    </h:panelGrid>

    <rich:dataTable id="registryLoadResultTable" value="#{mainPage.registryLoadPage.lineResults}" var="item" rows="20"
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
                <h:outputText escape="true" value="Идентификатор клиента в БД" />
            </f:facet>
            <h:outputText escape="true" value="#{item.idOfClient}" styleClass="output-text" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="registryLoadResultTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:commandButton value="Выгрузить в CSV" action="#{mainPage.showRegistryLoadResultCSVList}"
                     styleClass="command-button" />



</h:panelGrid>