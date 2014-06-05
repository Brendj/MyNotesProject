<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Frozen
  Date: 11.02.12
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h:panelGrid id="orgDiscountReportPanelGrid" binding="#{mainPage.orgDiscountsReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText escape="true" value="Организация" styleClass="output-text" />
        <h:panelGroup>
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;" />
            <h:outputText styleClass="output-text" escape="true" value=" {#{mainPage.orgDiscountsReportPage.filter}}" />
        </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:panelGroup>
        <a4j:commandButton value="Генерировать отчет" action="#{mainPage.buildOrgDiscountsReport}"
                           reRender="orgDiscountsReportTable"
                           styleClass="commandButton" status="sReportGeneratorStatus" />
        <a4j:status id="sReportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
            </f:facet>
        </a4j:status>
        </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid">
        <h:outputText escape="true" value="Отчет по учреждению " styleClass="output-text" />
        <rich:dataTable id="orgDiscountsReportTable" value="#{mainPage.orgDiscountsReportPage.orgDiscountsReport.itemList}"
                        var="item" rowKeyVar="row" rows="15" footerClass="data-table-footer">

                <f:facet name="header">
                    <rich:columnGroup>
                        <rich:column headerClass="center-aligned-column" colspan="2">
                            <h:outputText escape="true" value="Класс" styleClass="column-header" />
                        </rich:column>
                    </rich:columnGroup>
                </f:facet>

                <rich:column  colspan="2">
                    <h:outputText value="Класс #{item.name}"/>
                    <rich:subTable value="#{item.subItemList}" var="subItem">
                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="ФИО"/>
                            </f:facet>
                            <h:outputText escape="true" value="#{subItem.fio}"/>
                            <f:facet name="footer">
                                <h:outputText value="Итого: #{item.size}" styleClass="output-text"/>
                            </f:facet>
                        </rich:column>
                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Категории льгот"/>
                            </f:facet>
                            <h:outputText escape="true" value="#{subItem.categories}" styleClass="output-text"/>
                        </rich:column>
                    </rich:subTable>
                </rich:column>
                <%--<rich:column  colspan="2">--%>
                    <%--<h:outputText value="Итого: #{item.size}"/>--%>
                <%--</rich:column>--%>

            <f:facet name="footer">
                <h:outputText value="Итого по ОУ: #{mainPage.orgDiscountsReportPage.orgDiscountsReport.count}"/>
                <rich:datascroller for="orgDiscountsReportTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
</h:panelGrid>