<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 11.02.12
  Time: 17:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="orgsDiscountReportPanelGrid" binding="#{mainPage.allOrgsDiscountsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="true" value="Округ" styleClass="output-text" />
        <h:selectOneMenu id="regionsList" value="#{mainPage.allOrgsDiscountsReportPage.region}" style="width:100px;" >
            <f:selectItems value="#{mainPage.allOrgsDiscountsReportPage.regions}"/>
        </h:selectOneMenu>

        <h:outputText escape="false" value="Показать все организации" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{mainPage.allOrgsDiscountsReportPage.showAllOrgs}"
                                 styleClass="output-text">
        </h:selectBooleanCheckbox>

        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildAllOrgsDiscountsReport}"
                           reRender="workspaceTogglePanel, orgsDiscountsReportTable, testPanelGrid"
                           styleClass="commandButton" status="sReportGenerateStatus" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <a4j:status id="sReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <%-- не показывать пустую таблицу --%>
        <c:if test="${!empty mainPage.allOrgsDiscountsReportPage.allOrgsDiscountsReport.itemsList}" >
        <h:outputText value="Отчет по льготам организаций" styleClass="output-text" />
            <rich:dataTable id="orgsDiscountsReportTable" value="#{mainPage.allOrgsDiscountsReportPage.allOrgsDiscountsReport.itemsList}"
                        var="item" rowKeyVar="row" rows="20" footerClass="data-table-footer">

                    <rich:column styleClass="center-aligned-column">
                        <f:facet name="header">
                            <h:outputText value="№" />
                        </f:facet>
                        <h:outputText value="#{row + 1}" styleClass="output-text" />
                    </rich:column>

                <c:if test="${item.values == null}">
                        <rich:columns value="#{mainPage.allOrgsDiscountsReportPage.allOrgsDiscountsReport.columnNames}"
                                      var="value" index="ind" headerClass="center-aligned-column" >
                            <f:facet name="header" >
                                <h:outputText escape="true" value="#{value}" />
                            </f:facet>
                            <h:outputText escape="true" value="#{item.values[ind]}" styleClass="output-text" />
                        </rich:columns>
                </c:if>

                <f:facet name="footer">
                    <rich:datascroller for="orgsDiscountsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
            <h:outputText value="Итого: #{mainPage.allOrgsDiscountsReportPage.allOrgsDiscountsReport.count}" escape="true" styleClass="output-text"/>

            <%--<h:commandButton value="Выгрузить в SCV" action="#{mainPage.showArgOrgsDiscountsCSVList}"--%>
                         <%--styleClass="command-button" />--%>

        </c:if>
    </h:panelGrid>
</h:panelGrid>