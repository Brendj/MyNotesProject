<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductViewPage"--%>
<%--@elvariable id="productGroupViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.ProductGroupViewPage"--%>
<%--@elvariable id="configurationProviderViewPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderViewPage"--%>
<%--@elvariable id="selectedConfigurationProviderGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.SelectedConfigurationProviderGroupPage"--%>
<%--@elvariable id="selectedProductGroupGroupPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.group.SelectedProductGroupGroupPage"--%>
<h:panelGrid id="productViewPanelGrid" binding="#{productViewPage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{productViewPage.configurationProvider.name}" action="#{configurationProviderViewPage.show}" styleClass="command-link">
            <f:setPropertyActionListener value="#{productViewPage.configurationProvider}" target="#{selectedConfigurationProviderGroupPage.selectConfigurationProvider}" />
        </a4j:commandLink>

        <h:outputText escape="true" value="Группа продуктов" styleClass="output-text" />
        <a4j:commandLink reRender="mainMenu, workspaceForm" value="#{productViewPage.currentProduct.productGroup.nameOfGroup}" action="#{productGroupViewPage.show}" styleClass="command-link"
                       style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px;">
            <f:setPropertyActionListener value="#{productViewPage.currentProduct.productGroup}" target="#{selectedProductGroupGroupPage.currentProductGroup}" />
        </a4j:commandLink>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{productViewPage.currentProduct.deletedState}" readonly="true" disabled="true"/>

        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{productViewPage.currentProduct.code}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text" />
        <h:inputText value="#{productViewPage.currentProduct.fullName}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Товарное наименование" styleClass="output-text" />
        <h:inputText value="#{productViewPage.currentProduct.productName}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код (коды) ОКП" styleClass="output-text" />
        <h:inputText value="#{productViewPage.currentProduct.okpCode}" maxlength="128" readonly="true" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код классификации" styleClass="output-text" />
        <h:inputText value="#{productViewPage.currentProduct.classificationCode}" readonly="true" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="1">

        <a4j:commandButton value="Редактировать" action="#{productEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>
