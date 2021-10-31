<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="technologicalMapGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupViewPage"--%>
<%--@elvariable id="technologicalMapGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupEditPage"--%>
<%--@elvariable id="configurationProviderViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderViewPage"--%>
<%--@elvariable id="selectedConfigurationProviderGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.SelectedConfigurationProviderGroupPage"--%>
<h:panelGrid id="technologicalMapGroupViewPanelGrid" binding="#{technologicalMapGroupViewPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text" />
        <h:inputText value="#{technologicalMapGroupViewPage.currentTechnologicalMapGroup.nameOfGroup}" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Организация поставщик" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{technologicalMapGroupViewPage.currentOrg.shortName}" action="#{mainPage.showOrgViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{technologicalMapGroupViewPage.currentOrg.idOfOrg}" target="#{mainPage.selectedIdOfOrg}" />
        </a4j:commandLink>
        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{technologicalMapGroupViewPage.currentConfigurationProvider.name}" action="#{configurationProviderViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{technologicalMapGroupViewPage.currentConfigurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}" />
        </a4j:commandLink>
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{technologicalMapGroupViewPage.currentTechnologicalMapGroup.deletedState}" readonly="true" disabled="true"/>
        <h:outputText escape="true" value="Технологической карты" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{technologicalMapGroupViewPage.countTechnologicalMaps}" action="#{technologicalMapGroupViewPage.showTechnologicalMaps}" styleClass="command-link"/>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Редактировать" action="#{technologicalMapGroupEditPage.show}"
                           reRender="workspaceTogglePanel, mainMenu" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>