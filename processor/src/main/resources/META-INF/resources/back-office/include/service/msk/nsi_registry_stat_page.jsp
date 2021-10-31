<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%--@elvariable id="NSIOrgsRegistryStatPage" type="ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgsRegistryStatPage"--%>

<h:panelGrid id="NSIRegistryStatPage" styleClass="borderless-grid" binding="#{NSIOrgsRegistryStatPage.pageComponent}">
    <a4j:status>
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" />
        </f:facet>
    </a4j:status>

    <h:panelGrid styleClass="borderless-grid" columns="1">
        <a4j:commandButton value="Обновить список" action="#{NSIOrgsRegistryStatPage.buildStatReport}"
                           reRender="nsiRegistryStatReportTable"
                           styleClass="commandButton" />
    </h:panelGrid>

    <h:panelGrid columns="2" styleClass="borderless-grid">
        <h:outputText escape="true" value="Дата сверки разногласий" styleClass="output-text" />
        <h:selectOneMenu id="revisionDates" value="#{NSIOrgsRegistryStatPage.selectedRevision}" style="width:350px;" >
            <f:selectItems value="#{NSIOrgsRegistryStatPage.revisions}"/>
        </h:selectOneMenu>
    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid">
        <h:outputText value="Статистика сверки с АИС НСИ и Реестры по зданиям" styleClass="output-text" />
        <rich:dataTable id="nsiRegistryStatReportTable" value="#{NSIOrgsRegistryStatPage.nsiRegistryStatReport.items}"
                        var="item" rowKeyVar="row" footerClass="data-table-footer">
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Округ" />
                </f:facet>
                <h:outputText value="#{item.district}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Создание (кол-во зданий)" />
                </f:facet>
                <h:outputText value="#{item.createOperation}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Изменение (кол-во зданий)" />
                </f:facet>
                <h:outputText value="#{item.changeOperation}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Отключение (кол-во зданий)" />
                </f:facet>
                <h:outputText value="#{item.removeOperation}" styleClass="output-text" />
            </rich:column>
            <rich:column>
                <f:facet name="header">
                    <h:outputText value="Итого (общее кол-во изменений)" />
                </f:facet>
                <h:outputText value="#{item.totalOperation}" styleClass="output-text" />
            </rich:column>
        </rich:dataTable>
    </h:panelGrid>

</h:panelGrid>
