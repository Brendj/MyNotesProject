<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель просмотра списка пользователей --%>
<rich:dataTable id="userListTable" binding="#{mainPage.userListPage.pageComponent}"
                value="#{mainPage.userListPage.items}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Пользователь" />
        </f:facet>
        <h:commandLink value="#{item.userName}" action="#{mainPage.showUserViewPage}" styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Права" />
        </f:facet>
        <h:outputText escape="true" value="#{item.functions}" converter="functionSetConverter"
                      styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Последние изменения" />
        </f:facet>
        <h:outputText escape="true" value="#{item.updateTime}" converter="timeConverter" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <h:commandLink action="#{mainPage.showUserEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.selectedIdOfUser}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header" rendered="#{mainPage.eligibleToDeleteUsers}">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <h:commandLink action="#{mainPage.removeUser}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfUser}" target="#{mainPage.removedIdOfUser}" />
        </h:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="userListTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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