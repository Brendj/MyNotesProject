<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<h:panelGrid id="productCreatePanel" binding="#{mainPage.productCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Наименование продукта" styleClass="output-text" />
    <h:inputText value="#{mainPage.productCreatePage.nameOfProduct}" maxlength="256" styleClass="input-text long-field" />

    <h:outputText escape="true" value="Массва брутто (г)" styleClass="output-text" />
    <h:inputText value="#{mainPage.productCreatePage.grossMass}" maxlength="32" styleClass="input-text"
            validatorMessage="Масса брутто должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
    </h:inputText>

    <h:outputText escape="true" value="Массва нетто (г)" styleClass="output-text" />
    <h:inputText value="#{mainPage.productCreatePage.netMass}" maxlength="32" styleClass="input-text"
            validatorMessage="Масса нетто должно быть числом.">
            <f:validateDoubleRange minimum="0.00" maximum="99999999.00" />
    </h:inputText>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать продукт" action="#{mainPage.createProduct}"
                       reRender="productCreatePanel" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>