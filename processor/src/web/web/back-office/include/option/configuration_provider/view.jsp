<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 15.05.12
  Time: 22:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="configurationProviderViewPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderViewPage"--%>
<h:panelGrid styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Идентификатор" styleClass="output-text" />
    <h:inputText readonly="true" value="#{configurationProviderViewPage.currentConfigurationProvider.idOfConfigurationProvider}" styleClass="input-text"
                     style="width: 200px;" />

    <h:outputText escape="true" value="Имя" styleClass="output-text" />
    <h:inputText readonly="true" value="#{configurationProviderViewPage.currentConfigurationProvider.name}" styleClass="input-text"/>

    <h:outputText escape="true" value="Организации" styleClass="output-text" />
    <h:inputText readonly="true" value="Данная конфигурация не привязана ни к одной организации." styleClass="input-text long-field" rendered="#{configurationProviderViewPage.currentConfigurationProvider.orgEmpty}"/>
    <rich:dataTable value="#{configurationProviderViewPage.currentConfigurationProvider.orgs}" var="org" rendered="#{!configurationProviderViewPage.currentConfigurationProvider.orgEmpty}">
        <rich:column>
            <h:inputText readonly="true" value="#{org.shortName}" styleClass="input-text"/>
        </rich:column>
    </rich:dataTable>
    <h:outputText escape="true" value="Продукты" styleClass="output-text" />
    <h:commandLink value="Перейти к списку" action="#{configurationProviderViewPage.showProducts}" styleClass="command-link"/>

    <h:outputText escape="true" value="Технологической карты" styleClass="output-text" />
    <h:commandLink value="Перейти к списку" action="#{configurationProviderViewPage.showTechnologicalMaps}" styleClass="command-link"/>

    <a4j:commandButton value="Редактировать" action="#{configurationProviderEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>