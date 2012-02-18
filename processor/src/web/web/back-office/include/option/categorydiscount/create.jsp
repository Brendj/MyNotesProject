<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCategory())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания категории --%>
<h:panelGrid id="categoryCreatePanel" binding="#{categoryDiscountCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Идентификатор" styleClass="output-text required-field" />
    <h:inputText value="#{categoryDiscountCreatePage.idOfCategoryDiscount}" maxlength="32" styleClass="input-text" />

    <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
    <h:inputText value="#{categoryDiscountCreatePage.categoryName}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{categoryDiscountCreatePage.description}" maxlength="32" styleClass="input-text" />

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать категорию" action="#{categoryDiscountCreatePage.createCategory}"
                       reRender="categoryCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>