<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="technologicalMapGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupViewPage"--%>

<h:panelGrid id="technologicalMapGroupViewPanelGrid" binding="#{technologicalMapGroupViewPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text" />
        <h:inputText value="#{technologicalMapGroupViewPage.currentTechnologicalMapGroup.nameOfGroup}" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{technologicalMapGroupViewPage.currentTechnologicalMapGroup.deletedState}" readonly="true" disabled="true"/>
        <h:outputText escape="true" value="Технологической карты" styleClass="output-text" />
        <h:commandLink value="#{technologicalMapGroupViewPage.countTechnologicalMaps}" action="#{technologicalMapGroupViewPage.showTechnologicalMaps}" styleClass="command-link"/>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Редактировать" action="#{technologicalMapGroupViewPage.show}"
                           reRender="workspaceTogglePanel, mainMenu" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>