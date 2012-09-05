<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка пользователей --%>
    <rich:dataTable id="cityListTable" binding="#{mainPage.cityListPage.pageComponent}"
                value="#{mainPage.cityListPage.items}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Город" />
        </f:facet>
        <h:commandLink value="#{item.name}" action="#{mainPage.showCityViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfCity}" target="#{mainPage.selectedIdOfCity}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Активность" />
        </f:facet>
        <h:outputText escape="true" value="#{item.activity}"
                      styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип авторизации" />
        </f:facet>
        <h:outputText escape="true" value="#{item.authorizationType}"  styleClass="output-text" />
    </rich:column>

    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <h:commandLink action="#{mainPage.showCityEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfCity}" target="#{mainPage.selectedIdOfCity}" />
        </h:commandLink>
    </rich:column>

    <rich:column headerClass="column-header" rendered="true">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <h:commandLink action="#{mainPage.removeCity}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfCity}" target="#{mainPage.removedIdOfCity}" />
        </h:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="cityListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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