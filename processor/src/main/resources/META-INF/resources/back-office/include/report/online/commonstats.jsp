<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<%--@elvariable id="commonStatsPage" type="ru.axetta.ecafe.processor.web.ui.report.online.CommonStatsPage"--%>
<h:panelGrid id="commonStatsPanelGrid" binding="#{commonStatsPage.pageComponent}" styleClass="borderless-grid">
    <a4j:status id="commonStatsPageStatus">
        <f:facet name="start">
            <h:graphicImage value="/images/gif/waiting.gif" alt="waiting"/>
        </f:facet>
    </a4j:status>
    <h:panelGrid styleClass="borderless-grid">
        <rich:dataTable id="commonStatsTable" value="#{commonStatsPage.statItems}"
                        var="item" rowKeyVar="row" rows="50" footerClass="data-table-footer">

            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Параметр"/>
                    </rich:column>
                    <rich:column headerClass="center-aligned-column" rowspan="2">
                        <h:outputText styleClass="column-header" escape="true" value="Значение"/>
                    </rich:column>
                </rich:columnGroup>
            </f:facet>
           <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.name}" />
            </rich:column>
           <rich:column styleClass="right-aligned-column">
                <h:outputText styleClass="output-text" value="#{item.value}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="commonStatsTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <a4j:commandButton value="Обновить" action="#{commonStatsPage.updateData}"
                           reRender="workspaceTogglePanel, commonStatsTable"
                           styleClass="command-button" />
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>