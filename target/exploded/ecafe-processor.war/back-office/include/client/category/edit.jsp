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
<h:panelGrid id="categoryEditPanel" binding="#{mainPage.categoryEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Наименование" styleClass="output-text" />
    <h:inputText value="#{mainPage.categoryEditPage.categoryName}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Описание" styleClass="output-text" />
    <h:inputText value="#{mainPage.categoryEditPage.description}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Дата создания" styleClass="output-text" />
    <rich:calendar value="#{mainPage.categoryEditPage.createdDate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
    <h:outputText escape="true" value="Дата последнего обновления" styleClass="output-text" />
    <rich:calendar value="#{mainPage.categoryEditPage.lastUpdate}" datePattern="dd.MM.yyyy" converter="dateConverter"
                   inputClass="input-text" showWeeksBar="false" />
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{mainPage.updateCategory}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{mainPage.showCategoryEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>