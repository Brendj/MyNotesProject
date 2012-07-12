<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.product.ProductCreatePage"--%>
<h:panelGrid id="productGroupCreateCreatePanelGrid" binding="#{productCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1" rendered="#{productCreatePage.rendered}">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
        <h:selectOneMenu id="selectCurrentConfigurationProvider" value="#{productCreatePage.currentIdOfConfigurationProvider}" styleClass="input-text long-field" >
            <f:selectItems value="#{productCreatePage.configurationProviderMenu.items}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Группа продуктов" styleClass="output-text" />
        <h:selectOneMenu id="selectCurrentProductGroup" value="#{productCreatePage.currentIdOfProductGroup}" styleClass="input-text long-field">
            <f:selectItems value="#{productCreatePage.productGroupMenu.items}" />
        </h:selectOneMenu>

        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.code}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.fullName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Товарное название" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.productName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код (коды) ОКП" styleClass="output-text" />
        <h:inputText value="#{productCreatePage.product.okpCode}" maxlength="128" styleClass="input-text long-field" />

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <a4j:commandButton value="Создать продукт" action="#{productCreatePage.onSave}"
                           reRender="productGroupCreateCreatePanelGrid" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>