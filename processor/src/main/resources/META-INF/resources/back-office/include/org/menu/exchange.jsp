<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  Created by IntelliJ IDEA.
  User: damir
  Date: 11.01.12
  Time: 12:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель создания меню --%>

<h:panelGrid id="menuExchangeExcept" rendered="#{empty mainPage.menuExchangePage.menuExchangeItemList}">
    <h:outputText value="Данных нет"/>
</h:panelGrid>
<rich:dataTable id="menuExchangeListTable" binding="#{mainPage.menuExchangePage.pageComponent}" value="#{mainPage.menuExchangePage.menuExchangeItemList}"
                var="menuExchange" rows="20" headerClass="page-header-text" footerClass="data-table-footer" rendered="#{not empty mainPage.menuExchangePage.menuExchangeItemList}"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">
    <f:facet name="header">
        <rich:columnGroup>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Дата меню" styleClass="output-text"  />
            </rich:column>

            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Действия" styleClass="output-text" />
            </rich:column>
        </rich:columnGroup>
    </f:facet>
    <rich:column styleClass="left-aligned-column">
        <h:outputText value="#{menuExchange.menuDate}" styleClass="output-text"  converter="timeConverter"/>
    </rich:column>
    <rich:column styleClass="left-aligned-column">
        <a4j:commandLink reRender="mainMenu, workspaceForm" action="#{mainPage.showMenuDataXML}" styleClass="command-link" value="Просмотреть" target="_blink">
            <f:setPropertyActionListener value="#{menuExchange.menuData}" target="#{mainPage.selectedMenuDataXML}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="menuExchangeListTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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


