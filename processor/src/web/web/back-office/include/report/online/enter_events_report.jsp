<%--
  ~ Copyright (c) 2016. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h:panelGrid id="enterEventsReportPanelGrid" binding="#{electionsPage.pageComponent}"
             styleClass="borderless-grid">

    <h:panelGrid styleClass="borderless-grid" columns="2">

        <h:outputText escape="false" value="Показать только организации с УИК" styleClass="output-text" />
        <h:selectBooleanCheckbox value="#{electionsPage.enterEventsMonitoringReportPage.showElectionAreaOnly}" styleClass="output-text">
        </h:selectBooleanCheckbox>

    </h:panelGrid>

    <h:panelGrid styleClass="borderless-grid" columns="3" id="blah">
        <a4j:commandButton value="Обновить" action="#{electionsPage.enterEventsMonitoringReportPage.buildReportHTML}"
                           reRender="enterEventsReportTable"
                           styleClass="command-button" />
        <h:commandButton value="Выгрузить в Excel" actionListener="#{electionsPage.enterEventsMonitoringReportPage.exportToXLS}" styleClass="command-button" />
        <a4j:status>
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>
    </h:panelGrid>

    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
    <h:panelGrid styleClass="borderless-grid" id="enterEventsReportTable">
        <c:if test="${not empty electionsPage.enterEventsMonitoringReportPage.htmlReport}">
            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${electionsPage.enterEventsMonitoringReportPage.htmlReport} </div>
            </f:verbatim>
        </c:if>
    </h:panelGrid>
</h:panelGrid>