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
<h:outputText value="#{mainPage.menuDetailsPage.count}"/>


<rich:dataTable id="menuDetailsListTable" binding="#{mainPage.menuDetailsPage.pageComponent}" value="#{mainPage.menuDetailsPage.menuDetailItems}"
                var="menuDetail" rows="20" footerClass="data-table-footer"
                columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">

    <f:facet name="header">
        <rich:columnGroup>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Идентификатор" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Имя" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Группа" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="вывод" />
            </rich:column>
            <rich:column styleClass="center-aligned-column" rowspan="2">
                <h:outputText escape="true" value="Цена" />
            </rich:column>
        </rich:columnGroup>
    </f:facet>
    <rich:column styleClass="left-aligned-column">
        <h:outputText escape="true" value="#{menuDetail.idOfMenuDetail}" styleClass="output-text" />
    </rich:column>
    <rich:column styleClass="left-aligned-column">
        <h:outputText value="#{menuDetail.menuDetailName}" styleClass="output-text" />
    </rich:column>
    <rich:column styleClass="left-aligned-column">
        <h:outputText escape="true" value="#{menuDetail.groupName}" styleClass="output-text" />
    </rich:column>
    <rich:column styleClass="left-aligned-column">
        <h:outputText value="#{menuDetail.menuDetailOutput}" styleClass="output-text" />
    </rich:column>
    <rich:column styleClass="left-aligned-column">
        <h:outputText value="#{menuDetail.price}" styleClass="output-text" />
    </rich:column>
    <f:facet name="footer">
        <rich:datascroller for="menuDetailsListTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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


