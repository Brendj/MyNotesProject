<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="externalSystemsPage" type="ru.axetta.ecafe.processor.web.ui.option.ExternalSystemsPage"--%>
<h:panelGrid id="externalSystemGrid" binding="#{externalSystemsPage.pageComponent}" styleClass="borderless-grid">

    <rich:simpleTogglePanel label="Отладочные задачи" switchType="client"
                            opened="true" headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">

            <h:outputText escape="true" value="Отправлять события о картах во внешнюю систему" styleClass="output-text" />
            <h:selectBooleanCheckbox value="#{externalSystemsPage.enabled}" styleClass="output-text" />

            <h:outputText escape="true" value="Адрес внешней системы" styleClass="output-text" />
            <h:inputText value="#{externalSystemsPage.url}" maxlength="32" styleClass="input-text"/>

            <h:outputText escape="true" value="Типы электронных носителей" styleClass="output-text" />
            <rich:dataTable id="externalSystemsSettingType" value="#{externalSystemsPage.types}" var="item"
                            rows="8"
                            columnClasses="left-aligned-column, center-aligned-column"
                            footerClass="data-table-footer">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Тип электронного носителя" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Статус" />
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{item.enabled}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>

            <h:outputText escape="true" value="Виды операций" styleClass="output-text" />
            <rich:dataTable id="externalSystemsSettingOperationsType" value="#{externalSystemsPage.operationTypes}" var="item"
                            rows="8"
                            columnClasses="left-aligned-column, center-aligned-column"
                            footerClass="data-table-footer">
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Вид операции" />
                    </f:facet>
                    <h:outputText escape="true" value="#{item.name}" styleClass="output-text" />
                </rich:column>
                <rich:column headerClass="column-header">
                    <f:facet name="header">
                        <h:outputText escape="true" value="Статус" />
                    </f:facet>
                    <h:selectBooleanCheckbox value="#{item.enabled}" styleClass="output-text" />
                </rich:column>
            </rich:dataTable>

        </h:panelGrid>
    </rich:simpleTogglePanel>
</h:panelGrid>

<h:panelGrid columns="1" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{externalSystemsPage.save}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>