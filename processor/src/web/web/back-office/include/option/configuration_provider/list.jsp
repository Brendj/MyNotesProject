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

<rich:dataTable id="configurationProviderListTable" value="#{mainPage.configurationProviderListPage.items}" var="item"
        columnClasses="left-aligned-column, left-aligned-column, center-aligned-column" width="400px">
    <rich:column headerClass="column-header" width="100px">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификаторв" />
        </f:facet>
        <h:commandLink value="#{item.idOfConfigurationProvider}" action="#{mainPage.showСonfigurationProviderViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfConfigurationProvider}" target="#{mainPage.selectedIdOfConfigurationProvider}" />
        </h:commandLink>
    </rich:column>

    <rich:column headerClass="column-header" width="250px">
        <f:facet name="header">
            <h:outputText escape="true" value="Имя" />
        </f:facet>
        <h:commandLink value="#{item.name}" action="#{mainPage.showСonfigurationProviderViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfConfigurationProvider}" target="#{mainPage.selectedIdOfConfigurationProvider}" />
        </h:commandLink>
    </rich:column>

    <rich:column headerClass="column-header" width="50px">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>

        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('removedСonfigurationProviderItemDeletePanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfConfigurationProvider}"
                                         target="#{mainPage.removedConfigurationProviderItemId}" />
        </a4j:commandLink>

    </rich:column>

</rich:dataTable>