<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCategory())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования категории --%>
<%--@elvariable id="categoryDiscountEditPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage"--%>
<h:panelGrid id="categoryEditPanel" binding="#{categoryDiscountEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование" styleClass="output-text required-field" />
    <h:inputText value="#{categoryDiscountEditPage.categoryName}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{categoryDiscountEditPage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Ставка дисконтирования" styleClass="output-text" />
    <h:panelGrid columns="2">
        <h:inputText value="#{categoryDiscountEditPage.discountRate}" maxlength="3" styleClass="input-text" />
        <h:outputText escape="true" value="%" styleClass="output-text" />
    </h:panelGrid>
    <h:outputText escape="true" value="Тип категории" styleClass="output-text" />
    <h:selectOneMenu value="#{categoryDiscountEditPage.categoryType}" styleClass="input-text">
        <f:selectItems value="#{categoryDiscountEditPage.categoryDiscountEnumTypeMenu.items}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Тип организации" styleClass="output-text" />
    <h:selectOneMenu value="#{categoryDiscountEditPage.organizationType}" styleClass="input-text">
        <f:selectItems value="#{categoryDiscountEditPage.organizationItems}" />
    </h:selectOneMenu>
    <h:outputText escape="true" value="Запрет изменения в АРМ" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{categoryDiscountEditPage.blockedToChange}" styleClass="output-text"/>
    <h:outputText escape="true" value="Удалять при переводе" styleClass="output-text" />
    <h:selectBooleanCheckbox value="#{categoryDiscountEditPage.eligibleToDelete}" styleClass="output-text"/>
    <h:outputText escape="true" value="Используется в правилах" styleClass="output-text" />
    <h:panelGroup>
        <h:outputText styleClass="output-text" id="ruleListFilter" escape="true" value=" {#{categoryDiscountEditPage.filter}}" />
    </h:panelGroup>

</h:panelGrid>
<h:panelGrid styleClass="borderless-grid" rendered="#{categoryDiscountEditPage.deletedState}">
    <h:outputText escape="true" value="Льгота удалена (в архиве)" styleClass="output-text-strong" />
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{categoryDiscountEditPage.save}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" disabled="#{categoryDiscountEditPage.deletedState}" />
    <a4j:commandButton value="Восстановить" action="#{categoryDiscountEditPage.reload}" disabled="#{categoryDiscountEditPage.deletedState}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>