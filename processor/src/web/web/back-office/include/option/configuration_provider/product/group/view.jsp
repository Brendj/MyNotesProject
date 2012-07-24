<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupViewPage"--%>

<h:panelGrid id="productGroupViewPanelGrid" binding="#{productGroupViewPage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text" />
        <h:inputTextarea value="#{productGroupViewPage.currentProductGroup.nameOfGroup}" cols="128" rows="4" styleClass="input-text long-field" readonly="true" />
        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{productGroupViewPage.currentProductGroup.deletedState}" readonly="true" disabled="true"/>
        <h:outputText escape="true" value="Код классификации" styleClass="output-text" />
        <h:inputText value="#{productGroupViewPage.currentProductGroup.сlassificationCode}" readonly="true" maxlength="32" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Продукты" styleClass="output-text" />
        <h:commandLink value="#{productGroupViewPage.countProducts}" action="#{productGroupViewPage.showProducts}" styleClass="command-link"/>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Редактировать" action="#{productGroupEditPage.show}"
                           reRender="workspaceTogglePanel, mainMenu" styleClass="command-button" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>

</h:panelGrid>