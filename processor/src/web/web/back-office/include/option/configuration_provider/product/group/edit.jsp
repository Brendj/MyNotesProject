<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productGroupEditPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupEditPage"--%>
<h:panelGrid id="productGroupEditPanelGrid" binding="#{productGroupEditPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text required-field" />
        <h:inputTextarea value="#{productGroupEditPage.currentProductGroup.nameOfGroup}" cols="128" rows="4" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneListbox value="#{productGroupEditPage.currentProductGroup.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>
        <h:outputText escape="true" value="Код классификации" styleClass="output-text" />
        <h:inputText value="#{productGroupEditPage.currentProductGroup.сlassificationCode}" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Сохранить группу" action="#{productGroupEditPage.onSave}"
                           reRender="productGroupEditPanelGrid, mainMenu" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>