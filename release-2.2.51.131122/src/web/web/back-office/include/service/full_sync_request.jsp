<%--
  ~ Copyright (c) 2013. Axetta LLC. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<% if (!ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditOrgs()) {
    out.println("Недостаточно прав для просмотра страницы");
    return;
} %>

<%--@elvariable id="fullSyncRequestPage" type="ru.axetta.ecafe.processor.web.ui.service.FullSyncRequestPage"--%>
<h:panelGrid id="fullSyncRequestPage" binding="#{fullSyncRequestPage.pageComponent}" styleClass="borderless-grid"
             columns="2">
    <rich:panel>
        <f:facet name="header"><h:outputText styleClass="column-header" value="Запрос полной синхронизации" /></f:facet>
        <h:panelGrid columns="2" styleClass="borderless-grid">
            <h:outputText escape="true" value="Организации" styleClass="output-text required-field" />
            <h:panelGrid columns="2">
                <a4j:commandButton value="..." action="#{mainPage.showOrgListSelectPage}"
                                   reRender="modalOrgListSelectorPanel"
                                   oncomplete="if (#{facesContext.maximumSeverity == null}) #{rich:component('modalOrgListSelectorPanel')}.show();"
                                   styleClass="command-link" style="width: 25px;">
                    <f:setPropertyActionListener value="#{fullSyncRequestPage.idOfOrgList}"
                                                 target="#{mainPage.orgFilterOfSelectOrgListSelectPage}" />
                </a4j:commandButton>
                <h:outputText styleClass="output-text" id="fullSyncRequestPageFilter" escape="true"
                              value=" {#{fullSyncRequestPage.filter}}" />
            </h:panelGrid>
            <a4j:commandButton value="Запросить" action="#{fullSyncRequestPage.applyFullSyncOperation}" />
            <rich:spacer />
            <h:panelGrid styleClass="borderless-grid">
                <a4j:status id="sFullSyncRequestStatus">
                    <f:facet name="start">
                        <h:graphicImage value="/images/gif/waiting.gif" alt="waiting" />
                    </f:facet>
                </a4j:status>
                <rich:messages styleClass="messages" errorClass="error-messages" infoClass="info-messages"
                               warnClass="warn-messages" />
            </h:panelGrid>
        </h:panelGrid>
    </rich:panel>
</h:panelGrid>
