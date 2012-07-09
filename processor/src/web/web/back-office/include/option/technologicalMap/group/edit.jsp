<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="technologicalMapGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group.TechnologicalMapGroupEditPage"--%>
<h:panelGrid id="technologicalMapGroupEditPanelGrid" binding="#{technologicalMapGroupEditPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы технологическох карт" styleClass="output-text" />
        <h:inputText value="#{technologicalMapGroupEditPage.currentTechnologicalMapGroup.nameOfGroup}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Номер технологической карты" styleClass="output-text" />
        <h:selectOneListbox value="#{technologicalMapGroupEditPage.currentTechnologicalMapGroup.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Сохранить группу" action="#{technologicalMapGroupEditPage.onSave}"
                           reRender="technologicalMapGroupEditPanelGrid, mainMenu" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>