<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2017. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditCategory())
      { out.println("Недостаточно прав для просмотра страницы"); return; } %>

<%-- Панель редактирования категории --%>
<%--@elvariable id="categoryDiscountDSZNEditPage" type="ru.axetta.ecafe.processor.web.ui.option.categorydiscountdszn.CategoryDiscountDSZNEditPage"--%>
<h:panelGrid id="categoryDSZNEditPanel" binding="#{categoryDiscountDSZNEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Код льготы ДТиСЗН" styleClass="output-text required-field" />
    <h:inputText value="#{categoryDiscountDSZNEditPage.code}" styleClass="input-text" maxlength="32" />
    <h:outputText escape="true" value="Описание льготы ДТиСЗН" styleClass="output-text" />
    <h:inputTextarea value="#{categoryDiscountDSZNEditPage.description}" styleClass="input-text" rows="5" cols="64" />
    <h:outputText escape="true" value="Код льготы ЕТП" styleClass="output-text" />
    <h:inputText value="#{categoryDiscountDSZNEditPage.ETPCode}" maxlength="32" styleClass="input-text" />
    <h:outputText escape="true" value="Льгота ИСПП" styleClass="output-text" />
    <h:panelGroup styleClass="borderless-div">
        <h:inputText value="#{categoryDiscountDSZNEditPage.categoryName}" readonly="true" styleClass="input-text"
                     style="margin-right: 2px;" />
        <a4j:commandButton value="..." action="#{mainPage.showCategorySelectPage}" reRender="modalCategorySelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalCategorySelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" >
        </a4j:commandButton>
    </h:panelGroup>
</h:panelGrid>
<h:panelGrid columns="4" styleClass="borderless-grid">
    <a4j:commandButton value="Сохранить" action="#{categoryDiscountDSZNEditPage.save}" reRender="mainMenu, workspaceTogglePanel"
                       styleClass="command-button" />
    <a4j:commandButton value="Восстановить" action="#{categoryDiscountDSZNEditPage.reload}"
                       reRender="mainMenu, workspaceTogglePanel" ajaxSingle="true" styleClass="command-button" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>