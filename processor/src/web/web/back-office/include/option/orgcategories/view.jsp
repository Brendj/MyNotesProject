<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="categoryOrgViewTable" binding="#{categoryOrgViewPage.pageComponent}"
                 styleClass="borderless-grid" columns="2">
    <h:outputText escape="true" value="Имя категории" styleClass="output-text" />
    <h:outputText escape="true" value="#{categoryOrgViewPage.currCategoryOrg.categoryName}" styleClass="output-text" />
    <h:outputText escape="true" value="Организаций" styleClass="output-text"/>

    <h:dataTable value="#{categoryOrgViewPage.names}" var="orgShortName">
        <h:column>
            <h:outputText value="#{orgShortName}"/>
        </h:column>
    </h:dataTable>


</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">

    <h:commandLink action="#{categoryOrgEditPage.show}" styleClass="command-link" value="Редактировать">
        <f:setPropertyActionListener
                value="#{categoryOrgViewPage.currCategoryOrg.idOfCategoryOrg}"
                target="#{categoryOrgEditPage.selectedIdOfCategoryOrg}" />
    </h:commandLink>


</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
  <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>
