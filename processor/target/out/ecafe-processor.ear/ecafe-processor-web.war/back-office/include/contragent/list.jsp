<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%-- Список контрагентов --%>
<rich:dataTable id="contragentTable" value="#{mainPage.contragentListPage.items}" var="item" rows="20"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, center-aligned-column"
                footerClass="data-table-footer">
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Идентификатор" />
        </f:facet>
        <h:outputText escape="true" value="#{item.idOfContragent}" styleClass="output-text" />
    </rich:column>
    <rich:column headerClass="column-header">
        <f:facet name="header">
            <h:outputText escape="true" value="Контрагент" />
        </f:facet>
        <h:commandLink value="#{item.contragentName}" action="#{mainPage.showContragentViewPage}"
                       styleClass="command-link">
            <f:setPropertyActionListener value="#{item.idOfContragent}" target="#{mainPage.selectedIdOfContragent}" />
        </h:commandLink>
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
        <h:commandLink action="#{mainPage.showContragentEditPage}" styleClass="command-link">
            <h:graphicImage value="/images/16x16/edit.png" style="border: 0;" />
            <f:setPropertyActionListener value="#{item.idOfContragent}" target="#{mainPage.selectedIdOfContragent}" />
        </h:commandLink>
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="contragentTable" renderIfSinglePage="false" maxPages="5" fastControls="hide"
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