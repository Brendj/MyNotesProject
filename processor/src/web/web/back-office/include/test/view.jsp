<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра организации --%>
<h:panelGrid id="testViewGrid" binding="#{mainPage.testViewPage.pageComponent}" styleClass="borderless-grid" columns="2">
    <rich:dataTable id="testViewTable" value="#{mainPage.testViewPage.wideFieldKeys}" var="key" rows="20"
                    footerClass="data-table-footer"
                    columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Наименование" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="#{key}" styleClass="output-text"/>
        </rich:column>

        <rich:column headerClass="column-header">
            <f:facet name="header">
                <h:outputText escape="true" value="Значение" styleClass="output-text"/>
            </f:facet>
            <h:outputText escape="true" value="#{mainPage.testViewPage.wideFields[key]}" />
        </rich:column>

        <f:facet name="footer">
            <rich:datascroller for="testViewTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
                               stepControls="auto" boundaryControls="hide">
                <f:facet name="previous">
                    <h:graphicImage value="/images/16x16/left-arrow.png" />
                </f:facet>
                <f:facet name="next">
                    <h:graphicImage value="/images/16x16/right-arrow.png" />
                </f:facet>
            </rich:datascroller>
        </f:facet>
    </rich:dataTable>
</h:panelGrid>
<h:panelGrid styleClass="borderless-grid">
    <a4j:commandButton value="Редактировать" action="#{mainPage.showTestEditPage}"
                       reRender="mainMenu, workspaceTogglePanel" styleClass="command-button" />
</h:panelGrid>