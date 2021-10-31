<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="orgCatalogFindPage" styleClass="borderless-grid">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>


    <rich:simpleTogglePanel label="Введите параметры поиска" switchType="client" opened="true"
                            headerClass="filter-panel-header">
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Наименование/номер организации" styleClass="output-text" />
            <h:inputText value="#{orgCatalogFindPage.orgName}" maxlength="20" size="20" styleClass="input-text" />
            <h:outputText escape="true" value="GUID организации" styleClass="output-text" />
            <h:inputText value="#{orgCatalogFindPage.guid}" maxlength="36" size="40" styleClass="input-text" />
        </h:panelGrid>
        <h:panelGrid columns="2" styleClass="borderless-grid">

            <a4j:commandButton value="Найти в реестрах" action="#{orgCatalogFindPage.updateList}" reRender="workspaceTogglePanel"
                               styleClass="command-button" />

        </h:panelGrid>
    </rich:simpleTogglePanel>


    <rich:dataTable id="orgCatalogFindTable" footerClass="data-table-footer" value="#{orgCatalogFindPage.orgInfos}"
                    var="item"
                    columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column">
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Номер ОУ" />
            </f:facet>
            <h:outputText value="#{item.number}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Сокращенное наименование" />
            </f:facet>
            <h:outputText value="#{item.shortName}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Адрес" />
            </f:facet>
            <h:outputText value="#{item.address}" />
        </rich:column>
        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="GUID" />
            </f:facet>
            <h:outputText value="#{item.guid}" />
        </rich:column>

    </rich:dataTable>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>
