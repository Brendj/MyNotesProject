<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productCreatePage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductCreatePage"--%>
<h:panelGrid id="productCreatePanelGrid" binding="#{productCreatePage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Группа продуктов" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{productCreatePage.currentProductGroup.nameOfGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{productCreatePage.selectProductGroup}" reRender="productGroupSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('productGroupSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />

        </h:panelGroup>

        <h:outputText escape="true" value="Товарное название" styleClass="output-text required-field" />
        <h:inputText value="#{productCreatePage.product.productName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.code}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.fullName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код (коды) ОКП" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.okpCode}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код классификации" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.classificationCode}" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Создать продукт" action="#{productCreatePage.onSave}"
                           reRender="productCreatePanelGrid" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>