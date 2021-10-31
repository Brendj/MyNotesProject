<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productEditPage" type="ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.product.ProductEditPage"--%>
<h:panelGrid id="productEditPanelGrid" binding="#{productEditPage.pageComponent}" styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">

        <h:outputText escape="true" value="Производственная конфигурация" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{productEditPage.currentConfigurationProvider.name}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{productEditPage.selectConfigurationProvider}" reRender="configurationProviderSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('configurationProviderSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />

        </h:panelGroup>



        <h:outputText escape="true" value="Группа продуктов" styleClass="output-text required-field" />
        <h:panelGroup styleClass="borderless-div">
            <h:outputText value="#{productEditPage.currentProductGroup.nameOfGroup}" styleClass="output-text" style="margin-right: 2px; margin-top: 2px; width: 366px; min-height: 14px; float: left; padding: 3px; border: 1px groove #EEE; background-color: #ffffff;" />
            <a4j:commandButton value="..." action="#{productEditPage.selectProductGroup}" reRender="productGroupSelectModalPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('productGroupSelectModalPanel')}.show();"
                               styleClass="command-link" style="width: 25px; float: right;" />
        </h:panelGroup>

        <h:outputText escape="true" value="Статус" styleClass="output-text" />
        <h:selectOneListbox value="#{productEditPage.currentProduct.deletedState}" size="1">
            <f:selectItem itemLabel="Не удален" itemValue="false"/>
            <f:selectItem itemLabel="Удален" itemValue="true"/>
        </h:selectOneListbox>

        <h:outputText escape="true" value="Полное наименование пищевого продукта" styleClass="output-text required-field" />
        <h:inputText value="#{productEditPage.currentProduct.fullName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код" styleClass="output-text" />
        <h:inputText value="#{productEditPage.currentProduct.code}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Товарное название" styleClass="output-text" />
        <h:inputText value="#{productEditPage.currentProduct.productName}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код (коды) ОКП" styleClass="output-text" />
        <h:inputText value="#{productEditPage.currentProduct.okpCode}" maxlength="128" styleClass="input-text long-field" />
        <h:outputText escape="true" value="Код классификации" styleClass="output-text" />
        <h:inputText value="#{productEditPage.currentProduct.classificationCode}" maxlength="32" styleClass="input-text long-field" />
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Сохранить" action="#{productEditPage.onSave}"
                           reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />

        <a4j:commandButton value="Восстановить" action="#{productEditPage.show}"
                           reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>