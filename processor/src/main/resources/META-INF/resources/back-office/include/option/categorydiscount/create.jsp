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
<%--@elvariable id="categoryDiscountCreatePage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountCreatePage"--%>
<h:panelGrid id="categoryCreatePanel" binding="#{categoryDiscountCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
    <h:inputText value="#{categoryDiscountCreatePage.categoryName}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{categoryDiscountCreatePage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text" />
    <h:panelGrid columns="2">
        <h:inputText value="#{categoryDiscountCreatePage.discountRate}" maxlength="3" styleClass="input-text" />
        <h:outputText escape="true" value="%" styleClass="output-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Тип категории" styleClass="output-text" />
    <h:selectOneMenu value="#{categoryDiscountCreatePage.categoryType}" styleClass="input-text">
        <f:selectItems value="#{categoryDiscountCreatePage.categoryDiscountEnumTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:selectOneMenu value="#{categoryDiscountCreatePage.organizationType}" styleClass="input-text">
        <f:selectItems value="#{categoryDiscountCreatePage.organizationItems}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Запрет изменения в АРМ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{categoryDiscountCreatePage.blockedToChange}" styleClass="output-text"/>
    <h:outputText escape="true" value="Удалять при переводе" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{categoryDiscountCreatePage.eligibleToDelete}" styleClass="output-text"/>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать категорию" action="#{categoryDiscountCreatePage.onSave}"
                       reRender="categoryCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>