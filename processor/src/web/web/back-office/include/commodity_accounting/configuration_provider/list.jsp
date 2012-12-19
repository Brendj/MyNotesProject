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
<%--@elvariable id="selectedConfigurationProviderGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.SelectedConfigurationProviderGroupPage"--%>
<h:panelGrid id="configurationProviderEditPage" binding="#{configurationProviderListPage.pageComponent}"
             styleClass="borderless-grid" columns="1">

    <rich:dataTable id="configurationProviderListTable" value="#{configurationProviderListPage.configurationProviderList}" var="configurationProvider"
                    columnClasses="left-aligned-column, left-aligned-column, center-aligned-column" width="400px" rowKeyVar="row">
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

        <rich:column headerClass="column-header" width="50px">
            <f:facet name="header">
                <h:outputText escape="true" value="Удалить" styleClass="output-text"/>
            </f:facet>

            <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                             oncomplete="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.show()">
                <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
                <f:setPropertyActionListener value="#{configurationProvider.idOfConfigurationProvider}"
                                             target="#{mainPage.removedConfigurationProviderItemId}" />
            </a4j:commandLink>

        </rich:column>

    </rich:dataTable>

</h:panelGrid>
