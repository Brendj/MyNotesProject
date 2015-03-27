<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid id="empInfoPageControls" binding="#{mainPage.empInfoPage.pageComponent}" >
    <h:panelGrid styleClass="borderless-grid" columns="1">

        <h:outputText escape="true" value="Введите номер мобильного для получения данных из ЕМП:" styleClass="output-text" />
        <h:inputText value="#{mainPage.empInfoPage.clientMobile}" styleClass="input-text" size="15" />
        <rich:spacer />
        <a4j:commandButton value="Запросить данные" action="#{mainPage.empInfoPage.populateEntryAttributes}" reRender="empInfoDataTable"/>
        <rich:spacer />
        <a4j:status id="sEmpInfoStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
        <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                       warnClass="warn-messages" />
    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" columns="1">

        <rich:dataTable id="empInfoDataTable" value="#{mainPage.empInfoPage.result}" var="line" rowKeyVar="row" rows="50"
                        footerClass="data-table-footer"
                        columnClasses="right-aligned-column, left-aligned-column, left-aligned-column, right-aligned-column, left-aligned-column, center-aligned-column"
                reRender="workspaceForm">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Параметр" />
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="1">
                        <h:outputText styleClass="column-header" escape="true" value="Значение" />
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.name}" />
            </rich:column>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{line.value}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="empInfoDataTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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