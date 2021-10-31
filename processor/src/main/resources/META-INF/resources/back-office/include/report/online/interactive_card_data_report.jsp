<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%--
  ~ Copyright (c) 2010. Axetta LLC. All Rights Reserved.
  --%>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>


<%--@elvariable id="interactiveCardDataReportPage" type="ru.axetta.ecafe.processor.web.ui.report.online.InteractiveCardDataReportPage"--%>
<h:panelGrid id="reportPanelGrid" binding="#{interactiveCardDataReportPage.pageComponent}" styleClass="borderless-grid">
    <h:panelGrid styleClass="borderless-grid" columns="2">
        <h:outputText styleClass="output-text" escape="true" value="Организация" />
        <h:panelGroup styleClass="borderless-div">
            <h:inputText value="#{interactiveCardDataReportPage.filter}" readonly="true"
                         styleClass="input-text long-field" style="margin-right: 2px;" />
            <a4j:commandButton value="..." action="#{mainPage.showOrgSelectPage}" reRender="modalOrgSelectorPanel"
                               oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgSelectorPanel')}.show();"
                               styleClass="command-link" style="width: 25px;" />
        </h:panelGroup>
        <h:panelGrid styleClass="borderless-grid" columns="2">
            <a4j:commandButton value="Генерировать отчет" action="#{interactiveCardDataReportPage.doGenerate}"
                               reRender="workspaceTogglePanel, reportPanel" styleClass="command-button"
                               status="reportGenerateStatus" />
            <h:commandButton value="Выгрузить в Excel" actionListener="#{interactiveCardDataReportPage.doGenerateXLS}"
                             styleClass="command-button" />
        </h:panelGrid>
        <a4j:status id="reportGenerateStatus">
            <f:facet name="start">
                <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
            </f:facet>
        </a4j:status>

    </h:panelGrid>
    <h:panelGrid styleClass="borderless-grid" id="reportPanel">
        <c:if test="${not empty interactiveCardDataReportPage.report && not empty interactiveCardDataReportPage.report.htmlReport}">


            <f:verbatim>
                <style type="text/css">
                    div.htmlReportContent :empty {
                        display: none;
                    }
                </style>
                <div class="htmlReportContent"> ${interactiveCardDataReportPage.report.htmlReport} </div>
            </f:verbatim>

            <h:outputText escape="true" value="Отчет по обороту электронных карт" styleClass="output-text" />

        </c:if>
    </h:panelGrid>
    <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                   warnClass="warn-messages" />
</h:panelGrid>