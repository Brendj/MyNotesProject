<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания правила --%>
<%--@elvariable id="productGroupCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.product.group.ProductGroupCreatePage"--%>
<h:panelGrid id="productGroupCreateCreatePanelGrid" binding="#{productGroupCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="1">
    <h:panelGrid columns="2">
        <h:outputText escape="true" value="Наименование группы" styleClass="output-text" />
        <h:inputText value="#{productGroupCreatePage.productGroup.nameOfGroup}" maxlength="128" styleClass="input-text long-field" />
    </h:panelGrid>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Создать группу" action="#{productGroupCreatePage.onSave}"
                       reRender="technologicalMapGroupCreatePanelGrid" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />

</h:panelGrid>