<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="categoryOrgEditTable" binding="#{categoryOrgEditPage.pageComponent}"
             styleClass="borderless-grid" columns="2">

    <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />

    <h:panelGrid columns="2">
        <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}" reRender="modalOrgListSelectorPanel"
                           oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                           styleClass="command-link" style="width: 25px;" />
        <h:outputText styleClass="output-text" id="categoryOrgEditFilter" escape="true" value=" {#{categoryOrgEditPage.filter}}" />
    </h:panelGrid>

    <h:outputText escape="true" value="Имя категории" styleClass="output-text required-field" />
    <h:inputText value="#{categoryOrgEditPage.currCategoryOrg.categoryName}" styleClass="output-text"/>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
   <h:commandButton value="Сохранить" action="#{categoryOrgEditPage.save}" />
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>