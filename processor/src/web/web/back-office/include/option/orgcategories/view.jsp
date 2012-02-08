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

    <rich:dataTable value="#{categoryOrgViewPage.orgList}" var="org" rowKeyVar="row">
        <rich:column>
            <h:outputText value="#{row+1}) #{org.shortName}" escape="true" styleClass="output-text"/>
        </rich:column>
    </rich:dataTable>

</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
    <h:commandButton action="#{categoryOrgEditPage.show}" styleClass="command-button" value="Редактировать">
        <f:setPropertyActionListener value="#{categoryOrgViewPage.currCategoryOrg}" target="#{categoryOrgEditPage.currCategoryOrg}" />
    </h:commandButton>
</h:panelGrid>

<h:panelGrid styleClass="borderless-grid">
  <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages" warnClass="warn-messages" />
</h:panelGrid>
