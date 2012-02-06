<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:outputText value="Create page" />

<h:panelGrid id="createCategoryOrgPanelGrid" binding="#{categoryOrgCreatePage.pageComponent}" styleClass="borderless-grid">

    <h:outputText styleClass="output-text" escape="true" value="Организация" />

    <h:panelGroup>
        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
        <h:outputText styleClass="output-text" id="categoryOrgCreatePageFilter" escape="true" value=" {#{categoryOrgCreatePage.filter}}" />
    </h:panelGroup>

    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Имя категории" styleClass="output-text" />
        <h:inputText value="#{categoryOrgCreatePage.categoryOrg.categoryName}" styleClass="output-text" />
   </h:panelGrid>

    <h:panelGroup style="margin-top: 10px">
        <a4j:commandButton value="Сохранить" action="#{categoryOrgCreatePage.save}"
                           reRender="mainMenu, workspaceTogglePanel, createCategoryOrgPanelGrid"
                           styleClass="command-button" />
        <a4j:commandButton value="Отмена" action="#{categoryOrgCreatePage.cancel}"
                           reRender="mainMenu, workspaceTogglePanel, createCategoryOrgPanelGrid"
                           styleClass="command-button" />
    </h:panelGroup>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>


