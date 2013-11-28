<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 15.05.12
  Time: 22:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="configurationProviderListPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderListPage"--%>
<%--@elvariable id="configurationProviderEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderEditPage"--%>
<%--@elvariable id="configurationProviderViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderViewPage"--%>
<%--@elvariable id="configurationProviderDeletePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderDeletePage"--%>
<%--@elvariable id="selectedConfigurationProviderGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.SelectedConfigurationProviderGroupPage"--%>
<h:panelGrid id="configurationProviderListPage" binding="#{configurationProviderListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <rich:dataTable id="configurationProviderListTable" value="#{configurationProviderListPage.configurationProviderList}" var="configurationProvider"
                    columnClasses="left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column" width="400px" rowKeyVar="row" rows="15">
        <rich:column  headerClass="column-header">
            <f:facet name="header">
                <h:outputText value="№" styleClass="output-text" escape="true"/>
            </f:facet>
            <h:outputText styleClass="output-text" value="#{row+1}" />
        </rich:column>

        <rich:column headerClass="column-header" width="250px">
            <f:facet name="header">
                <h:outputText escape="true" value="Имя" styleClass="output-text"/>
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{configurationProvider.name}" action="#{configurationProviderViewPage.show}"
                           styleClass="command-link">
                <f:setPropertyActionListener value="#{configurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}"/>
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header" styleClass="center-aligned-column">
            <f:facet name="header">
                <h:outputText escape="true" value="Редактировать" />
            </f:facet>
            <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{configurationProviderEditPage.show}" styleClass="command-link">
                <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{configurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}"/>
            </a4j:commandLink>
        </rich:column>

        <rich:column headerClass="column-header" width="50px">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" styleClass="output-text"/>
            </f:facet>

            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{configurationProvider}"
                                             target="#{configurationProviderDeletePage.configurationProvider}" />
            </a4j:commandLink>

        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="configurationProviderListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />

    </h:panelGrid>
</h:panelGrid>

