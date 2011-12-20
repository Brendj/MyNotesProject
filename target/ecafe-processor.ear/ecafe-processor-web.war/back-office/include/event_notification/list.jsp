<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Панель посмотра списка уведомлений о событиях --%>
<rich:dataTable id="notificationRuleTable" binding="#{mainPage.eventNotificationListPage.pageComponent}"
                value="#{mainPage.eventNotificationListPage.rules}" var="item" rows="20"
                columnClasses="left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <h:commandLink value="#{item.idOfReportHandleRule}" action="#{mainPage.showEventNotificationViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfEventNotification}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Название" />
        </f:facet>
        <h:commandLink value="#{item.ruleName}" action="#{mainPage.showEventNotificationViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfEventNotification}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Включено" />
        </f:facet>
        <h:selectBooleanCheckbox disabled="true" readonly="true" value="#{item.enabled}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тип события" />
        </f:facet>
        <h:outputText escape="true" value="#{item.notificationType}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Формат уведомления" />
        </f:facet>
        <h:outputText escape="true" value="#{item.documentFormat}" styleClass="output-text"
                      converter="reportFormatConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Тема письма" />
        </f:facet>
        <h:outputText escape="true" value="#{item.subject}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Список адресов" />
        </f:facet>
        <h:outputText escape="true" value="#{item.routeAddresses}" styleClass="output-text"
                      converter="addressListConverter" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Редактировать" />
        </f:facet>
        <h:commandLink action="#{mainPage.showEventNotificationEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.selectedIdOfEventNotification}" />
        </h:commandLink>
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Удалить" />
        </f:facet>
        <a4j:commandLink ajaxSingle="true" styleClass="command-link"
                         oncomplete="#{rich:component('eventNotificationDeletePanel')}.show()">
            <h:graphicImage value="/images/16x16/delete.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfReportHandleRule}"
                                         target="#{mainPage.removedIdOfEventNotification}" />
        </a4j:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="notificationRuleTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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