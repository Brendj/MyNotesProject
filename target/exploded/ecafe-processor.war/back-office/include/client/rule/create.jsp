<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2011. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditRule())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель создания правила --%>
<h:panelGrid id="ruleCreatePanel" binding="#{mainPage.ruleCreatePage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Категория" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{mainPage.ruleCreatePage.category.categoryName}" readonly="true"
                     styleClass="input-text" style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showCategorySelectPage}"
                           reRender="modalCategorySelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategorySelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;">
        </a4j:commandButton>
    </h:panelGroup>
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 0" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex0}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 1" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex1}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 2" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex2}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 3" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex3}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 4" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex4}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 5" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex5}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 6" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex6}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 7" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex7}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 8" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex8}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплекс 9" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complex9}" maxlength="11" styleClass="input-text" />
    <h:outputText escape="true" value="Комплексы" styleClass="output-text" />
    <h:inputText value="#{mainPage.ruleCreatePage.complexes}" maxlength="11" styleClass="input-text" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Зарегистрировать правило" action="#{mainPage.createRule}"
                       reRender="ruleCreatePanel" styleClass="command-button" />
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>