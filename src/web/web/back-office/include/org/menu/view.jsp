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



<rich:dataTable id="menuListTable" binding="#{mainPage.menuViewPage.pageComponent}" value="#{mainPage.menuViewPage.items}"
                        var="item" rows="20" footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Идентификатор" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Дата создания" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Дата меню" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Menu Source" />
                    </rich:column>
                    <rich:column styleClass="center-aligned-column" rowspan="2">
                        <h:outputText escape="true" value="Flag" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{item.idOfMenu}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText value="#{item.createTime}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText value="#{item.menuDate}" styleClass="output-text" converter="timeConverter"/>
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{item.menuSource}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText escape="true" value="#{item.flag}" styleClass="output-text" />
            </rich:column>
    <f:facet name="footer">
            <rich:datascroller for="menuListTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
<h:commandButton value="Выгрузить в SCV" action="#{mainPage.showMenuToXML}"
                 styleClass="command-button" />