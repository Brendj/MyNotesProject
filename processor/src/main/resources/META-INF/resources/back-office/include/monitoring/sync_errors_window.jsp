<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<rich:modalPanel id="syncErrorsWindow" autosized="true" headerClass="modal-panel-header" width="450">
    <rich:hotKey key="esc" handler="#{rich:component('syncErrorsWindow')}.hide();return false;"/>
    <f:facet name="header">
        <h:outputText escape="true" value="Просмотр зафиксированных ошибок" />
    </f:facet>
    <a4j:form id="syncErrorsModalForm" styleClass="borderless-form" eventsQueue="syncErrorsFormEventsQueue">
        <rich:dataTable id="syncErrorsTable" value="#{mainPage.statusSyncReportPage.currentStatusSync.syncErrors}"
                        var="syncError" rowKeyVar="row" rows="8" footerClass="data-table-footer">
            <f:facet name="header">
                <rich:columnGroup>
                    <rich:column headerClass="center-aligned-column">
                        <h:outputText styleClass="column-header" escape="true" value="№" />
                    </rich:column>
                        <rich:column headerClass="center-aligned-column" width="380">
                            <h:outputText styleClass="column-header" escape="true" value="Сообщение об ошибке" />
                        </rich:column>
                </rich:columnGroup>
            </f:facet>
            <rich:column styleClass="center-aligned-column">
                <h:outputText value="#{row + 1}" styleClass="output-text" />
            </rich:column>
            <rich:column styleClass="left-aligned-column">
                <h:outputText styleClass="output-text" value="#{syncError}" />
            </rich:column>
            <f:facet name="footer">
                <rich:datascroller for="syncErrorsTable" renderIfSinglePage="false" maxPages="10" fastControls="hide"
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
        <div style="text-align: center;">
            <a4j:commandButton value="Закрыть" styleClass="command-button"
                               onclick="#{rich:component('syncErrorsWindow')}.hide();return false;" />
        </div>
    </a4j:form>
</rich:modalPanel>