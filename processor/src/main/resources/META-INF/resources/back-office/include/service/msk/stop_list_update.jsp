<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="uosStopListPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.UosStopListPage"--%>

<h:panelGrid id="stopListUpdatePage" styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>


    <rich:simpleTogglePanel label="Обновление стоп-лист карт" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Время последнего обновления стоп-листа" styleClass="output-text" />
            <h:inputText value="#{uosStopListPage.lastUpdate}" maxlength="20" size="20" styleClass="input-text" readonly="true"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Текущий размер стоп-листа" styleClass="output-text" />
            <h:inputText value="#{uosStopListPage.stopListSize}" maxlength="20" size="20" styleClass="input-text" readonly="true"/>
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Запросить" action="#{uosStopListPage.updateStopList}" reRender="workspaceTogglePanel"
                               styleClass="command-button" />

        </h:panelGrid>
    </rich:simpleTogglePanel>


    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
